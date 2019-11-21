#ifndef __sequence_h
#define __sequence_h

#include "structures.h"

void initSequencePool(void);
SEQUENCE *newSequence(void);
void putSequence(SEQUENCE *s);

SEQUENCE *getSequence(int i);

void traverseTopSeqs(void (*opr)(SEQUENCE *));

SEQUENCE *getSequenceWithType(int type);

#endif
