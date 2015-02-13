#include <stdlib.h>
#include <stdio.h>
#include "sequence.h"

extern int alloc_size;

typedef struct SeqArrayPointer {
  SEQUENCE **array;
  struct SeqArrayPointer *next,*prev;
} SeqArrayPointer;

int size;                             /* External - next free slot */

static SeqArrayPointer *root,*last;   /* The root and active sequence array       */

void initSequencePool(void)
{
  size = 0;
  
  root = last = malloc(sizeof(SeqArrayPointer));
  root->array = malloc(alloc_size*sizeof(SEQUENCE *));
  root->next = NULL;
  root->prev = root;
}

/* Create a new sequence. */
SEQUENCE *newSequence(void)
{
  static int ID = 0;

  SEQUENCE *result;
  result = malloc(sizeof(SEQUENCE));
  result->ID = ID++;
  result->Time = 0.0;
  result->nextTime = NULL;
  result->reversed = false;
  result->count = 0;
  //result->y = -1;
  result->matleft = -1.0;

  result->flipson = 0;
  result->flipdaughter = 0;

  result->sonID = -1;
  result->daughterID = -1;
  result->type = -1;

  return result;
}

/* Append sequence to the structure - expanding */
/* it if necessary.                             */
void putSequence(SEQUENCE *s)
{
  int i;

  if (size%alloc_size==0 && size>0) {
    if (last->next==NULL) {
      last->next = malloc(sizeof(SeqArrayPointer));
      last->next->array = malloc(alloc_size*sizeof(SEQUENCE *));
      for (i=0; i<alloc_size; i++)
	last->next->array[i] = NULL;
      last->next->next = NULL;
      last->next->prev = last;
    }
    last = last->next;
  }
  
  last->array[size%alloc_size] = s;
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
  return result;
}  

/* Get a totally random sequence. */
SEQUENCE *getSomeSequence(void)
{
  if (size>0)
    return getSequence(rand()%size);
  else
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

