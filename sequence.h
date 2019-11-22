#ifndef __sequence_h
#define __sequence_h

#include "structures.h"

SEQUENCE *newSequence(int type);
void putSequence(SEQUENCE *s);

SEQUENCE *getSequenceWithType(int type);

#endif
