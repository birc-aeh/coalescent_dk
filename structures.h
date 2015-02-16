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

  double A;
  double Time;
  double P;

  struct REALTREE *sub;
  //int seq_number;

  int indegree;
  int outdegree;

  struct SEQUENCE *father;
  struct SEQUENCE *mother;
  struct SEQUENCE *son;
  struct SEQUENCE *daughter;

  struct SEQUENCE *nextTime;
  int x,y;
  bool reversed;

  struct INTERVAL *intervals;
  struct INTERVAL *gray;
} SEQUENCE;


typedef struct INTERVAL {
  int size;
  struct INTERVALLIST *list;
} INTERVAL;

typedef struct INTERVALLIST {
  double start,end;
  struct INTERVALLIST *prev,*next;
} INTERVALLIST;

typedef struct REALTREE {
  double time;
  int number;
  struct REALTREE *left,*right;
} REALTREE;


#endif
