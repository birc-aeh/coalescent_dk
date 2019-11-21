#include <stdlib.h>
#include <stdio.h>
#include "sequence.h"
#include "memory.h"

extern int alloc_size;

typedef struct SeqArrayPointer {
  SEQUENCE **array;
  struct SeqArrayPointer *next,*prev;
} SeqArrayPointer;

int size;                             /* External - next free slot */
int type0;

static bool flipflop;                 /* When traversing - the state of <visited> */
static SeqArrayPointer *root,*last;   /* The root and active sequence array       */

void initSequencePool(void)
{
  size = 0;
  type0 = 0;

  flipflop = false;
  
  root = last = NEW(SeqArrayPointer);
  root->array = calloc(alloc_size, sizeof(SEQUENCE *));
  root->next = NULL;
  root->prev = root;
}

/* Create a new sequence. */
SEQUENCE *newSequence(void)
{
  static int ID = 0;

  SEQUENCE *result;
  result = NEW(SEQUENCE);
  result->ID = ID++;
  result->visited = flipflop;
  result->Time = 0.0;
  result->nextTime = NULL;
  result->reversed = false;
  result->x = -1;
  result->y = -1;
  result->matleft = -1.0;
  result->type = 0;
  return result;
}

/* Append sequence to the structure - expanding */
/* it if necessary.                             */
void putSequence(SEQUENCE *s)
{
  int i;

  if (size%alloc_size==0 && size>0) {
    if (last->next==NULL) {
      last->next = NEW(SeqArrayPointer);
      last->next->array = calloc(alloc_size, sizeof(SEQUENCE *));
      for (i=0; i<alloc_size; i++)
	last->next->array[i] = NULL;
      last->next->next = NULL;
      last->next->prev = last;
    }
    last = last->next;
  }
  
  last->array[size%alloc_size] = s;

  if (s->type == 0) type0++;
  size++;
}

/* Get sequence at index i - move the last sequence */ 
/* in the structure to the freed slot.              */
SEQUENCE *getSequence(int i)
{
  SEQUENCE *result;
  SeqArrayPointer *table;
  int j;

  if (i>=size) {
    fprintf(stderr,"Index out of range: getSequence(%i)\n",i);
    exit(1);
  }

  table = root;
  for(j=0; j<(i/alloc_size); j++)
    table = table->next;

  result = table->array[i%alloc_size];
  size--;

  table->array[i%alloc_size] = last->array[size%alloc_size];
  last->array[size%alloc_size] = NULL;

  if (size%alloc_size == 0) {
    last = last->prev;
  }

  if (result->type == 0) type0--;
  return result;
}  

SEQUENCE *getSequenceWithType(int type)
{
  int num,i,j;
  SeqArrayPointer *sap;

  if (type == 0)
    num = (int)(drand48()*type0);
  else
    num = (int)(drand48()*(size-type0));

  sap = root;
  for (i=0; i<size; i+=alloc_size) {
      for (j=0; j<alloc_size; j++) {
	if (sap->array[j]->type == type) {
	  if (num == 0) return getSequence(i+j);
	  num--;
	}
      }
      sap = sap->next;
  }

  return NULL;
}

/* Iterate through the sequences in the structure, */
/* calling function <opr> with each sequence.      */
void traverseTopSeqs(void (*opr)(SEQUENCE *))
{
  SeqArrayPointer *sap;
  int j;
  
  sap = root;
  while (sap!=NULL) {
    for (j=0; j<alloc_size; j++) {
      if (sap->array[j]==NULL) 
	return;
      opr(sap->array[j]);
    }
    sap = sap->next;
  }
}

/* Graph-traversal help function */
void handleSeq(SEQUENCE *s, void (*opr)(SEQUENCE *))
{
  if (s==NULL) return;
  if (s->visited==flipflop) return;
  s->visited = flipflop;

  switch (s->indegree) {
  case 1:
    handleSeq(s->son,opr);
    break;
  case 2:
    handleSeq(s->son,opr);
    handleSeq(s->daughter,opr);
    break;
  default:
    break;
  }

  if (!(s->indegree==1 && s->outdegree==1))
    (*opr)(s);
}



