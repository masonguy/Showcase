
#include "conf.h"
#include "hash.h"
#include <arpa/inet.h>
#include <fcntl.h>
#include <ifaddrs.h>
#include <net/if.h>
#include <netdb.h>
#include <netinet/in.h>
#include <netinet6/in6.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>
#include <assert.h>
#include <errno.h>

/* Constants */
#define STR1(x)   #x
#define STR(x)    STR1(x)
#define DEVICE    "device"
#define PORT      "port"
#define BROADCAST "broadcast"
#define ANYIF     "0.0.0.0"
#define ANYPORT   "0"
#define KEYSIZE 6
#define HEADERSIZE 40

/* Prototypes */
typedef struct Peer {
  char* host;
  struct addrinfo info;
} Peer;


typedef struct TCPKey {
  unsigned char remoteAddr[16];
  uint16_t localPort;
  uint16_t remotePort;
} TCPKey;

typedef struct Frame {
    uint8_t dst[KEYSIZE];
    uint8_t src[KEYSIZE];
    uint16_t type;
    uint8_t data[1500];
} Frame;

typedef struct Header {
    uint32_t vers         : 4;
    uint32_t trafficClass : 8;
    uint32_t flow         : 20;
    uint16_t length;
    uint8_t  nextHeader;
    uint8_t  hop;
    unsigned char srcAddr[16];
    unsigned char destAddr[16];
    uint8_t data[1460];
} Header;

typedef struct Segment {
    uint16_t srcPort;
    uint16_t destPort;
    uint32_t sequenceNum;
    uint32_t ackNum;

    uint16_t            : 4;
    uint16_t headerSize : 4;
    uint16_t FIN        : 1;
    uint16_t SYN        : 1;
    uint16_t RST        : 1;
    uint16_t PSH        : 1;
    uint16_t ACK        : 1;
    uint16_t URG        : 1;
    uint16_t            : 2;

    uint16_t window;
    uint16_t checkSum;
    uint16_t urgent;
    uint32_t options[];
} Segment;

/* Globals  */
static char* conffile   = STR(SYSCONFDIR) "/wfw.cfg";
static bool printusage = false;
static bool foreground = false;
static Peer peerArray[2];

/* Parse Options
 * argc, argv   The command line
 * returns      true iff the command line is successfully parsed
 *
 * This function sets the otherwise immutable global variables (above).
 */
static
bool parseoptions(int argc, char* argv[]);

/* Usage
 * cmd   The name by which this program was invoked
 * file  The steam to which the usage statement is printed
 *
 * This function prints the simple usage statement.  This is typically invoked
 * if the user provides -h on the command line or the options don't parse.
 */
static
void usage(char* cmd, FILE* file);

/* Ensure Tap
 * path     The full path to the tap device.
 * returns  If this function returns, it is the file descriptor for the tap
 *          device.
 *
 * This function tires to open the specified device for reading and writing.  If
 * that open fails, this function will report the error to stderr and exit the
 * program.
 */
static
int  ensuretap(char* path);

/* Ensure Socket
 * localaddress   The IPv4 address to bind this socket to.
 * port           The port number to bind this socket to.
 *
 * This function creates a bound socket.  Notice that both the local address and
 * the port number are strings.
 */
static
int ensuresocket(char* localaddr, char* port);

/* Make Socket Address
 * address, port  The string representation of an IPv4 socket address.
 *
 * This is a convince routine to convert an address-port pair to an IPv4 socket
 * address.
 */
static
struct sockaddr_in makesockaddr(char* address, char* port);

/* mkfdset
 * set    The fd_set to populate
 * ...    A list of file descriptors terminated with a zero.
 *
 * This function will clear the fd_set then populate it with the specified file
 * descriptors.
 */
static
int mkfdset(fd_set* set, ...);

/* Bridge
 * tap     The local tap device
 * in      The network socket that receives broadcast packets.
 * out     The network socket on with to send broadcast packets.
 * bcaddr  The broadcast address for the virtual ethernet link.
 *
 * This is the main loop for wfw.  Data from the tap is broadcast on the
 * socket.  Data broadcast on the socket is written to the tap.
 */
static
void bridge(int tap, int in, int out, int peer, struct sockaddr_in bcaddr, struct sockaddr_in peerAddr);

 //checks if the packet is TCP, if it is check if it is inside our hashtable
 //returns true if in hashtable
static
bool foreignTCPCheck(Frame buffer, hashtable* table, hashtable* blacklist, int peer);

//check if this is a TCP packet
// returns true if it is
static
bool isTCPcon(Frame buffer);

//add TCP to connection hashmap, as we want to allow communication
static
void addTCPtoMap(Frame buffer, hashtable* table);

//attempts to establish connection with a peer
static
int tryconnect(struct addrinfo* ai);

//adds MAC addresses to map that we want to communciate with
static
void handleMap(uint8_t* source, hashtable* ht, struct sockaddr_in* from);

//helper function to htfree in hash.c
static
void keyfree(void* key, void* value);

//compares keys in MAC address map
static
int compKey(void* key, void* comp);

//compares values with correct length of blacklist key
static
int compKeyBList(void* key, void* comp);

//check if address is a Broadcast address
//returns true if it is
static
bool isBCast(uint8_t* check);

//checks if address is a 0x33 0x33
//returns true if it is
static
bool isMCast(uint8_t* check);

//checks if the packet is an IPv6 Packet
//returns true if it is
static
bool isIPv6(uint16_t type);

//checks if the segment is a TCP Segment
//returns true if it is
static
bool isTCPSegment(uint8_t* type);

//returns true if the SYN bit is set in the segment
static
bool isSYNset(Frame buffer);

static
int ensurePeerSocket(char* address, char* port, struct sockaddr_in addr);

//checks if the src addr is in the blacklist
static
bool blackListCheck(hashtable* table, Frame buffer);

//this function creates the addr info from the peer that will store in the peer array,
//this way we can send blacklist info
static
struct addrinfo connectToPeer(const char* name, const char* svc);

//send the blacklist key to my peers
static
void sendPeerKey(unsigned char* key, int peer);

//compare key for values in the Ipv6 map
static
int compKeyIPv6(void* key, void* comp);
/* daemonize
*/
static
void daemonize(hashtable conf);

/* Main
 *
 * Mostly, main parses the command line, the conf file, creates the necessary
 * structures and then calls bridge.  Bridge is where the real work is done.
 */
int main(int argc, char* argv[]) {
  int result = EXIT_SUCCESS;

  if(!parseoptions(argc, argv)) {
    usage(argv[0], stderr);
    result = EXIT_FAILURE;
  }
  else if(printusage) {
    usage(argv[0], stdout);
  }
  else {
    hashtable conf = readconf (conffile);
    int       tap  = ensuretap (htstrfind (conf, DEVICE));
    int       out  = ensuresocket(ANYIF, ANYPORT);
    int       in   = ensuresocket(htstrfind (conf, BROADCAST),
                                  htstrfind (conf, PORT));
    struct sockaddr_in
      bcaddr       = makesockaddr (htstrfind (conf,BROADCAST),
                               htstrfind (conf, PORT));

    char* peerport = htstrfind(conf, "peerport");
    peerArray[0].host = htstrfind(conf, "peerone");
    peerArray[1].host = htstrfind(conf, "peertwo");
    peerArray[0].info = connectToPeer(peerArray[0].host, peerport);
    peerArray[1].info = connectToPeer(peerArray[1].host, peerport);
    struct sockaddr_in peerAddr = makesockaddr(htstrfind(conf, "ipv4"), peerport);
    int peerSocket = ensurePeerSocket(htstrfind(conf, "ipv4"), peerport, peerAddr);
    if(!foreground) {
      daemonize(conf);
    }



    bridge(tap, in, out, peerSocket, bcaddr, peerAddr);

    close(in);
    close(out);
    close(tap);
    htfree(conf);
  }

  return result;
}

int ensurePeerSocket(char* address, char* port, struct sockaddr_in addr) {
  int s = socket(PF_INET, SOCK_STREAM, 0);
  if(-1 == bind(s, (struct sockaddr*)&addr, sizeof(addr))) {
    perror("bind");
    close(s);
    exit(EXIT_FAILURE);
  }
  if(-1 == listen(s, 5)) {
    perror("listen");
    close(s);
    exit(EXIT_FAILURE);
  }
  return s;
}


/* Parse Options
 *
 * see man 3 getopt
 */
static
bool parseoptions(int argc, char* argv[]) {
  static const char* OPTS = "hc:f";

  bool parsed = true;

  char c = getopt(argc, argv, OPTS);
  while(c != -1) {
    switch (c) {
    case 'c':
      conffile = optarg;
      break;

    case 'f':
      foreground = true;
      break;

    case 'h':
      printusage = true;
      break;

    case '?':
      parsed = false;
      break;
    }

    c = parsed ? getopt(argc, argv, OPTS) : -1;
  }

  if(parsed) {
    argc -= optind;
    argv += optind;
  }

  return parsed;
}

/* Print Usage Statement
 *
 */

static
void usage(char* cmd, FILE* file) {
  fprintf(file, "Usage: %s -c file.cfg [-h]\n", cmd);
}

/* Ensure Tap device is open.
 *
 */
static
int ensuretap(char* path) {
  int fd = open(path, O_RDWR | O_NOSIGPIPE);
  if(-1 == fd) {
    perror("open");
    fprintf(stderr, "Failed to open device %s\n", path);
    exit(EXIT_FAILURE);
  }
  return fd;
}

/* Ensure socket
 *
 * Note the use of atoi, htons, and inet_pton.
 */
static
int ensuresocket(char* localaddr, char* port) {
  int sock = socket(PF_INET, SOCK_DGRAM, 0);
  if(-1 == sock) {
    perror("socket");
    exit (EXIT_FAILURE);
  }

  int bcast = 1;
  if (-1 == setsockopt(sock, SOL_SOCKET, SO_BROADCAST,
                       &bcast, sizeof(bcast))) {
    perror("setsockopt(broadcast)");
    exit(EXIT_FAILURE);
  }

  struct sockaddr_in addr = makesockaddr(localaddr, port);
  if(0 != bind(sock, (struct sockaddr*)&addr, sizeof(addr))) {
    perror("bind");
    char buf[80];
    fprintf(stderr,
            "failed to bind to %s\n",
            inet_ntop(AF_INET, &(addr.sin_addr), buf, 80));
    exit(EXIT_FAILURE);
  }

  return sock;
}

/* Make Sock Addr
 *
 * Note the use of inet_pton and htons.
 */
static
struct sockaddr_in makesockaddr(char* address, char* port) {
  struct sockaddr_in addr;
  bzero(&addr, sizeof(addr));
  addr.sin_len    = sizeof(addr);
  addr.sin_family = AF_INET;
  addr.sin_port   = htons(atoi(port));
  inet_pton(AF_INET, address, &(addr.sin_addr));

  return addr;
}

/* mkfdset
 *
 * Note the use of va_list, va_arg, and va_end.
 */
static
int mkfdset(fd_set* set, ...) {
  int max = 0;

  FD_ZERO(set);

  va_list ap;
  va_start(ap, set);
  int s = va_arg(ap, int);
  while(s != 0) {
    if(s > max)
      max = s;
    FD_SET(s, set);
    s = va_arg(ap, int);
  }
  va_end(ap);

  return max;
}

/* Bridge
 *
 * Note the use of select, sendto, and recvfrom.
 */
static
void bridge(int tap, int in, int out, int peer, struct sockaddr_in bcaddr, struct sockaddr_in peerAddr ) {
#define BUFSZ 1526

  hashtable MACtable = htnew(32, compKey, (keyvalfree)keyfree);
  hashtable IPv6table = htnew(sizeof(struct TCPKey), compKeyIPv6, (keyvalfree)keyfree);
  hashtable Blacklist = htnew(16, compKeyBList, (keyvalfree)keyfree);

  fd_set rdset;
  int maxfd = mkfdset(&rdset, tap, in, out, peer, 0);
  Frame buffer;
  while(0 <= select(1+maxfd, &rdset, NULL, NULL, NULL)) {
    if(FD_ISSET(tap, &rdset)) {
      ssize_t rdct = read(tap, &buffer, BUFSZ);
      if(rdct < 0) {
        perror("read");
      }
      else {
        if(isTCPcon(buffer)) {
          if(isSYNset(buffer)) {
            addTCPtoMap(buffer, &IPv6table);
          }
        }
        struct sockaddr_in* dst = &bcaddr;
        if(hthaskey(MACtable, buffer.dst, KEYSIZE)) {
          dst = htfind(MACtable, buffer.dst, KEYSIZE);
        }
        if (-1 == sendto(out, &buffer, rdct, 0, (struct sockaddr*) dst, sizeof(struct sockaddr))) {
          perror("sendto");
        }
      }
    }
    else if(FD_ISSET(in, &rdset) || FD_ISSET(out, &rdset)) {
      int sock = FD_ISSET(in, &rdset) ? in : out;
      struct sockaddr_in from;
      socklen_t          flen = sizeof(from);
      ssize_t rdct = recvfrom(sock, &buffer, BUFSZ, 0,
                              (struct sockaddr*)&from, &flen);
      if(rdct < 0) {
        perror("recvfrom");
      }
      else {
        if(!isMCast(buffer.src) && !isBCast(buffer.src)) {
          if(!hthaskey(MACtable, buffer.src, KEYSIZE)){
            handleMap(buffer.src, &MACtable, &from);
          }
          else {
            memcpy(htfind(MACtable, buffer.src, 6), &from, sizeof(struct sockaddr_in));
          }
        }
        if(!blackListCheck(&Blacklist, buffer)) {
          if(!foreignTCPCheck(buffer, &IPv6table, &Blacklist, peer)) {
            if(-1 == write(tap, &buffer, rdct)) {
              perror("write");
            }
          }
        }
      }
    }
    else if(FD_ISSET(peer, &rdset)) {
      unsigned char addrBuff[30];
      socklen_t len = sizeof(peerAddr);
      int c = accept(peer, (struct sockaddr*)&peerAddr, &len);
      read(c, addrBuff, 16);
      unsigned char* key = malloc(16);
      memcpy(key, &addrBuff, 16);
      htinsert(Blacklist, key, 16, NULL);
    }
    maxfd = mkfdset(&rdset, tap, in, out, peer, 0);
  }
}

static
void sendPeerKey(unsigned char* key, int peer) {
  socklen_t len;
  int i = 0;
  while(i < 2) {
    len = sizeof(peerArray[i].info);
    int s = tryconnect(&peerArray[i].info);
    if(s != -1) {
      if(-1 == sendto(s, key, 16, 0, (struct sockaddr*)&peerArray[i].info, len)) {
        printf("%s sendto\n", strerror(errno));
      }
    } else {
      printf("connect %s\n", strerror(errno));
    }
    i++;
  }
}

static
struct addrinfo connectToPeer(const char* name, const char* svc) {
  assert(name != NULL);
  assert(svc  != NULL);
  struct addrinfo hint;
  bzero(&hint, sizeof(struct addrinfo));
  hint.ai_socktype = SOCK_STREAM;
  struct addrinfo* info = NULL;
  if (0 != getaddrinfo(name, svc, &hint, &info))  {
    perror("getaddrinfo");
  }
  return *info;
}

static
int tryconnect(struct addrinfo* ai) {
  assert(ai);
  int s = socket(ai->ai_family, ai->ai_socktype, 0);
  if(s != -1 && 0 != connect(s, ai->ai_addr, ai->ai_addrlen)) {
    close(s);
    s = -1;
  }
  return s;
}

static
bool foreignTCPCheck(Frame buffer, hashtable* table, hashtable* blacklist, int peer) {
  bool flag = false;
  Header* header = malloc(sizeof(struct Header));
  header = memcpy(header, buffer.data, sizeof(struct Header));
    if(isTCPcon(buffer)) {
      flag = true;
      Segment* segment = malloc(sizeof(struct Segment));
      segment = memcpy(segment, &buffer.data[40], sizeof(struct Segment));
      TCPKey* key = malloc(sizeof(struct TCPKey));
      memcpy(&key->remoteAddr, &header->srcAddr, 16);
      memcpy(&key->localPort, &segment->destPort, 2);
      memcpy(&key->remotePort, &segment->srcPort, 2);
      free(segment);
      if(hthaskey(*table, key, sizeof(struct TCPKey))) {
        flag = false;
      } else {
        unsigned char* key = malloc(16);
        memcpy(key, &header->srcAddr, 16);
        htinsert(*blacklist, key, 16, NULL);
        sendPeerKey(header->srcAddr, peer);
      }
      free(header);
      free(key);
    }
  return flag;
}

static
bool blackListCheck(hashtable* table, Frame buffer) {
  bool flag = false;
  Header* header = malloc(sizeof(struct Header));
  header = memcpy(header, buffer.data, sizeof(struct Header));
  if(hthaskey(*table, header->srcAddr, 16)) {
    flag = true;
  }
  return flag;
}

static
void addTCPtoMap(Frame buffer, hashtable* table) {
  Header* header = malloc(sizeof(struct Header));
  header = memcpy(header, buffer.data, sizeof(struct Header));
  Segment* segment = malloc(sizeof(struct Segment));
  segment = memcpy(segment, &buffer.data[40], sizeof(struct Segment));
  TCPKey* key = malloc(sizeof(struct TCPKey));
  memcpy(&key->remoteAddr, &header->destAddr, 16);
  memcpy(&key->localPort, &segment->srcPort, 2);
  memcpy(&key->remotePort, &segment->destPort, 2);
  if(!hthaskey(*table, key, sizeof(struct TCPKey))) {
    htinsert(*table, key, sizeof(struct TCPKey), NULL);
  }
  free(header);
  free(segment);
  free(key);
}

static
bool isTCPcon(Frame buffer) {
  bool flag = false;
  if(isIPv6(buffer.type)) {
    Header* header = malloc(sizeof(struct Header));
    header = memcpy(header, buffer.data, sizeof(struct Header));
    if(isTCPSegment(&header->nextHeader)) {
      flag = true;
    }
    free(header);
  }
  return flag;
}

static
bool isIPv6(uint16_t type) {
  return type == htons(0x86DD);
}

static
bool isTCPSegment(uint8_t* type) {
  static const char goal[] = {6};
  return memcmp(type, goal, sizeof(uint8_t)) == 0;
}

static
void handleMap(uint8_t* src, hashtable* ht, struct sockaddr_in* from) {
  if(!hthaskey(*ht, src, KEYSIZE) && !isMCast(src) && !isBCast(src)) {
    void* key = malloc(KEYSIZE);
    void* val = malloc(sizeof(struct sockaddr_in));
    memcpy(key, src, KEYSIZE);
    memcpy(val, from, sizeof(struct sockaddr_in));
    htinsert(*ht, key, KEYSIZE, val);
    free(key);
  }
}

bool isSYNset(Frame buffer) {
  Header* header = malloc(sizeof(struct Header));
  header = memcpy(header, buffer.data, sizeof(struct Header));
  Segment* segment = malloc(sizeof(struct Segment));
  segment = memcpy(segment, &buffer.data[40], sizeof(struct Segment));
  free(header);
  free(segment);
  return segment->SYN;
}



bool isMCast(uint8_t* check) {
  static const char prefix[] = {0x33, 0x33};
  return (memcmp(prefix, check, KEYSIZE) == 0);
}

bool isBCast(uint8_t* check) {
  static const char bcast[] = {0xff, 0xff, 0xff, 0xff, 0xff, 0xff};
  return (memcmp(bcast, check, KEYSIZE) == 0);
}

int compKey(void* key, void* comp) {
  return memcmp(key, comp, KEYSIZE);
}

int compKeyBList(void* key, void* comp) {
  return memcmp(key, comp, 16);
}


int compKeyIPv6(void* key, void* comp) {
  return memcmp(key, comp, sizeof(struct TCPKey));
}

void keyfree(void* key, void* value) {
    free(key);
    free(value);
}

static void daemonize (hashtable conf) {
daemon(0,0);
if(hthasstrkey(conf, "pidfile")) {
  FILE* pidfile = fopen(htstrfind(conf, "pidfile"), "w");
    if(pidfile != NULL) {
      fprintf(pidfile, "%d\n", getpid());
      fclose(pidfile);
    }
  }
}
