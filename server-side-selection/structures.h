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

  int children;
  int parents;

  struct SEQUENCE *father;
  struct SEQUENCE *mother;
  struct SEQUENCE *son;
  struct SEQUENCE *daughter;

  struct SEQUENCE *nextTime;
  struct SEQUENCE *prevTime;

  int son_is_mutated, daughter_is_mutated;
  int sonID, daughterID;
  int type;

} SEQUENCE;

#endif
