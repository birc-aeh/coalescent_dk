#ifndef __sequence_h
#define __sequence_h

#include "structures.h"

void initSequencePool(void);
SEQUENCE *newSequence(void);
void putSequence(SEQUENCE *s);

SEQUENCE *getSequence(int i);
SEQUENCE *getSomeSequence(void);

void traverseTopSeqs(void (*opr)(SEQUENCE *));

#endif
