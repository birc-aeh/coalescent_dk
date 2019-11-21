#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "tree.h"
#include "structures.h"
#include "sequence.h"
#include "terminator.h"

extern int num_ini_seq;   /* Number of initial sequences */

extern int seqs_len;     /* Number of sequences to choose from (k) */
extern double newTime;   /* Elapsed time (backwards)               */

SEQUENCE *rootTime;       /* Base of timeline */
static SEQUENCE *lastTime; /* Timeline */

static int edgeCounter = 0;

extern double M1,M2;
extern int seqs_of_type0;

int nextEvent(void)
{
  double pc1,pc2,pm1,pm2,p;
  int type0 = seqs_of_type0;
  int type1;

  type1 = (seqs_len-type0);
  pc1 = (type0*(type0-1))/2;
  pc2 = (type1*(type1-1))/2;

  pm1 = type0*M1;
  pm2 = type1*M2;

  p = drand48()*(pc1+pc2+pm1+pm2);
  if (p < pc1) return 0;
  p -= pc1;
  if (p < pc2) return 1;
  p -= pc2;
  if (p < pm1) return 2;
  return 3;
}


double exponen(double f)
{
  return -log(drand48())/f;
}

extern INTERVAL *last_make;

void makeCoalescensNode(int type)
{
  SEQUENCE *s,*s1,*s2;
  INTERVAL *i;

  s1 = getSequenceWithType(type);
  s1->outdegree = 1;

  s2 = getSequenceWithType(type);
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
  s->intervals = unite(s1->intervals,s2->intervals);
  s->Time = newTime;

  s1->father = s;
  s2->father = s;

  s->matleft = updateOneK();  
  s->type = type;

  putSequence(s);

  //printf("N %i|%i|%f|",s->ID,s->type+2,s->Time);
  printf("N %i|%i|%f|",s->ID,1,s->Time);
  printf("Time:%f",s->Time);
  printf("\n");

  printf("IN %i|",s->ID);
  prettyInterval(s->intervals);
  printf("|");

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

  printf("\n");

/*    if (s->son->indegree == 1) { */
/*      printf("E %d|%d|%d\n",edgeCounter,s->son->son->ID,s->ID); */
/*      printf("IE %d|",edgeCounter); */
/*      prettyInterval(s->son->intervals); */
/*      printf("\n"); */
/*      edgeCounter++; */
/*    } */
/*    else */
    printf("E %d|%d|%d\n",edgeCounter++,s->son->ID,s->ID);

/*    if (s->daughter->indegree == 1) { */
/*      printf("E %d|%d|%d\n",edgeCounter,s->daughter->son->ID,s->ID); */
/*      printf("IE %d|",edgeCounter); */
/*      prettyInterval(s->daughter->intervals); */
/*      printf("\n"); */
/*      edgeCounter++; */
/*    } */
/*    else */
    printf("E %d|%d|%d\n",edgeCounter++,s->daughter->ID,s->ID);
}


void makeMigrationNode(int type)
{

  SEQUENCE *s, *r;

  s = getSequenceWithType(type);
  if (s->type != type) { fprintf(stderr,"Bad impl\n"); exit(1); }

  r = newSequence();
  r->indegree = 1;
  r->outdegree = 1;
  r->son = s;
  r->type = (s->type+1)%2;
  r->intervals = copyIntervals(s->intervals);

  s->outdegree = 1;
  s->father = r;
  
  r->Time = newTime;

  if (lastTime==NULL) {
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
  //printf("P: %f",r->P);
  printf("\n");
  
  printf("IN %i|%f#",r->ID,r->P);
  prettyInterval(r->intervals);
  printf("\n");

  printf("E %i|%i|%i\n",edgeCounter++,r->son->ID,r->ID);
}


void build(void)
{
  SEQUENCE *s;
  int i,type1;
  double wmean;

  rootTime = lastTime = NULL;
  newTime = 0.0;
  initTerminator();
  initSequencePool();

  for (i=0; i<num_ini_seq; i++) {
    s = newSequence();
    s->indegree = 0;
    s->outdegree = 0;
    s->intervals = initInterval(0, 0.5);
    s->Time = 0.0;
    if (i>=num_ini_seq/2) s->type = 1;

    putSequence(s);

    printf("N %i|%i|%f|",s->ID,s->type?3:0,s->Time);
    printf("Time:%f\n",s->Time);
    printf("IN %d|",s->ID);
    prettyInterval(s->intervals);
    printf("|\n");
  }

  i = num_ini_seq;
  while (!theEnd()) {
    if (seqs_len==1)
      printf("One size!!\n");

    int type0 = seqs_of_type0;
    type1 = (seqs_len-type0);
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


INTERVAL *the_intervals;

void intersectOne(SEQUENCE *s)
{
    intersect(s->intervals,the_intervals);
}

void intersectAll(INTERVAL *i)
{
  if (i==NULL) return;
  the_intervals = i;
  traverseTopSeqs(intersectOne);
}
