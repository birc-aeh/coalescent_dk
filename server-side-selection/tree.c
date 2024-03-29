#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "tree.h"
#include "structures.h"
#include "sequence.h"

extern double selection_rate;
extern int num_ini_seq;   /* Number of initial sequences */
extern int seqs_len;      /* Number of sequences to choose from (k) */
SEQUENCE *rootTime;       /* Base of timeline */
static SEQUENCE *lastTime; /* Timeline */

static bool probCoalescens(double k)
{
  double sz = (k*(k-1.0))/(k*(k-1.0)+k*selection_rate);
  return (drand48()<sz);
}

static double exponen(double f)
{
  return -log(drand48())/f;
}

static int edgeCounter = 0;

static void makeCoalescensNode(double newTime)
{
  SEQUENCE *s,*s1,*s2;

  s1 = getSomeSequence();
  s1->parents = 1;

  s2 = getSomeSequence();
  s2->parents = 1;
  
  s = newSequence();
  s->children = 2;
  s->parents = 0;

  lastTime->nextTime = s;
  s->prevTime = lastTime;
  lastTime = s;

  s->son = s1;
  s->sonID = edgeCounter++;
  s->daughter = s2;
  s->daughterID = edgeCounter++;
  s->Time = newTime;

  s1->father = s;
  s2->father = s;

  putSequence(s);
}

static void makeSelectionNode(double newTime)
{
  SEQUENCE *s,*r,*s1,*s2;

  s = getSomeSequence();
  s->parents = 1;

  r = newSequence();
  r->children = 1;
  r->parents = 2;
  r->son = s;
  s->father = r;

  lastTime->nextTime = r;
  r->prevTime = lastTime;
  lastTime = r;

  r->Time = newTime;

  s1 = newSequence();
  s1->children = 1;
  s1->parents = 1;
  s1->son = r;

  s2 = newSequence();
  s2->children = 1;
  s2->parents = 1;
  s2->son = r;

  r->father = s1;
  r->mother = s2;
  
  putSequence(s1);
  putSequence(s2);

  r->sonID = edgeCounter++;
}

void build(void)
{
  int i;
  double newTime = 0.0;
  rootTime = lastTime = NULL;
  for (i=0; i<num_ini_seq; i++) {
    SEQUENCE *s = newSequence();
    s->children = 0;
    s->parents = 0;
    s->Time = 0.0;

    putSequence(s);

    if (lastTime==NULL) {
      lastTime = s;
      rootTime = s;
    }
    else {
      lastTime->nextTime = s;
      s->prevTime = lastTime;
      lastTime = s;
    }
  }

  while (seqs_len>1) {
    double k = (double)seqs_len;
    newTime = newTime + exponen(k*(k-1.0)/2.0+k*selection_rate);
    if (probCoalescens(k))
      makeCoalescensNode(newTime);
    else
      makeSelectionNode(newTime);
  }
}

