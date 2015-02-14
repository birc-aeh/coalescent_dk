#include <stdio.h>
#include <stdlib.h>
#include "sets.h"
#include "terminator.h"

static double fmin(double a,double b)
{
  return (a<b?a:b);
}

static double fmax(double a, double b)
{
  return (a>b?a:b);
}

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

INTERVAL *emptyInterval(void)
{
  INTERVAL *result;
  result = malloc(sizeof(INTERVAL));
  result->size = 0;
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

/* Return start-point of first part-interval in <i>. */
double getX1(INTERVAL *i)
{
  return i->list->start;
}

/* Return end-point of last part-interval in <i>. */
double getYn(INTERVAL *i)
{
  return i->list->prev->end;
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

/* Insert new element after <i>, returning */
/* a reference to the new element.         */
INTERVALLIST *prependInterval(INTERVALLIST *i)
{
  if (i!=NULL) {
    INTERVALLIST *new;
    new = malloc(sizeof(INTERVALLIST));
    new->next = i;
    new->prev = i->prev;
    i->prev = new;
    new->prev->next = new;
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


/* Unite intervals <i1> with <i2> */
INTERVAL *unite(INTERVAL *i1, INTERVAL *i2)
{
  INTERVAL *i;
  INTERVALLIST *result,*il1,*il2;
  int size,pos1,pos2;
  double seen;
  bool success;

  /* Update terminator */
  il1 = i1->list;
  il2 = i2->list;
  pos1=0; pos2=0;
  
  while ((pos1<i1->size) && (pos2<i2->size)) {
    if ((il1->end > il2->start) && (il1->end <= il2->end)) {
      updateCoalescens(fmax(il1->start,il2->start),il1->end);
      il1 = il1->next;
      pos1++;
    }
    else if ((il2->end > il1->start) && (il2->end <= il1->end)) {
      updateCoalescens(fmax(il1->start,il2->start),il2->end);
      il2 = il2->next;
      pos2++;
    }
    else if (il1->end <= il2->start) {
      il1 = il1->next;
      pos1++;
    }
    else {
      il2 = il2->next;
      pos2++;
    }
  }
  
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


/* Intersect intervals <dest> with <i>. */
void intersect(INTERVAL *dest, INTERVAL *i)
{
  INTERVALLIST *il2;
  INTERVALLIST *il1;
  INTERVALLIST *result;

  int pos1,pos2,size;
  bool success;

  il1 = dest->list;
  il2 = i->list;
  result = NULL;

  pos1=0; pos2=0; size=0;
  while (pos1<dest->size) {
    success = true;
    while (success) {
      success = false;
      if ((il1->start >= il2->start) && (il1->start < il2->end)) {
	result = appendInterval(result);
	size++;
	result->start = il1->start;
	result->end = fmin(il1->end,il2->end);
      }
      else if ((il1->end > il2->start) && (il1->end <= il2->end)) {
	result = appendInterval(result);
	size++;
	result->start = fmax(il2->start,il2->start);
	result->end = il1->end;
      }
      else if ((il2->start > il1->start) && (il2->end < il1->end)) {
	result = appendInterval(result);
	size++;
	result->start = il2->start;
	result->end = il2->end;
      }
      if (il1->end > il2->end) {
	il2 = il2->next;
	pos2++;
	if (pos2==i->size)
	  pos1=dest->size;
	else
	  success = true;
      }

    }
    il1 = il1->next;
    pos1++;
  }

  dest->list = (result==NULL ? NULL:result->next);
  dest->size = size;

}


/* Make copy af intervals in <i1> up to point <P>. */
INTERVAL *intersectTo(INTERVAL *i1, double P)
{
  INTERVAL *result;
  INTERVALLIST *i,*l;
  int j;

  result = malloc(sizeof(INTERVAL));
  result->size = 0;
  i = NULL;

  l = i1->list;
  for (j=0; j<i1->size; j++) {
    if (l->end <= P) {
      i = appendInterval(i);
      result->size++;
      cloneInterval(i,l);
      l = l->next;
    }
    else if (l->start < P) {
      i = appendInterval(i);
      result->size++;
      i->start = l->start;
      i->end = P;
      result->list = i->next;
      return result;
    }
    else 
      break;
  }
  result->list = (i==NULL ? NULL:i->next);
  return result;
}

/* Make a copy of intervals in <i1> starting at point <P>. */
INTERVAL *intersectFrom(INTERVAL *i1, double P)
{
  INTERVAL *result;
  INTERVALLIST *i,*l;
  int j;
  
  result = malloc(sizeof(INTERVAL));
  result->size = 0;
  i = NULL;

  if (i1->size > 0) {
    l = i1->list->prev;
    for (j=0; j<i1->size; j++) {
      if (l->start >= P) {
	i = prependInterval(i);
	result->size++;
	cloneInterval(i,l);
	l = l->prev;
      }
      else if (l->end > P) {
	i = prependInterval(i);
	result->size++;
	i->start = P;
	i->end = l->end;
	result->list = i;
	return result;
      }
      else 
	break;
    }
  }
  result->list = i;
  return result;
}

extern int R;

INTERVAL *inverse(INTERVAL *i1)
{
  INTERVALLIST *il,*result;
  INTERVAL *i;
  int pos,size;

  il = i1->list;
  pos = 0; size = 0;
  result = NULL;

  if (i1->size==0)
    return NULL;

  if (il->start!=0.0) {
    result = appendInterval(result);
    result->start = 0.0;
    result->end = il->start;
    size++;
  }

  while (pos<i1->size) {
    if (il->end==(double)R/2.0) {
      break;
    }
    result = appendInterval(result);
    size++;
    result->start = il->end;
    if (il->next->start < il->end) 
      result->end = R/2.0;
    else
      result->end = il->next->start;
    il=il->next;
    pos++;
  }
      
  i = malloc(sizeof(INTERVAL));
  i->size = size;
  i->list = (size==0)?NULL:result->next;
  return i;
}

void prettyInterval(INTERVAL *i)
{
  INTERVALLIST *l;
  int j;

  if (i==NULL) {
    printf("<null>\n");
    return;
  }

  l = i->list;
  for (j=0; j<i->size; j++) {
    printf("%f,%f#",l->start,l->end);
    //if (j<i->size-1) printf("#");
    l = l->next;
  }
  //printf("\n");
}
