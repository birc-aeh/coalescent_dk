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

extern int size;         /* Number of sequences to choose from (k) */
extern double exprate;

SEQUENCE *rootTime;       /* Base of timeline */
static SEQUENCE *lastTime; /* Timeline */

extern double selection_rate;

bool probCoalescens(double k)
{
  double sz = (k*(k-1.0))/(k*(k-1.0)+k*selection_rate);
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

extern INTERVAL *last_make;
static int edgeCounter = 0;

void makeCoalescensNode(double newTime)
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
}

void makeSelectionNode(double newTime)
{
  SEQUENCE *s,*r,*s1,*s2;

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
}

void build(void)
{
  SEQUENCE *s;
  int i;
  
  rootTime = lastTime = NULL;
  double newTime = 0.0;
  initTerminator();
  initSequencePool();

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
  }

  i = num_ini_seq;
  while (size>1) {

    if (RZ) {
      newTime = newTime + exponen((double)size*((double)size-1.0)/2.0+
				  (double)size*selection_rate);
    } else {
      fprintf(stderr,"Rho must be zero!!\n");
      exit(1);
    }

    if (probCoalescens((double)size)) {
      makeCoalescensNode(newTime);
      i++;
    }
    else {
      makeSelectionNode(newTime);
      i++;
    }
  }
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
}
