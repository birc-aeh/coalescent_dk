#include <stdlib.h>
#include <stdio.h>
#include "sequence.h"
#include "memory.h"

int seqs_of_type0 = 0; /* External - count of type 0 elements in global list */
int seqs_len = 0;      /* External - number of sequences in global list */
static int seqs_alloc = 0;
static SEQUENCE **seqs = NULL;

/* Create a new sequence. */
SEQUENCE *newSequence(void)
{
  static int ID = 0;

  SEQUENCE *result;
  result = NEW(SEQUENCE);
  result->ID = ID++;
  result->Time = 0.0;
  result->nextTime = NULL;
  result->type = 0;
  return result;
}

/* Append sequence to the structure - expanding */
/* it if necessary.                             */
void putSequence(SEQUENCE *s)
{
  if (seqs_len == seqs_alloc) {
    seqs_alloc = seqs_alloc == 0? 1000 : seqs_alloc*2;
    seqs = realloc(seqs, seqs_alloc*sizeof(SEQUENCE*));
  }
  seqs[seqs_len] = s;
  seqs_len += 1;

  if (s->type == 0)
    seqs_of_type0 += 1;
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

  if (result->type == 0)
    seqs_of_type0 -= 1;
  return result;
}

SEQUENCE *getSequenceWithType(int type)
{
  int num,i;

  if (type == 0)
    num = (int)(drand48()*seqs_of_type0);
  else
    num = (int)(drand48()*(seqs_len-seqs_of_type0));

  for (i = 0; i < seqs_len; i++) {
    if (seqs[i]->type == type) {
      if (num == 0)
        return getSequence(i);
      num -= 1;
    }
  }

  return NULL;
}
