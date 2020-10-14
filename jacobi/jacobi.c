/*
 Course: CSCI 347 - Fall 2019
 Aran Clauson
 File Name: jacobi.c
 Kyle Leibowitz & Mason Rudolph
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <fts.h>
#include <errno.h>
#include <unistd.h>
#include <pthread.h>
#include "jacobi.h"
#include <math.h>

#define	NOTH 8

double epsilon = .001;

ThreadArgs threadArgs[NOTH];

barrier b;

int main(int argc, char* argv[]) {
    FILE* file;
    if((file = fopen("input.mtx" , "r"))==NULL){
      perror("fopen");
      exit(1);
    }

    matrixData (* dMatrix)[1024] = malloc(sizeof(matrixData)*1024*1024);
    initMatrix(file, dMatrix);
    
    initThread(dMatrix);
    barrierRendezInit(&b);
    
    for(int i =0; i < NOTH; i++) {
      //printf("%d %d\n", threadArgs[i].indexStart, threadArgs[i].indexEnd);
      int retVal = pthread_create(&threadArgs[i].threadId, NULL, &threadFunction, &threadArgs[i]);
      if(retVal) {
        perror("pthread_create: ");
        exit(1);
      }
    }
    
    for(int i = 0; i < NOTH; i++) {
      pthread_join(threadArgs[i].threadId, NULL);
    }

    //Checking if jacobi properly went all the way to epsilon
    FILE* file2;
    if((file2 = fopen("output-001.mtx" , "r"))==NULL){
      perror("fopen");
      exit(1);
      }
    matrixData (* dMatrix2)[1024] = malloc(sizeof(matrixData)*1024*1024);
    initMatrix(file2, dMatrix2);
    if(basicallyTheSame(dMatrix, dMatrix2)){
      printf("Test Passed\n");
      }
    else{
      printf("Test Failed\n");
    }

    return 0;
}

//Function that initializes all the variables of the barrier.
void barrierRendezInit(barrier* b) {
  pthread_mutex_init(&(b->mtx), NULL);
  pthread_cond_init(&(b->threadsArrived), NULL);
  b->count = 0;
  b->finalCont = 0;
}

//Function that stops all threads until it reaches NOTH (waiting them). Once the last thread has arrived (count==NOTH) it resets count, sets all parents to child's value, sets the main continue and then releases threads
void barrierRendezArrived(barrier* b) {
  pthread_mutex_lock(&(b->mtx));
  b->count++;
  if(b->count < NOTH) {
    pthread_cond_wait(&(b->threadsArrived), &(b->mtx));
    }

  else {
    b->count = 0;
    setParentChild();
    finalCont(b);
    for(int i =0; i < NOTH; i++) {
        pthread_cond_signal(&(b->threadsArrived));
        }
    }
  pthread_mutex_unlock(&(b->mtx));
  }

//Function that goes through all of nodes and sets the parent to be the child, and the child to be zero.
void setParentChild(){
for(int i=1; i<1023; i++){
        for(int k=1; k<1023; k++){
          threadArgs[0].dMatrix[i][k].parent=threadArgs[0].dMatrix[i][k].child;
          }
        }
}

//Function that sets the continue for the threads. If any of the threads have a continue, sets the main loop to continue.
//(NOTE: If finalCont = 0 main loop will continue.) 
void finalCont(barrier* b){
    b->finalCont=1;
    for(int i = 0; i<NOTH; i++){
      if (!(threadArgs[i].tCont)){
        b->finalCont=0;
        }
      }
}

//Function that each thread runs. Runs until all the threads have stopped changing by the epsilon 
void* threadFunction(void* arg) {
  ThreadArgs* myThreadArg;
  myThreadArg = (ThreadArgs*)arg;
  while(!(b.finalCont)){
    computeMatrix(myThreadArg);
    barrierRendezArrived(&b);
  }
}

//Function that goes through the matrix (at the threads given rows) and recalculates the next generation of children. Then it calculates if that particular thread wants to continue or not (if it is changing more than the epislon)
void computeMatrix(ThreadArgs* myThreadArg){
  myThreadArg->tCont=1;

  for(int i=myThreadArg->indexStart; i<myThreadArg->indexEnd; i++){
    for(int k=1; k<1023; k++){
      myThreadArg->dMatrix[i][k].child = compAvg(i, k, myThreadArg);
      if(trueNode(i, k, myThreadArg)) myThreadArg->tCont=0;
      }
    }
  }

//Function that returns if a node is changing more than the epsilon between parent and child
int trueNode(int i, int j, ThreadArgs* myThreadArg){
  double delta = fabs(myThreadArg->dMatrix[i][j].parent-myThreadArg->dMatrix[i][j].child);
  return(epsilon<delta);
}

//Function that determines if the matrix gotten in our Jacobi is similar to the one aquired by Aran.
int basicallyTheSame(matrixData (* dMatrix)[1024], matrixData (* dMatrix2)[1024]){
  for(int i=1; i<1023; i++){
    for(int k=1; k<1023; k++){
      if(fabs(dMatrix[i][k].parent-dMatrix2[i][k].parent)>(2*epsilon)){
        return 0;
      }
    }
  }
  return 1;
}

//Function that initizes all the threads
void initThread(matrixData (* dMatrix)[1024]){
	int i,y;
  int start;
  int end;
  int numRow = 1022/NOTH;
  int dif = 1022 - (numRow*NOTH);
  for(i=0;i<NOTH;i++){
    threadArgs[i].dMatrix = dMatrix;
    threadArgs[i].threadCount = i;
    threadArgs[i].threadId=0;
    threadArgs[i].indexStart = 1+(1022*i)/NOTH;
    threadArgs[i].indexEnd = 1+(1022*(i+1))/NOTH;
	  }
  }

//Function that returns the average of all of a nodes neighbors.
double compAvg(int i, int j, ThreadArgs* myThreadArg){
  return (myThreadArg->dMatrix[i-1][j].parent+myThreadArg->dMatrix[i+1][j].parent+myThreadArg->dMatrix[i][j-1].parent+myThreadArg->dMatrix[i][j+1].parent)/4;
  }

//Function that given a file with matrix data, fills a matrix of parents and children with that data (setting parent to values given)
void initMatrix(FILE * file, matrixData (* dMatrix)[1024]){
    matrixData curNum;
    curNum.parent = 0;
    int i=0;
    int k=0;
    while(fscanf(file, "%le", &curNum.parent)>0){
      curNum.child=0;
      dMatrix[i][k]=curNum;
      i++;
      if(i>1023){
        i=0;
        k++;
      }
  }
}
