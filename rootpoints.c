#include <stdlib.h>
#include <stdio.h>
#include "rootpoints.h"
#include "memory.h"

typedef struct RPOINT {
  double from,to;
  SEQUENCE *point;
  struct RPOINT *next;
} RPOINT;

static RPOINT *root;

void initRootPoints(void) 
{
  root = NULL;
}

void addRootPoint(INTERVAL *i, SEQUENCE *s)
{
  RPOINT *new;
  INTERVALLIST *l;
  int j;

  if (i==NULL) return;
  
  l = i->list;
  for (j=0; j<i->size; j++) {
    new = NEW(RPOINT);
    new->point = s;
    new->from = l->start;
    new->to = l->end;
    new->next = root;
    root = new;
    l = l->next;
  }
}

SEQUENCE *getRootOf(double P)
{
  RPOINT *p;

  p = root;
  while (p!=NULL) {
    if (p->from <= P && P <= p->to)
      return p->point;
    p = p->next;
  }
  return NULL;

}


static int mycompare(const void *left,const void *right)
{
  RPOINT *pl,*pr;

  pl = *(RPOINT **)left;
  pr = *(RPOINT **)right;

  if (pl->from == pr->from) return 0;
  if (pl->from < pr->from) return -1;
  return 1;

}

void sortRootPoints(void)
{
  RPOINT **array;
  RPOINT *p;
  int size,i;

  size = 0;
  p = root;
  while (p!=NULL) { 
    size++; 
    p=p->next; 
  }

  array = Malloc(sizeof(RPOINT *)*size);

  p = root;
  size=0;
  while (p!=NULL) {
    array[size] = p;
    size++;
    p = p->next;
  }

  qsort(&array[0],size,sizeof(RPOINT *),mycompare);

  for (i=0;i<size-1;i++)
    array[i]->next = array[i+1];
  array[size-1]->next = 0;
  root = array[0];
}

void prettyRootPoints(void)
{
  RPOINT *p;

  p = root;
  while (p!=NULL) {
    printf("(%f ,  %f) address (0x%X)\n",p->from,p->to,(int)p->point);
    p = p->next;
  }
  
}
