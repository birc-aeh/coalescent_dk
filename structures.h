#ifndef __structures_h
#define __structures_h

#define true 1
#define false 0

#ifndef __cplusplus
typedef int bool;
#endif

typedef struct SEQUENCE {
  
  int ID;

  double Time;

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

} SEQUENCE;

#endif
