#include <stdlib.h>
#include <stdio.h>
#include "sequence.h"

int seqs_len = 0; /* External - number of sequences in global list */
static int seqs_alloc = 0;
static SEQUENCE **seqs = NULL;

void initSequencePool(void)
{
  seqs_len = 0;
  seqs_alloc = 1000;
  seqs = malloc(seqs_alloc*sizeof(SEQUENCE *));
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
  if (seqs_len == seqs_alloc) {
    seqs_alloc *= 2;
    seqs = realloc(seqs, seqs_alloc*sizeof(SEQUENCE*));
  }
  seqs[seqs_len] = s;
  seqs_len += 1;
}

/* Get sequence at index i - move the last sequence */ 
/* in the structure to the freed slot.              */
SEQUENCE *getSequence(int i)
{
  if (i >= seqs_len) {
    fprintf(stderr,"Index out of range: getSequence(%i)\n",i);
    exit(1);
  }

  SEQUENCE *result = seqs[i];
  seqs[i] = seqs[seqs_len-1];
  seqs_len -= 1;
  return result;
}  

/* Get a totally random sequence. */
SEQUENCE *getSomeSequence(void)
{
  if (seqs_len>0)
    return getSequence(rand()%seqs_len);
  else
    return NULL;
}

/* Iterate through the sequences in the structure, */
/* calling function <opr> with each sequence.      */
void traverseTopSeqs(void (*opr)(SEQUENCE *))
{
  int i;
  for (i = 0; i < seqs_len; i++)
    opr(seqs[i]);
}

