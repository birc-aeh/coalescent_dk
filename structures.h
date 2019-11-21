#ifndef __structures_h
#define __structures_h

#define true 1
#define false 0
#define debug

#ifndef __cplusplus
typedef int bool;
#endif

typedef struct SEQUENCE {
  
  int ID;

  double Time;

  int type;

  struct REALTREE *sub;
  //int seq_number;

  int indegree;
  int outdegree;

  struct SEQUENCE *father;
  struct SEQUENCE *son;
  struct SEQUENCE *daughter;

  struct SEQUENCE *nextTime;
} SEQUENCE;


typedef struct INTERVAL {
  int size;
  double *ranges;
} INTERVAL;

typedef struct REALTREE {
  double time;
  int number;
  struct REALTREE *left,*right;
} REALTREE;


#endif
