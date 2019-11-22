#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <assert.h>
#include "tree.h"
#include "structures.h"
#include "sequence.h"

extern int num_ini_seq;  /* Number of initial sequences */
extern int seqs_len;     /* Number of sequences to choose from (k) */
extern double M1, M2;
extern int type_counts[2];

static double newTime;   /* Elapsed time (backwards) */
static SEQUENCE *lastTime; /* Timeline */
static int coalescent_events_to_go;
static int edgeCounter = 0;

SEQUENCE *rootTime;       /* Base of timeline */

int nextEvent(void)
{
  int type0 = type_counts[0];
  int type1 = type_counts[1];

  double pc1 = (type0*(type0-1))/2;
  double pc2 = (type1*(type1-1))/2;

  double pm1 = type0*M1;
  double pm2 = type1*M2;

  double p = drand48()*(pc1+pc2+pm1+pm2);
  if (p < pc1)
      return 0;
  if (p - pc1 < pc2)
      return 1;
  if (p - pc1 - pc2 < pm1)
      return 2;
  return 3;
}

double exponen(double f)
{
  return -log(drand48())/f;
}

void makeCoalescensNode(int type)
{
  coalescent_events_to_go -= 1;

  SEQUENCE *s1 = getSequenceWithType(type);
  SEQUENCE *s2 = getSequenceWithType(type);
  SEQUENCE *s = newSequence(type);
  s->indegree = 2,
  s->son = s1,
  s->daughter = s2,
  s->Time = newTime,

  s1->outdegree = 1;
  s2->outdegree = 1;
  s1->father = s;
  s2->father = s;

  if (lastTime == NULL) {
    lastTime = s;
    rootTime = s;
  }
  else {
    lastTime->nextTime = s;
    lastTime = s;
  }

  putSequence(s);

  printf("N %i|%i|%f|",s->ID,1,s->Time);
  printf("Time:%f",s->Time);
  printf("\n");

  printf("IN %i|",s->ID);
  printf("0.000000,0.500000#");
  printf("|");

  if (coalescent_events_to_go == 1) {
      printf("0.000000,0.500000#");
  }

  printf("\n");

  printf("E %d|%d|%d\n",edgeCounter++,s->son->ID,s->ID);
  printf("E %d|%d|%d\n",edgeCounter++,s->daughter->ID,s->ID);
}


void makeMigrationNode(int type)
{

  SEQUENCE *s = getSequenceWithType(type);
  SEQUENCE *r = newSequence((s->type + 1) % 2);
  r->indegree = 1,
  r->outdegree = 1,
  r->son = s,
  r->Time = newTime,

  s->outdegree = 1;
  s->father = r;

  if (lastTime == NULL) {
    lastTime = r;
    rootTime = r;
  }
  else {
    lastTime->nextTime = r;
    lastTime = r;
  }

  putSequence(r);

  printf("N %i|5|%f|",r->ID,r->Time);
  printf("Time: %f",r->Time);
  printf("\n");

  printf("IN %i|%f#", r->ID, 0.0);
  printf("0.000000,0.500000#");
  printf("\n");

  printf("E %i|%i|%i\n",edgeCounter++,r->son->ID,r->ID);
}

void build(void)
{
  int i;
  double wmean;

  rootTime = lastTime = NULL;
  newTime = 0.0;
  coalescent_events_to_go = num_ini_seq;

  for (i = 0; i < num_ini_seq; i++) {
    SEQUENCE *s = newSequence(i >= num_ini_seq/2);
    putSequence(s);

    printf("N %i|%i|%f|",s->ID,s->type?3:0,s->Time);
    printf("Time:%f\n",s->Time);
    printf("IN %d|",s->ID);
    printf("0.000000,0.500000#");
    printf("|\n");
  }

  while (coalescent_events_to_go > 1) {
    assert(seqs_len > 1);

    int type0 = type_counts[0];
    int type1 = type_counts[1];
    wmean =  (type0*(type0-1))/2;
    wmean += (type1*(type1-1))/2;
    wmean += type0*M1;
    wmean += type1*M2;

    newTime = newTime + exponen(wmean);

    switch (nextEvent()) {
    case 0:
      makeCoalescensNode(0);
      break;
    case 1:
      makeCoalescensNode(1);
      break;
    case 2:
      makeMigrationNode(0);
      break;
    case 3:
      makeMigrationNode(1);
      break;
    default:
      printf("Bad event\n");
    }
  }
}
