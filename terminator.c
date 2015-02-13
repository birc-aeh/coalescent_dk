#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "terminator.h"
#include "memory.h"
#include "tree.h"
#include "sets.h"

extern int num_ini_seq;
extern int R;

typedef struct termList {
  double z;
  int k;
  struct termList *next,*prev;
  REALTREE *realtree;
} termList;

typedef struct jumpList {
  termList *from,*to;
  struct jumpList *prev,*next;
} jumpList;

static termList *root;
static termList **htable;
static int hsize;
static int *number_with_size;
static int roof;
static bool some_k_became_one;

static jumpList *jlist;

static double matleft;

int max(int a, int b)
{
  return a>b ? a:b;
}

void initTerminator(void)
{
  int i;
  root = malloc(sizeof(termList));
  root->z = 0.0;
  root->k = num_ini_seq;
  root->next = NULL;
  root->prev = NULL;

  number_with_size = malloc(sizeof(int)*(num_ini_seq+1));
  for (i=0; i<num_ini_seq; number_with_size[i++]=0);
  number_with_size[num_ini_seq] = 1;
  roof = 1;
  some_k_became_one = false;

  hsize = (R/2)+1;
  htable = malloc(hsize*sizeof(termList *));
  for (i=0; i<hsize; i++) 
    htable[i] = NULL;
  htable[0] = root;

  jlist = NULL;
}

static jumpList *removeJumpElement(jumpList *jl)
{
  if (jl->prev==NULL) {
    if (jl->next==NULL) {
      jlist=NULL;
      return NULL;
    }
    jlist = jl->next;
    jlist->prev = NULL;
    return jlist;
  }
  if (jl->next==NULL) {
    jl->prev->next=NULL;
    return jl->prev;
  }
  jl->prev->next = jl->next;
  jl->next->prev = jl->prev;
  return jl->prev;
}

static jumpList *appendJumpElement(jumpList *jl)
{
  jumpList *tmp;

  tmp = malloc(sizeof(jumpList));
  tmp->from = tmp->to = NULL;

  if (jl==NULL) {
    tmp->prev = tmp->next = NULL;
    jlist = tmp;
    return tmp;
  }

  if (jl->next==NULL) {
    jl->next = tmp;
    tmp->prev = jl;
    tmp->next = NULL;
    return tmp;
  }

  tmp->next = jl->next;
  tmp->next->prev = tmp;
  tmp->prev = jl;
  jl->next = tmp;
  return tmp;
}

static jumpList *prependJumpElement(jumpList *jl)
{
  jumpList *tmp;
  if (jl==NULL)
    return appendJumpElement(NULL);

  if (jl->prev!=NULL)
    return appendJumpElement(jl->prev);

  tmp = appendJumpElement(NULL);
  tmp->next = jl;
  tmp->next->prev = tmp;
  tmp->prev = NULL;
  return tmp;
}


INTERVAL *makeIntervals(void)
{
  int j,size;
  INTERVAL *i;
  INTERVALLIST *il;
  termList *t;
  double sum;

  jumpList *jl;

  jl=jlist;
  while (jl!=NULL) {
    while (jl->from->prev!=NULL && jl->from->prev->k==1)
      jl->from = jl->from->prev;
    while (jl->to->next!=NULL && jl->to->next->k==1) {
      if (jl->next!=NULL && jl->next->from==jl->to) {
	jl->next->from = jl->from;
	jl=removeJumpElement(jl);
      }
      else
	jl->to=jl->to->next;
    }
    if (jl->next!=NULL && jl->to==jl->next->from)
      removeJumpElement(jl->next);
    jl=jl->next;
  }


  t=root;
  jl=jlist;
  while (t!=NULL) {
    if (t->k==1) {
      if (jl==NULL) {
	jl=appendJumpElement(jl);
	jl->from = t;
	while (t->next!=NULL && t->next->k==1)
	  t=t->next;
	jl->to = t;
      }
      else {
	if (jl->from==t)
	  t=jl->to;
	else {
	  if (t->z<jl->from->z) 
	    jl=prependJumpElement(jl);
	  else
	    jl=appendJumpElement(jl);
	  jl->from=t;
	  while (t->next!=NULL && t->next->k==1)
	    t=t->next;
	  jl->to=t;
	}
      }
      if (jl->next!=NULL)
	jl=jl->next;
    }
    t=t->next;
  }


  jl=jlist;
  size = (root==jl->from)?0:1;
  while (jl!=NULL) {
    size++;
    if (jl->next==NULL && jl->to->next==NULL)
      size--;
    jl=jl->next;
  }

  if (size==0) return NULL;

  il = malloc(sizeof(INTERVALLIST)*size);
  jl=jlist;
  j=0;
  if (root!=jl->from) {
    il[0].start = root->z;
    il[0].end = jl->from->z;
    j++;
  }
  while (jl!=NULL) {
    if (jl->to->next!=NULL) {
      il[j].start=jl->to->next->z;
      if (jl->next!=NULL)
	il[j].end=jl->next->from->z;
      else
	il[j].end=(double)R/2.0;
    }
    j++;
    jl=jl->next;
  }
  for (j=0; j<(size-1); j++) 
    il[j].next = &il[j+1];
  il[size-1].next = &il[0];

  for (j=1; j<size; j++)
    il[j].prev = &il[j-1];
  il[0].prev = &il[size-1];

  i = malloc(sizeof(INTERVAL));
  i->size = size;
  i->list = il;

  sum = 0.0;
  il = i->list;
  for (j=0; j<i->size; j++) {
    sum += il->end-il->start;
    il = il->next;
  }

  matleft = 200.0*sum/(float)R;

  return i;
}

debug double newTime;
  
INTERVAL *last_make;

double updateOneK(void)
{
  if (some_k_became_one) {
    last_make = makeIntervals();
    intersectAll(last_make);
    some_k_became_one = false;
    return matleft;
  }
  some_k_became_one = false;
  return -1.0;
}

extern SEQUENCE *rootTime;

#define bl 9


int printtree_r(char **display,int mdepth,REALTREE *t,int depth,
		int *line) {
  int i,j,from,to;
  char num[5];
  char rel[20];
  if (t->left==NULL && t->right==NULL) {
    /*tip*/
    for(j=depth*bl;j<bl*mdepth+bl-1;j++)
      display[*line][j] = '-';
    sprintf(num, "%d", t->number);
    strcat(display[*line], num);
    return (*line)++;
  }
  else {
    from = printtree_r(display,mdepth,t->left, depth+1,line);
    (*line)++;
    to = printtree_r(display,mdepth,t->right,depth+1,line);
    for(i=from;i<=to;i++)
      display[i][depth*bl+bl-1] = '|';
    for(j=depth*bl;j<depth*bl+bl-1;j++)
      display[(from+to)/2][j] = '-';
    sprintf(rel,"%f",t->time);
    for(i=0;rel[i]!='\0'&&i<10;i++)
      display[(from+to)/2][j+1+i]=rel[i];
    return (from+to)/2;
  }
}


int depthtree(REALTREE *t) {
  int ld,rd;
  if (t==NULL)
    return 0;
  else {
    ld = depthtree(t->left);
    rd = depthtree(t->right);
    if (ld>rd)
      return ld+1;
    else 
      return rd+1;
  }
}

void printAlltree(REALTREE *t) {
  if (t!=NULL) {
    printf("(");
    printAlltree(t->left);
    printf(",");
    printAlltree(t->right);
    printf(")");
    printf(":[%f:%i]",t->time,t->number);
  }
  else
    printf("NULL");
}

void dumpTreeStructure(REALTREE *t)
{
  if (!t) return;

  if (t->time == 0.0) 
    printf("%d ",t->number);
  else
    printf("%f ",t->time);

  dumpTreeStructure(t->left);
  dumpTreeStructure(t->right);
}

void dumpTree(termList *t)
{
  printf("%f|",t->z);
  dumpTreeStructure(t->realtree);
  printf("\n");
}

