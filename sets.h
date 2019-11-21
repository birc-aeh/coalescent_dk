#ifndef __sets_h
#define __sets_h

#include "structures.h"

INTERVAL *initInterval(double x1, double y1);
INTERVAL *copyIntervals(INTERVAL *i);

INTERVAL *unite(INTERVAL *i1, INTERVAL *i2);
INTERVAL *uniteNoTerm(INTERVAL *i1, INTERVAL *i2);

void intersect(INTERVAL *dest, INTERVAL *i);

INTERVAL *inverse(INTERVAL *i1);

void prettyInterval(INTERVAL *i);


/* Private */
INTERVALLIST *appendInterval(INTERVALLIST *i);

#endif
