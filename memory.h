
#ifndef __memory_h
#define __memory_h

#define NEW(type) (type *)Malloc(sizeof(type))

void initMemory(void);
void *Malloc(unsigned n);
void freeAll(void);

/* private */
void pushMemoryBlock(void);
void *allocate(unsigned n);

#endif
