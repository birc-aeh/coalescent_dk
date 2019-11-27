#ifndef __structures_h
#define __structures_h

#include <stdbool.h>

typedef struct SEQUENCE {
  int ID;
  double Time;
  int type;

  struct REALTREE *sub;

  int indegree;
  int outdegree;

  struct SEQUENCE *father;
  struct SEQUENCE *son;
  struct SEQUENCE *daughter;

  struct SEQUENCE *nextTime;
} SEQUENCE;

typedef struct REALTREE {
  double time;
  int number;
  struct REALTREE *left,*right;
} REALTREE;

#endif
