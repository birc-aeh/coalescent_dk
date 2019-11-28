#include <stdlib.h>
#include <stdio.h>

#include "sequence.h"
#include "sets.h"

int seqs_len = 0;                /* External - next free slot */
static int seqs_alloc = 0;
static SEQUENCE **seqs = NULL;

double sumA(void)
{
  double res = 0.0;
  int i = 0;
  for (; i < seqs_len; i++)
    res += seqs[i]->A;
  return res;
}

void initSequencePool(void)
{
  seqs_alloc = 1000;
  seqs = malloc(seqs_alloc * sizeof(SEQUENCE*));
}

/* Create a new sequence. */
SEQUENCE *newSequence(void)
{
  static int ID = 0;

  SEQUENCE *result = calloc(1, sizeof(SEQUENCE));
  result->ID = ID++;
  result->gray = emptyInterval();
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
  if (seqs_len > 0)
    return getSequence(rand() % seqs_len);
  else
    return NULL;
}

/* Get a random sequence where each sequence has weight A/sumA */
SEQUENCE *getWeightedSequence(void)
{
  if (seqs_len == 0)
    return NULL;

  double random = sumA()*drand48();
  double sum = 0.0;
  int i;
  for (i = 0; i < seqs_len; i++)
  {
    SEQUENCE *s = seqs[i];
    sum += s->A;
    if (random <= sum)
      return getSequence(i);
  }
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
