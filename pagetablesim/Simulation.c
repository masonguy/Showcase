#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include "Simulation.h"
/* If there are custom classes/source files that you write, with
   custom functions, and you want those functions available for use in
   THIS .c file, then include the header file for the custom .c
   file(s) you've written, using the #include directive. For example:

   #include "SomeFile.h"

 */
#define PAGE_TABLE_SIZE 128

FILE* file1;
FILE* file2;
FILE* curFile;
FILE* nextFile;
FILE* tempFile;
Page* pageTable[PAGE_TABLE_SIZE];
int eofFlag = 0;
int file1Flag = 0;
int file2Flag = 0;
int numFaults = 0;
int nonFaults = 0;
int curTableSize = 128;
int freeForAllFlag = 0;


void Simulate(char* fileName1, char* fileName2, char allocation) {
  // A function whose inputs are the trace files names and the allocation
  // strategy 's' for split or 'f' for free for all
  char input[12];
  char discard[1];
  openFiles(fileName1, fileName2);
  initTable();
  curFile = file1;
  nextFile = file2;
  tempFile = NULL;
  int iter = 0;
  rewind(curFile);
  while(eofFlag != 1) {
    checkCounter(iter);
    if(fscanf(curFile, " %s %c", input, discard) == EOF) {
      checkEOF();
    } else {
      if(freeForAllFlag) {
        freeForAllocation(input, iter);
      } else {
        splitAllocation(input,iter);
      }
    }
    iter++;
  }
  for(int i = 0; i < curTableSize; i++) {
    free(pageTable[i]);
  }
  printf("Page Faults for %d frames: %d\n", curTableSize, numFaults);
  resetFlags();
  numFaults = 0;
  nonFaults = 0;
}

//Function that resets flags at end of each simulation
void resetFlags() {
  eofFlag = 0;
  file1Flag = 0;
  file2Flag = 0;
}

//Function that initilizes table values to -1, called at each simulation
void initTable() {
  for(int i = 0; i < curTableSize; i++) {
    pageTable[i] = malloc(sizeof(Page*)*PAGE_TABLE_SIZE);
    pageTable[i]->timeStamp = -1;
    strcpy(pageTable[i]->pageNum,"-1");
    pageTable[i]->ASID = -1;
  }
}

//Function that handles the begining allocation and specfic allocations for freeForAll
//charachteristics of the allocation process, and than calls replaceLastUsed
void freeForAllocation(char* input, int iter) {
  int i = 0;
  while(i < curTableSize) {
    if(pageTable[i]->timeStamp == -1) {
      numFaults++;
      strcpy(pageTable[i]->pageNum, input);
      pageTable[i]->timeStamp = iter;
      if(curFile == file1) {
        pageTable[i]->ASID = 1;
      } else if(curFile == file2) {
        pageTable[i]->ASID = 2;
      }
      return;
    } else if((strncmp(input, pageTable[i]->pageNum, 5) == 0) &&
          ((pageTable[i]->ASID == 1 && curFile == file1) || (pageTable[i]->ASID == 2 && curFile == file2))) {
      nonFaults++;
      pageTable[i]->timeStamp = iter;
      return;
    }
    i++;
  }
  replaceLastUsed(input, iter);
}

//Function that finds index of least recently used page
// and than replaces it with the new page
void replaceLastUsed(char* input, int iter) {
  int tempTableSize = curTableSize;
  int i = 0;
  if(curFile == file1 && freeForAllFlag != 1) {
    i = 0;
    tempTableSize = curTableSize/2;
  } else if(curFile == file2 && freeForAllFlag != 1){
    i = tempTableSize/2;
  }
  numFaults++;
  int lastUsedNum = INT_MAX;
  int lastFrame = -1;
  while(i < tempTableSize) {
    if(lastUsedNum > pageTable[i]->timeStamp) {
      lastUsedNum = pageTable[i]->timeStamp;
      lastFrame = i;
    }
    i++;
  }
  strcpy(pageTable[lastFrame]->pageNum, input);
  pageTable[lastFrame]->timeStamp = iter;
  if(curFile == file1) {
    pageTable[lastFrame]->ASID = 1;
  } else if(curFile == file2) {
    pageTable[lastFrame]->ASID = 2;
  }
}

//Function that handles the begining allocation and specfic allocations for splitAllocation
//charachteristics of the allocation process, and than calls replaceLastUsed
void splitAllocation(char* input, int iter) {
  int tempTableSize = curTableSize;
  int i = 0;
  if(curFile == file1) {
    i = 0;
    tempTableSize = curTableSize/2;
  } else {
    i = curTableSize/2;
  }
  while(i < tempTableSize) {
    if(pageTable[i]->timeStamp == -1) {
      numFaults++;
      strcpy(pageTable[i]->pageNum, input);
      pageTable[i]->timeStamp = iter;
      if(curFile == file1) {
        pageTable[i]->ASID = 1;
      } else if(curFile == file2) {
        pageTable[i]->ASID = 2;
      }
      return;
    } else if((strncmp(input, pageTable[i]->pageNum, 5) == 0) &&
          ((pageTable[i]->ASID == 1 && curFile == file1) || (pageTable[i]->ASID == 2 && curFile == file2))) {
      nonFaults++;
      pageTable[i]->timeStamp = iter;
      return;
    }
    i++;
  }
  replaceLastUsed(input, iter);
}

//This function checks if 20 lines have been read, and if so change
//curFile to the next file to read
void checkCounter(int iter) { //TODO fix switching
  if(file1Flag == 1) {
      curFile = file2;
  }
  else if(file2Flag == 1) {
      curFile = file1;
  }
  else if(iter % 20 == 0) {
    if(curFile == file1 && file2Flag != 1) {
        curFile = file2;
    }
    else if(curFile == file2 && file1Flag != 1) {
        curFile = file1;
    }
  }
}

//this function checks if either file is at the EOF and if both files are at EOF
//the eof flag is set signaling the end of both files
void checkEOF() {
  if(curFile == file1) {
    file1Flag = 1;
    if(file2Flag == 1)	{
      eofFlag = 1;
    }
  } else if (curFile == file2) {
    file2Flag = 1;
    if(file1Flag == 1) {
      eofFlag = 1;
    }
  }
}

//this function opens at sets the files to their globals
void openFiles(char* fileName1, char* fileName2) {
  file1 = malloc(sizeof(FILE*));
  file2 = malloc(sizeof(FILE*));
  curFile = malloc(sizeof(FILE*));
  nextFile = malloc(sizeof(FILE*));
  tempFile = malloc(sizeof(FILE*));
  if((file1 = fopen(fileName1, "r")) == NULL) {
    perror("fopen");
    exit(1);
  }
  if((file2 = fopen(fileName2, "r")) == NULL) {
    perror("fopen");
    exit(1);
  }
}


int main(int argc, char* argv[]) {
  // Run simulation
  printf("=================================\n");
  if(strcmp(argv[3], "f") == 0) {
    freeForAllFlag = 1;
    printf("Allocation:  Free For All\n");
  } else {
    printf("Allocation:  Split\n");
  }
  Simulate(argv[1], argv[2], argv[3]);
  curTableSize = 64;
  Simulate(argv[1], argv[2], argv[3]);
  curTableSize = 32;
  Simulate(argv[1], argv[2], argv[3]);
  printf("=================================\n");
}
