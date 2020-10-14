#if !defined(a408e82f_5d68_4d14_bbf7_5f73c2147a3a)
#define a408e82f_5d68_4d14_bbf7_5f73c2147a3a

typedef struct matrixData matrixData;
typedef matrixData* mData;
struct matrixData{
    double parent;
    double child;
};

typedef struct{
	int			threadCount;
  int     tCont;
	pthread_t	threadId;
	int			indexStart;
	int			indexEnd;
  matrixData (* dMatrix)[1024];
} ThreadArgs;

typedef struct {
  pthread_mutex_t mtx;
  unsigned int count;
  int finalCont;
  // unsigned int maxThreads;
  pthread_cond_t threadsArrived;
} barrier;

void initMatrix(FILE * file, matrixData (* dMatrix)[1024]);

void computeMatrix(ThreadArgs* myThreadArg);

int trueNode(int i, int j, ThreadArgs* myThreadArg);

double compAvg(int i, int j, ThreadArgs* myThreadArg);

void initThread(matrixData (* dMatrix)[1024]);

void* threadFunction(void* arg);

int basicallyTheSame(matrixData (* dMatrix)[1024], matrixData (* dMatrix2)[1024]);

void barrierRendezInit(barrier* b);

void barrierRendezArrived(barrier* b);

void finalCont(barrier* b);

void setParentChild();

#endif
