#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#include "structures.h"
#include "sequence.h"
#include "selection.h"

extern SEQUENCE *rootTime;
extern double M;
extern int num_ini_seq;


//static void removeSingles(SEQUENCE *s


static void pointThrough(SEQUENCE *s)
{
  SEQUENCE *r,*old;

  //if (s == NULL) return;
  //if (s->indegree == 0) return;

  while (s) {

    if (s->indegree == 0) { s = s->revTime; continue; }

    r = s;
    do {
      old = r;
      r = r->son;
    } while (r->indegree==1 && r->outdegree==1);
  
    if (old != s) {
      s->son = r;
      if (r->father == old)
	r->father = s;
      else
	r->mother = s;
    }

    if (s->indegree==2 && 
	s->daughter->indegree==1 && s->daughter->outdegree==1) {
    
      r = s->daughter;
      do {
	old = r;
	r = r->son;
      } while (r->indegree==1 && r->outdegree==1);
    
      if (old != s) {
	s->daughter = r;
	if (r->father == old)
	  r->father = s;
	else
	  r->mother = s;
      }
    }
    
    s = s->revTime;
  }

}



static void dumpStructure(void)
{
  SEQUENCE *s;

  s = rootTime;

  while (s) {
    switch (s->indegree) {      
    case 0:
      printf("N %i|0|%f|",s->ID,s->Time);
      printf("Time:%f\n",s->Time);
      break;
    case 1:
      printf("N %i|2|%f|",s->ID,s->Time);
      printf("Time:%f\n",s->Time);
      printf("E %i|%i|%i\n",s->sonID,s->son->ID,s->ID);
      break;
    case 2:      
      printf("N %i|1|%f|",s->ID,s->Time);
      printf("Time:%f\n",s->Time);
      printf("E %d|%d|%d\n",s->sonID,s->son->ID,s->ID);
      printf("E %d|%d|%d\n",s->daughterID,s->daughter->ID,s->ID);
      break;
    }
    s = s->nextTime;
  }

}

static void eraseIncoming(SEQUENCE *s)
{
  s->mother->indegree--;
  if (s->mother->son == s) {
    s->mother->son = s->mother->daughter;
    s->mother->sonID = s->mother->daughterID;
    s->mother->flipson = s->mother->flipdaughter;
  }

  s->mother->daughter = NULL;

  s->outdegree--;
  s->mother = NULL;
}

static void eraseContinuing(SEQUENCE *s)
{
  s->father->indegree--;
  if (s->father->son == s) {
    s->father->son = s->father->daughter;
    s->father->sonID = s->father->daughterID;
    s->father->flipson = s->father->flipdaughter;
  }
  s->father->daughter = NULL;

  s->outdegree--;
  s->father = s->mother;
  s->mother = NULL;
}


static int isFlip(SEQUENCE *child, SEQUENCE *parent)
{
  if (parent->son == child)
    return parent->flipson;

  if (parent->daughter == child)
    return parent->flipdaughter;

  fprintf(stderr,"Bad child\n");
  exit(1);
  return 2;
}

static int getType(SEQUENCE *child, SEQUENCE *parent)
{
  return isFlip(child,parent)?!parent->type:parent->type;
}


static void trimSelection(SEQUENCE *r)
{
  int kind;

  while (r) {

    while (1) {
      if (r->outdegree==1) {
	r->type = getType(r,r->father);
	break;
      }

      if (r->indegree==1 && r->outdegree==2) {
	kind  = getType(r,r->mother) << 1;
	kind |= getType(r,r->father);

	switch (kind) {
	case 0:
	  eraseIncoming(r);
	  r->type = 0;
	  break;
	case 1:
	  eraseIncoming(r);
	  r->type = 1;
	  break;
	case 2:
	case 3:	
	  eraseContinuing(r);
	  r->type = 1;
	  break;
	default:
	  fprintf(stderr,"Bad kind %d\n",kind);
	  exit(1);
	}
	break;
      }

      if (r->father == NULL) {
	r->type = 1;
	break;
      }

      break;
    }

    r = r->revTime;
  }
  
}


static void dumpTree(SEQUENCE *s)
{
  if (s == NULL) return;

  if (s->indegree == 0 && s->outdegree == 1) {
    printf("%d ",s->ID);
    return;
  }

  if (s->indegree == 2 && s->outdegree < 2) {
    printf("%f ",s->Time);
    dumpTree(s->son);
    dumpTree(s->daughter);
    return;
  }

  fprintf(stderr,"Bad tree (i:%d o:%d)\n",s->indegree,s->outdegree);
  exit(1);
}


static double measureEdges(SEQUENCE *s)
{
  double res;

  res = 0.0;
  while (s) {
    switch (s->indegree) {
    case 0:
      break;
    case 1:
      res += s->Time-s->son->Time;
      break;
    case 2:
      res += (s->Time-s->son->Time)+(s->Time-s->daughter->Time);
      break;    
    }
    s = s->revTime;
  }

  return res;
}


static void placeMutation(SEQUENCE *s, double place)
{

  while (s) {
    switch (s->indegree) {
    case 0:
      break;
    case 1:
      place = place - (s->Time-s->son->Time);
      if (place < 0.0) s->flipson = !s->flipson;
      break;
    case 2:
      place = place - (s->Time-s->son->Time);
      if (place < 0.0) s->flipson = !s->flipson;

      place = place - (s->Time-s->daughter->Time);
      if (place < 0.0) s->flipdaughter = !s->flipdaughter;
      break;
    }
    s = s->revTime;
  }

  if (place >= 0.0) {
    printf("Bad place mutation %f\n",place);
    exit(1);
  }
}


static void dumpMutations(SEQUENCE *s)
{
  while (s) {
    switch (s->indegree) {
    case 0:
      break;
    case 1:
      if (s->flipson) printf(" %d",s->sonID);
      break;
    case 2:
      if (s->flipson) printf(" %d",s->sonID);
      if (s->flipdaughter) printf(" %d",s->daughterID);
      break;
    default:
      break;
    }
    s = s->revTime;
  }
}





static int poissonDist(double mean)
{
  double U,Nom,Den;
  int i;

  U = drand48()/exp(-mean);
  Nom = 1.0;
  Den = 1.0;

  for (i=0; U>0.0; i++) {
    U -= Nom/Den;
    Nom *= mean;
    Den *= (double)(i+1);
  }
  
  return i;
}


static void selectionMutation(SEQUENCE *s)
{
  double length,place;
  int i,muts;

  length = measureEdges(s);
  muts = poissonDist(M);

  for (i=0; i<muts; i++) {
    place = drand48()*length;
    placeMutation(s,place);
  }
}


void reverseTime(void)
{
  SEQUENCE *s = rootTime;

  s->revTime = NULL;

  while (s->nextTime != NULL) {
    s->nextTime->revTime = s;
    s = s->nextTime;   
  }
}

static void removeDummies(SEQUENCE **r)
{
  SEQUENCE *s;

  s = *r;
  while (s->indegree == 1)
    s=s->son;
  s->outdegree = 0;
  *r = s;

  pointThrough(s);

  s = rootTime;
  while (s->nextTime) {
    if ((s->nextTime->indegree==1 && s->nextTime->outdegree<2) ||
	(s->nextTime->indegree==0 && s->nextTime->outdegree==0))
      s->nextTime = s->nextTime->nextTime;
    else
      s = s->nextTime;
  }
  
  reverseTime();
}


static int extractTree(SEQUENCE *s)
{
  int good;

  if (s==NULL) return 1;

  switch (s->indegree) {
  case 0:
    good = s->ID<num_ini_seq;
    if (!good) s->outdegree=0;
    return good;
  case 1:
    fprintf(stderr,"Bad trim\n");
    exit(1);
  case 2:
    if (!extractTree(s->son)) {
      s->indegree--;
      s->son = NULL;
    }
    if (!extractTree(s->daughter)) {
      s->indegree--;
      s->daughter = NULL;
    }
    if (s->indegree==1 && s->son==NULL) {
      s->son = s->daughter;
      s->daughter = NULL;
    }
    good = s->indegree>0;
    if (!good) s->outdegree=0;
    return good;
  }
    
  fprintf(stderr,"Bad extract\n");
  exit(1);
  return -1;
}


void dumpEdgesInTree(SEQUENCE *s)
{
  if (s->son == NULL) return;
  
  printf(" %d %d",s->sonID,s->daughterID);
  dumpEdgesInTree(s->son);
  dumpEdgesInTree(s->daughter);
}

void dumpType(SEQUENCE *s, int t)
{
  while (s) {
    if (s->type == t) printf(" %d",s->ID);
    s = s->revTime;
  }
}


void makeSelection(void)
{
  SEQUENCE *s = getSomeSequence();

  if (s==NULL) {
    fprintf(stderr,"Bad sequence\n");
    exit(1);
  }

  reverseTime();
  removeDummies(&s);

  selectionMutation(s);
  dumpStructure();

  printf("\nMU");
  dumpMutations(s);
  printf("\n");

  reverseTime();
  trimSelection(s);
  printf("T0");
  dumpType(s,1);
  printf("\nT1");
  dumpType(s,0);
  printf("\n");  
  removeDummies(&s);


  extractTree(s);
  removeDummies(&s);

  printf("ME");
  dumpEdgesInTree(s);
  printf("\n");

  printf("\n0.0000|");
  dumpTree(s);
  printf("\n");
}
