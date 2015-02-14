#include <stdio.h>
#include <stdlib.h>
#include "memory.h"

extern int mblock_size;

typedef struct MEMORY_BLOCK {
  void *base;
  struct MEMORY_BLOCK *next;
} MEMORY_BLOCK;

static MEMORY_BLOCK *root;
static MEMORY_BLOCK *last;
static unsigned int offset;
static unsigned word_size;

void initMemory(void)
{

  root = last = (MEMORY_BLOCK *)allocate(sizeof(MEMORY_BLOCK));
  root->base = allocate(mblock_size);
  root->next = NULL;
  offset=0;
  word_size = sizeof(int);

}

void *Malloc(unsigned n)
{
  void *result;

  n = n+(8-n%8);

  if ((offset+n)>mblock_size)
    pushMemoryBlock();
  
  result = (void *)((int)last->base+offset);
  offset += n;

  return result;
}

void freeAll(void)
{
  offset = 0;
  last = root;
}

void pushMemoryBlock(void)
{
  if (last->next==NULL) {
    last->next = (MEMORY_BLOCK *)allocate(sizeof(MEMORY_BLOCK));
    last->next->base = allocate(mblock_size);
    last->next->next = NULL;
  }

  last = last->next;
  offset = 0;
}

void *allocate(unsigned n)
{ 
  void *p;
   
  if (!(p = malloc(n))) {
     fprintf(stderr,"Malloc(%d) failed.\n",n);
     fflush(stderr);
     abort();
   }
   return p;
}

