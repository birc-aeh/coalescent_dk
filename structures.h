#ifndef __structures_h
#define __structures_h

#define true 1
#define false 0

#ifndef __cplusplus
typedef int bool;
#endif

typedef struct SEQUENCE {
  
  int ID;

  double A;
  double Time;
  double P;
  bool visited;
  double matleft;

  struct REALTREE *sub;
  //int seq_number;

  int indegree;
  int outdegree;

  struct SEQUENCE *father;
  struct SEQUENCE *mother;
  struct SEQUENCE *son;
  struct SEQUENCE *daughter;

  struct SEQUENCE *nextTime;
  struct SEQUENCE *revTime;

  int count;
  bool reversed;

  int flipson, flipdaughter;
  int sonID, daughterID;
  int type;

  struct INTERVAL *intervals;
} SEQUENCE;


typedef struct INTERVAL {
  int size;
  struct INTERVALLIST *list;
} INTERVAL;

typedef struct INTERVALLIST {
  double start,end;
  struct INTERVALLIST *prev,*next;
} INTERVALLIST;


#endif
