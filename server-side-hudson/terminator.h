
#ifndef __terminator_h
#define __terminator_h

#include "structures.h"

void initTerminator(void);
bool updateRecombination(double P);
void updateCoalescens(double from, double to);
bool updateOneK(INTERVAL **last_make);
bool theEnd(void);


void makeRealTree();

void prettyTerm(void);

#endif
