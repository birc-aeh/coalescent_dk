#include <stdlib.h>
#include <stdio.h>
#include "sequence.h"

int seqs_len = 0; /* External - number of sequences in global list */
static int seqs_alloc = 0;
static SEQUENCE **seqs = NULL;

/* Create a new sequence. */
SEQUENCE *newSequence(void)
{
  static int ID = 0;

  SEQUENCE *result;
  result = malloc(sizeof(SEQUENCE));
  result->ID = ID++;
  result->Time = 0.0;
  result->nextTime = NULL;

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
    seqs_alloc = seqs_alloc == 0? 1000 : seqs_alloc*2;
    seqs = realloc(seqs, seqs_alloc*sizeof(SEQUENCE*));
  }
  seqs[seqs_len] = s;
  seqs_len += 1;
}

/* Get a totally random sequence. */
SEQUENCE *getSomeSequence(void)
{
  if (seqs_len>0) {
    int i = rand()%seqs_len;
    SEQUENCE *result = seqs[i];
    seqs[i] = seqs[seqs_len-1];
    seqs_len -= 1;
    return result;
  }
  else
    return NULL;
}

