#ifndef __structures_h
#define __structures_h

#define true 1
#define false 0
#define debug

#ifndef __cplusplus
typedef int bool;
#endif

typedef struct INTERVAL {
  int size;
  double *ranges;
} INTERVAL;

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

  /* Intervals that have yet to find MRCA according to java code - can that be right? intervals == inverse(gray)? */
  struct INTERVAL *intervals;

  /* Intervals that have already found most recent common ancestor (MRCA) */
  struct INTERVAL *gray;
} SEQUENCE;

typedef struct REALTREE {
  double time;
  int number;
  struct REALTREE *left,*right;
} REALTREE;


#endif
