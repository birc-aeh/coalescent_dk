#ifndef __sets_h
#define __sets_h

#include "structures.h"

INTERVAL *initInterval(double x1, double y1);
INTERVAL *copyIntervals(INTERVAL *i);

typedef void coal_callback(double, double);
INTERVAL *unite(INTERVAL *i1, INTERVAL *i2, coal_callback);
INTERVAL *uniteNoTerm(INTERVAL *i1, INTERVAL *i2);

void prettyInterval(INTERVAL *i);

#endif
