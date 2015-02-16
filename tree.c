#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "tree.h"
#include "structures.h"
#include "sequence.h"
#include "terminator.h"

extern int num_ini_seq;   /* Number of initial sequences */
extern int R;             /* Rekombination-rate */
extern int RZ;

extern double sumA(void);/* The global A                           */
extern int seqs_len;     /* Number of sequences to choose from (k) */
extern double exprate;

SEQUENCE *rootTime;       /* Base of timeline */
static SEQUENCE *lastTime; /* Timeline */

static int edgeCounter = 0;

static bool probCoalescens(double k)
{
  if (RZ)
    return 1;
  return (drand48() < (k*(k-1.0))/(k*(k-1.0)+2.0*sumA()));
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

void makeCoalescensNode(double time)
{
  SEQUENCE *s,*s1,*s2;
  INTERVAL *i = NULL;

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
  s->daughter = s2;
  s->intervals = unite(s1->intervals,s2->intervals, updateCoalescens);
  s->A = computeA(s->intervals);
  s->Time = time;
  s->gray = uniteNoTerm(s1->gray,s2->gray);

  s1->father = s;
  s2->father = s;

  putSequence(s);

  INTERVAL *last_make;
  double update = updateOneK(&last_make);  

  printf("N %i|1|%f|",s->ID,s->Time);
  printf("Time:%f",s->Time);
  printf("\n");

  printf("IN %i|",s->ID);
  prettyInterval(s->intervals);
  printf(" |");

  if (update >= 0.0) {

    if (last_make!=NULL) {
      i = inverse(last_make);
      intersect(i,uniteNoTerm(s1->intervals,s2->intervals));
      prettyInterval(i);
    } else {
      i = uniteNoTerm(s1->intervals,s2->intervals);
      prettyInterval(i);
    }

  }

  printf(" |");
  prettyInterval(s->gray);
  if (update >= 0.0)
    s->gray = uniteNoTerm(i,s->gray);

  printf("\n");

  if (s->son->indegree == 1) {
    printf("E %d|%d|%d\n",edgeCounter,s->son->son->ID,s->ID);
    printf("IE %d|",edgeCounter);
    prettyInterval(s->son->intervals);
    printf(" |");
    prettyInterval(s->son->gray);    
    printf("\n");
    edgeCounter++;
  }
  else
    printf("E %d|%d|%d\n",edgeCounter++,s->son->ID,s->ID);

  if (s->daughter->indegree == 1) {
    printf("E %d|%d|%d\n",edgeCounter,s->daughter->son->ID,s->ID);
    printf("IE %d|",edgeCounter);
    prettyInterval(s->daughter->intervals);
    printf(" |");
    prettyInterval(s->daughter->gray);    
    printf("\n");
    edgeCounter++;
  }
  else
    printf("E %d|%d|%d\n",edgeCounter++,s->daughter->ID,s->ID);
}


void makeRecombinationNode(double time)
{
  SEQUENCE *s,*r,*s1,*s2;
  double P;
  bool goodP;

  s = getWeightedSequence();
  s->outdegree = 1;

  r = newSequence();
  r->indegree = 1;
  r->outdegree = 2;
  r->son = s;
  r->A = s->A;
  r->intervals = copyIntervals(s->intervals);
  r->gray = copyIntervals(s->gray);
  s->father = r;

  if (lastTime==NULL) {
    lastTime = r;
    rootTime = r;
  }
  else {
    lastTime->nextTime = r;
    lastTime = r;
  }

  r->Time = time;

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
  
  do {
    P = getLegalPoint(r->intervals);
    s1->intervals = intersectTo(r->intervals,P);
    s2->intervals = intersectFrom(r->intervals,P);
  
    if (getYn(s1->intervals)==P) {
      goodP = updateRecombination(P);
    }
    else
      goodP = true;
  } while (!goodP);

  r->P = P;
  s1->gray = intersectTo(r->gray,P);
  s2->gray = intersectFrom(r->gray,P);

  s1->A = computeA(s1->intervals);
  s2->A = computeA(s2->intervals);

  putSequence(s1);
  putSequence(s2);

  printf("N %i|2|%f|",r->ID,r->Time);
  printf("Time: %f",r->Time);
  //printf("P: %f",r->P);
  printf("\n");
  
  printf("IN %i|%f#",r->ID,r->P);
  prettyInterval(r->intervals);
  printf("| |");
  prettyInterval(r->gray);
  printf("\n");

  if (r->son->indegree == 1) {
    printf("E %i|%i|%i\n",edgeCounter,r->son->son->ID,r->ID);
    printf("IE %i|",edgeCounter);
    prettyInterval(r->son->intervals);
    printf(" |");
    prettyInterval(r->son->gray);    
    printf("\n");
    edgeCounter++;
  }
  else
    printf("E %i|%i|%i\n",edgeCounter++,r->son->ID,r->ID);

}

void build(void)
{
  SEQUENCE *s;
  int i;
  
  rootTime = lastTime = NULL;
  double time = 0.0;
  initTerminator();
  initSequencePool();

  for (i=0; i<num_ini_seq; i++) {
    s = newSequence();
    s->A = (double)R/2.0;
    s->indegree = 0;
    s->outdegree = 0;
    s->intervals = initInterval(0,(double)R/2.0);
    s->Time = 0.0;
    putSequence(s);

    printf("N %i|0|%f|",s->ID,s->Time);
    printf("Time:%f\n",s->Time);
    printf("IN %d|",s->ID);
    prettyInterval(s->intervals);
    printf("|\n");
  }

  while (!theEnd()) {
    if (seqs_len == 1)
      printf("One size!!\n");

    double k = seqs_len;
    if (RZ) {
      if (exprate != 0.0) {
        time = time + 
          log(1.0+exprate*exp(-time)*-2.0/(k*(k-1))*log(drand48()));
      }
      else
        time = time + exponen(k*(k-1.0)/2.0);
    } else
      time = time + exponen(k*(k-1.0)/2.0+sumA());

    if (probCoalescens(k))
      makeCoalescensNode(time);
    else
      makeRecombinationNode(time);
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




