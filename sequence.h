#ifndef __sequence_h
#define __sequence_h

#include "structures.h"

void initSequencePool(void);
SEQUENCE *newSequence(void);
void putSequence(SEQUENCE *s);

SEQUENCE *getSequence(int i);
SEQUENCE *getSomeSequence(void);
SEQUENCE *getWeightedSequence(void);

void recalculateA(void);
void recalculateAllA(void);

void traverseTopSeqs(void (*opr)(SEQUENCE *));
void traverseSeqs(void (*opr)(SEQUENCE *));

SEQUENCE *getSequenceWithType(int type);

/* private */
void handleSeq(SEQUENCE *s, void (*opr)(SEQUENCE *));

#endif
