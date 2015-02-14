#include <stdio.h>
#include <stdlib.h>
#include "memory.h"

void initMemory(void)
{ }

void *Malloc(unsigned n)
{
  void *p;
   
  if (!(p = malloc(n))) {
    fprintf(stderr,"Malloc(%d) failed.\n",n);
    fflush(stderr);
    abort();
  }
  return p;
}

void freeAll(void)
{ }

void pushMemoryBlock(void)
{ }

void *allocate(unsigned n)
{ return NULL; }

