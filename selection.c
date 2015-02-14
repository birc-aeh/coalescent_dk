#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <assert.h>

#include "structures.h"
#include "sequence.h"
#include "selection.h"

extern SEQUENCE *rootTime;
extern double M;
extern int num_ini_seq;


//static void removeSingles(SEQUENCE *s

static void compress_path(SEQUENCE *s, SEQUENCE *r, SEQUENCE **field)
{
  if (s->children == 0)
    return;
  SEQUENCE *old;
  do {
    old = r;
    r = r->son;
  } while (r->children==1 && r->parents==1);

  if (old != s) {
    *field = r;
    if (r->father == old)
      r->father = s;
    else
      r->mother = s;
  }
}

static void pointThrough(SEQUENCE *s)
{
  while (s) {
    compress_path(s, s, &s->son);
    if (s->children==2 && 
	s->daughter->children==1 && s->daughter->parents==1) {
      compress_path(s, s->daughter, &s->daughter);
    }
    s = s->prevTime;
  }
}

static void dumpStructure(void)
{
  /* The original probably has a bug here. children 1 outputs 2 and children 2
   * outputs 1 - I'm trying to keep identical output though */
  int extra_field[] = {0,2,1};
  SEQUENCE *s = rootTime;
  while (s) {
    printf("N %i|%i|%f|", s->ID, extra_field[s->children], s->Time);
    printf("Time:%f\n",s->Time);
    if (s->children > 0)
      printf("E %d|%d|%d\n",s->sonID,s->son->ID,s->ID);
    if (s->children > 1)
      printf("E %d|%d|%d\n",s->daughterID,s->daughter->ID,s->ID);
    s = s->nextTime;
  }
}

static void erase_from_mother(SEQUENCE *s)
{
  s->mother->children--;
  if (s->mother->son == s) {
    s->mother->son = s->mother->daughter;
    s->mother->sonID = s->mother->daughterID;
    s->mother->son_is_mutated = s->mother->daughter_is_mutated;
  }

  s->mother->daughter = NULL;

  s->parents--;
  s->mother = NULL;
}

static void erase_from_father(SEQUENCE *s)
{
  s->father->children--;
  if (s->father->son == s) {
    s->father->son = s->father->daughter;
    s->father->sonID = s->father->daughterID;
    s->father->son_is_mutated = s->father->daughter_is_mutated;
  }
  s->father->daughter = NULL;

  s->parents--;
  s->father = s->mother;
  s->mother = NULL;
}


static int isMutated(SEQUENCE *child, SEQUENCE *parent)
{
  if (parent->son == child)
    return parent->son_is_mutated;

  if (parent->daughter == child)
    return parent->daughter_is_mutated;

  fprintf(stderr,"Bad child\n");
  exit(1);
  return 2;
}

static int getType(SEQUENCE *child, SEQUENCE *parent)
{
  return isMutated(child,parent)? !parent->type : parent->type;
}


static void trimSelection(SEQUENCE *r)
{
  while (r) {

    if (r->parents == 1) {
      r->type = getType(r, r->father);
    }
    else if (r->children == 1 && r->parents == 2) {
      int kind_m = getType(r, r->mother);
      int kind_f = getType(r, r->father);

      r->type = kind_m | kind_f;
      if (kind_m)
        erase_from_father(r);
      else
        erase_from_mother(r);
    }
    else if (r->father == NULL) {
      r->type = 1;
    }

    r = r->prevTime;
  }
  
}


static void dumpTree(SEQUENCE *s)
{
  if (s == NULL) return;

  if (s->children == 0 && s->parents == 1) {
    printf("%d ",s->ID);
    return;
  }

  if (s->children == 2 && s->parents < 2) {
    printf("%f ",s->Time);
    dumpTree(s->son);
    dumpTree(s->daughter);
    return;
  }

  fprintf(stderr,"Bad tree (i:%d o:%d)\n",s->children,s->parents);
  exit(1);
}


static double measureEdges(SEQUENCE *s)
{
  double res = 0.0;
  while (s) {
    if (s->children > 0)
      res += s->Time - s->son->Time;
    if (s->children > 1)
      res += s->Time - s->daughter->Time;
    s = s->prevTime;
  }
  return res;
}


static void placeMutation(SEQUENCE *s, double place)
{
  while (s) {
    if (s->children > 0) {
      place = place - (s->Time - s->son->Time);
      if (place < 0.0) s->son_is_mutated = !s->son_is_mutated;
    }
    if (s->children > 1) {
      place = place - (s->Time - s->daughter->Time);
      if (place < 0.0) s->daughter_is_mutated = !s->daughter_is_mutated;
    }
    s = s->prevTime;
  }
}


static void dumpMutations(SEQUENCE *s)
{
  while (s) {
    if (s->children > 0 && s->son_is_mutated)
      printf(" %d",s->sonID);
    if (s->children > 1 && s->daughter_is_mutated)
      printf(" %d",s->daughterID);
    s = s->prevTime;
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

  s->prevTime = NULL;

  while (s->nextTime != NULL) {
    s->nextTime->prevTime = s;
    s = s->nextTime;   
  }
}

static void removeDummies(SEQUENCE **r)
{
  SEQUENCE *s;

  s = *r;
  while (s->children == 1)
    s=s->son;
  s->parents = 0;
  *r = s;

  pointThrough(s);

  s = rootTime;
  while (s->nextTime) {
    if ((s->nextTime->children==1 && s->nextTime->parents<2) ||
	(s->nextTime->children==0 && s->nextTime->parents==0))
      s->nextTime = s->nextTime->nextTime;
    else
      s = s->nextTime;
  }
  
  reverseTime();
}


static int extract_direct_ancestors_of_original_IDs(SEQUENCE *s)
{
  if (s->children == 0)
    return s->ID < num_ini_seq;
  else {
    assert(s->children == 2);
    if (!extract_direct_ancestors_of_original_IDs(s->son)) {
      s->children--;
      s->son = NULL;
    }
    if (!extract_direct_ancestors_of_original_IDs(s->daughter)) {
      s->children--;
      s->daughter = NULL;
    }
    if (s->children == 1 && s->son == NULL) {
      s->son = s->daughter;
      s->daughter = NULL;
    }
    return s->children > 0;
  }
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
    s = s->prevTime;
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


  extract_direct_ancestors_of_original_IDs(s);
  removeDummies(&s);

  printf("ME");
  dumpEdgesInTree(s);
  printf("\n");

  printf("\n0.0000|");
  dumpTree(s);
  printf("\n");
}
