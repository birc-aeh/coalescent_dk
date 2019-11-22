#ifndef __sequence_h
#define __sequence_h

#include "structures.h"

SEQUENCE *newSequence(void);
void putSequence(SEQUENCE *s);

SEQUENCE *getSequenceWithType(int type);

#endif
