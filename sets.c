#include <stdio.h>
#include <stdlib.h>
#include "sets.h"

/* Make an interval with one entry (<x1>,<y1>). */
INTERVAL *initInterval(double x1, double y1)
{
  INTERVAL *result;
  result = malloc(sizeof(INTERVAL));
  result->size = 1;
  result->list = appendInterval(NULL);
  result->list->start = x1;
  result->list->end = y1;
  return result;
}

/* Return an exact copy of interval <i>. */
INTERVAL *copyIntervals(INTERVAL *i)
{
  INTERVAL *new;
  INTERVALLIST *ln,*lo;
  int j;

  new = malloc(sizeof(INTERVAL));
  new->size = i->size;
  lo = i->list;
  ln = NULL;
  for (j=0; j<new->size; j++) {
    ln = appendInterval(ln);
    ln->start = lo->start;
    ln->end = lo->end;
    lo = lo->next;
  }
  if (ln!=NULL)
    new->list = ln->next;
  else
    new->list = NULL;
  return new;
}

/* Insert new element after <i>, returning */
/* a reference to the new element.         */
INTERVALLIST *appendInterval(INTERVALLIST *i)
{
  if (i!=NULL) {
    INTERVALLIST *new;
    new = malloc(sizeof(INTERVALLIST));
    new->next = i->next;
    new->prev = i;
    i->next = new;
    new->next->prev = new;
    i = new;
  } 
  else {
    i = malloc(sizeof(INTERVALLIST));
    i->next = i->prev = i;
  }
  return i;
}

/* Copy values in <source> interval to <dest> interval */
void cloneInterval(INTERVALLIST *dest, INTERVALLIST *source)
{
  dest->start = source->start;
  dest->end = source->end;
}

/* Unite without updating terminator */
INTERVAL *uniteNoTerm(INTERVAL *i1, INTERVAL *i2)
{
  INTERVAL *i;
  INTERVALLIST *result,*il1,*il2;
  int size,pos1,pos2;
  double seen;
  bool success;
  
  /* Build united interval list */
  il1 = i1->list;
  il2 = i2->list;
  result = NULL;
  pos1=0; pos2=0; size=0; seen=0.0;

  while ((pos1<i1->size) && (pos2<i2->size)) {
    result = appendInterval(result);
    size++;
    if (il1->start < il2->start) {
      result->start = il1->start;
      seen = il1->end;
    }
    else {
      result->start = il2->start;
      seen = il2->end;
    }

    success = true;
    while (success) {
      if ((seen >= il1->start) && (seen <= il1->end) && (pos1<i1->size)) {	
	seen = il1->end;
	il1 = il1->next;
	pos1++;
      }
      else if ((seen >= il2->start) && (seen <= il2->end) && (pos2<i2->size)) {
	seen = il2->end;
	il2 = il2->next;
	pos2++;
      }
      else 
	success = false;
      
      while ((il1->end < seen) && (pos1 < i1->size)) {
	il1 = il1->next;
	pos1++;
      }
      while ((il2->end < seen) && (pos2 < i2->size)) {
	il2 = il2->next;
	pos2++;
      }

      if (!success) 
	result->end = seen;
    }
  }

  while (pos1<i1->size) {
    result = appendInterval(result);
    size++;
    cloneInterval(result,il1);
    il1 = il1->next;
    pos1++;
  }

  while (pos2<i2->size) {
    result = appendInterval(result);
    size++;
    cloneInterval(result,il2);
    il2 = il2->next;
    pos2++;
  }

  i = malloc(sizeof(INTERVAL));
  i->size = size;
  i->list = (result==NULL ? NULL:result->next);

  return i;
}

