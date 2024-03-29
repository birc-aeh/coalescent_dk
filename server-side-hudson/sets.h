#ifndef __sets_h
#define __sets_h

#include "structures.h"

INTERVAL *emptyInterval(void);
INTERVAL *initInterval(double x1, double y1);
INTERVAL *copyIntervals(INTERVAL *i);

typedef void coal_callback(double, double);
INTERVAL *unite(INTERVAL *i1, INTERVAL *i2, coal_callback);
INTERVAL *uniteNoTerm(INTERVAL *i1, INTERVAL *i2);

void intersect(INTERVAL *dest, INTERVAL *i);
INTERVAL *intersectTo(INTERVAL *i1, double P);
INTERVAL *intersectFrom(INTERVAL *i1, double P);

INTERVAL *inverse(INTERVAL *i1);

double getX1(INTERVAL *i);
double getYn(INTERVAL *i);

void prettyInterval(INTERVAL *i);

#endif
