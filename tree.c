#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "tree.h"
#include "structures.h"
#include "sequence.h"

extern int num_ini_seq;   /* Number of initial sequences */

extern int seqs_len;      /* Number of sequences to choose from (k) */

SEQUENCE *rootTime;       /* Base of timeline */
static SEQUENCE *lastTime; /* Timeline */

extern double selection_rate;

bool probCoalescens(double k)
{
  double sz = (k*(k-1.0))/(k*(k-1.0)+k*selection_rate);
  return (drand48()<sz);
}

double exponen(double f)
{
  return -log(drand48())/f;
}

static int edgeCounter = 0;

void makeCoalescensNode(double newTime)
{
  SEQUENCE *s,*s1,*s2;

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
  s->Time = newTime;

  s1->father = s;
  s2->father = s;

  putSequence(s);
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

  for (i=0; i<num_ini_seq; i++) {
    s = newSequence();
    s->indegree = 0;
    s->outdegree = 0;
    s->Time = 0.0;

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
  while (seqs_len>1) {
    double k = (double)seqs_len;

    newTime = newTime + exponen(k*(k-1.0)/2.0+k*selection_rate);

    if (probCoalescens(k)) {
      makeCoalescensNode(newTime);
      i++;
    }
    else {
      makeSelectionNode(newTime);
      i++;
    }
  }
}

