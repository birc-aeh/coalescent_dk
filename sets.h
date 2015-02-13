#ifndef __sets_h
#define __sets_h

#include "structures.h"

INTERVAL *initInterval(double x1, double y1);
INTERVAL *copyIntervals(INTERVAL *i);

INTERVAL *uniteNoTerm(INTERVAL *i1, INTERVAL *i2);

void intersect(INTERVAL *dest, INTERVAL *i);

INTERVAL *inverse(INTERVAL *i1);

double getX1(INTERVAL *i);
double getYn(INTERVAL *i);

void prettyInterval(INTERVAL *i);


/* Private */
INTERVALLIST *appendInterval(INTERVALLIST *i);
void cloneInterval(INTERVALLIST *dest, INTERVALLIST *source);

#endif
