#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "tree.h"
#include "memory.h"
#include "structures.h"
#include "sequence.h"
#include "terminator.h"

extern int num_ini_seq;   /* Number of initial sequences */
extern int R;             /* Rekombination-rate */
extern int RZ;

extern double sumA;      /* The global A                           */
extern int size;         /* Number of sequences to choose from (k) */
extern double newTime;   /* Elapsed time (backwards)               */
extern double exprate;

SEQUENCE *rootTime;       /* Base of timeline */
static SEQUENCE *lastTime; /* Timeline */

/*  static int edgeCounter = 0; */
extern double selection_rate;

bool probCoalescens(void)
{
  double sz;

  //if (RZ) return 1;
  sz = (double)size;
  sz = (sz*(sz-1.0))/(sz*(sz-1.0)+sz*selection_rate);
  return (drand48()<sz);
}

double computeA(INTERVAL *i)
{
  if (i->size==0)
    return 0.0;
  return (getYn(i)-getX1(i));
}

double exponen(double f)
{
  return -log(drand48())/f;
}

double getLegalPoint(INTERVAL *i)
{
  double x1,yn;
  x1 = getX1(i);
  yn = getYn(i);
  return x1+(drand48()*(yn-x1));
}

extern INTERVAL *last_make;
static int edgeCounter = 0;

void makeCoalescensNode(void)
{
  SEQUENCE *s,*s1,*s2;
  INTERVAL *i;

  s1 = getSomeSequence();
  s1->outdegree = 1;

  s2 = getSomeSequence();
  s2->outdegree = 1;
  
  s = newSequence();
  s->indegree = 2;
  s->outdegree = 0;

  if (lastTime==NULL) {
    lastTime = s;
    rootTime = s;
  }
  else {
    lastTime->nextTime = s;
    lastTime = s;
  }

/*    printf("\n\n"); */
/*    prettyInterval(s1->intervals); */
/*    printf("\n"); */
/*    prettyInterval(s2->intervals); */
/*    printf("\n\n"); */

  s->son = s1;
  s->sonID = edgeCounter++;
  s->daughter = s2;
  s->daughterID = edgeCounter++;
  s->intervals = uniteNoTerm(s1->intervals,s2->intervals);
  s->A = computeA(s->intervals);
  s->Time = newTime;

  s1->father = s;
  s2->father = s;

  putSequence(s);

  s->matleft = updateOneK();  

/*    printf("N %i|1|%f|",s->ID,s->Time); */
/*    printf("Time:%f",s->Time); */
/*    printf("\n"); */

/*    printf("IN %i|",s->ID); */
/*    prettyInterval(s->intervals); */
/*    printf("|"); */

  if (s->matleft>=0.0) {

    if (last_make!=NULL) {
      i = inverse(last_make);
      intersect(i,uniteNoTerm(s1->intervals,s2->intervals));
      prettyInterval(i);
    } else {
      i = uniteNoTerm(s1->intervals,s2->intervals);
      prettyInterval(i);
    }
  }

/*    printf("\n"); */

/*    if (s->son->indegree == 1) { */
/*      printf("E %d|%d|%d\n",edgeCounter,s->son->son->ID,s->ID); */
/*      printf("IE %d|",edgeCounter); */
/*      prettyInterval(s->son->intervals); */
/*      printf("\n"); */
/*      edgeCounter++; */
/*    } */
/*    else */
/*      printf("E %d|%d|%d\n",edgeCounter++,s->son->ID,s->ID); */

/*    if (s->daughter->indegree == 1) { */
/*      printf("E %d|%d|%d\n",edgeCounter,s->daughter->son->ID,s->ID); */
/*      printf("IE %d|",edgeCounter); */
/*      prettyInterval(s->daughter->intervals); */
/*      printf("\n"); */
/*      edgeCounter++; */
/*    } */
/*    else */
/*      printf("E %d|%d|%d\n",edgeCounter++,s->daughter->ID,s->ID); */
}

void makeSelectionNode(void)
{
  SEQUENCE *s,*r,*s1,*s2;
/*    double P; */
/*    bool goodP; */

  s = getSomeSequence();
  s->outdegree = 1;

  r = newSequence();
  r->indegree = 1;
  r->outdegree = 2;
  r->son = s;
  r->intervals = copyIntervals(s->intervals);
  s->father = r;

  if (lastTime==NULL) {
    lastTime = r;
    rootTime = r;
  }
  else {
    lastTime->nextTime = r;
    lastTime = r;
  }

  r->Time = newTime;

  s1 = newSequence();
  s1->indegree = 1;
  s1->outdegree = 1;
  s1->son = r;

  s2 = newSequence();
  s2->indegree = 1;
  s2->outdegree = 1;
  s2->son = r;

  r->father = s1;
  r->mother = s2;
  s1->intervals = copyIntervals(r->intervals);
  s2->intervals = copyIntervals(r->intervals);

  
  r->P = 0.0;

  s1->A = computeA(s1->intervals);
  s2->A = computeA(s2->intervals);

  putSequence(s1);
  putSequence(s2);

  r->sonID = edgeCounter++;

/*    printf("N %i|2|%f|",r->ID,r->Time); */
/*    printf("Time: %f",r->Time); */
/*    //printf("P: %f",r->P); */
/*    printf("\n"); */
  
/*    printf("IN %i|%f#",r->ID,r->P); */
/*    prettyInterval(r->intervals); */
/*    printf("\n"); */

/*    if (r->son->indegree == 1) { */
/*      printf("E %i|%i|%i\n",edgeCounter,r->son->son->ID,r->ID); */
/*      printf("IE %i|",edgeCounter); */
/*      prettyInterval(r->son->intervals); */
/*      printf("\n"); */
/*      edgeCounter++; */
/*    } */
/*    else */
/*      printf("E %i|%i|%i\n",edgeCounter++,r->son->ID,r->ID); */

}


/*  static void addTopPoints(SEQUENCE *s) */
/*  { */
/*    addRootPoint(s->intervals,s); */
/*  } */

void build(void)
{
  SEQUENCE *s;
  int i;
  
  rootTime = lastTime = NULL;
  newTime = 0.0;
  initTerminator();
  initSequencePool();
/*    initRootPoints(); */

  for (i=0; i<num_ini_seq; i++) {
    s = newSequence();
    s->A = (double)R/2.0;
    s->indegree = 0;
    s->outdegree = 0;
    s->intervals = initInterval(0,(double)R/2.0);
    s->Time = 0.0;
    s->count = 1;

    putSequence(s);

    if (lastTime==NULL) {
      lastTime = s;
      rootTime = s;
    }
    else {
      lastTime->nextTime = s;
      lastTime = s;
    }

/*      printf("N %i|0|%f|",s->ID,s->Time); */
/*      printf("Time:%f\n",s->Time); */
/*      printf("IN %d|",s->ID); */
/*      prettyInterval(s->intervals); */
/*      printf("|\n"); */
  }

  i = num_ini_seq;
  while (size>1) {
/*      if (size==1) */
/*        printf("One size!!\n"); */

    if (RZ) {
      newTime = newTime + exponen((double)size*((double)size-1.0)/2.0+
				  (double)size*selection_rate);
    } else {
      fprintf(stderr,"Rho must be zero!!\n");
      exit(1);
    }

    //fprintf(stderr,"%f\n",newTime);


    if (probCoalescens()) {
      makeCoalescensNode();
      i++;
    }
    else {
      makeSelectionNode();
      i++;
    }
  }

/*    traverseTopSeqs(addTopPoints); */

}


INTERVAL *the_intervals;

void intersectOne(SEQUENCE *s)
{
  intersect(s->intervals,the_intervals);  
  s->A = computeA(s->intervals);  
}

void intersectAll(INTERVAL *i)
{
  if (i==NULL) return;
  the_intervals = i;
  traverseTopSeqs(intersectOne);
  recalculateAllA();
}




