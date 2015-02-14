#ifndef _rootpoints_h
#define _rootpoints_h

#include "structures.h"

void initRootPoints(void);
void addRootPoint(INTERVAL *i, SEQUENCE *s);
SEQUENCE *getRootOf(double P);
void sortRootPoints(void);

void prettyRootPoints(void);



#endif
