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

void initTerminator(void)
{
  int i;
  root = NEW(termList);
  root->z = 0.0;
  root->k = num_ini_seq;
  root->next = NULL;
  root->prev = NULL;

  number_with_size = calloc(sizeof(int), (num_ini_seq+1));
  for (i=0; i<num_ini_seq; number_with_size[i++]=0);
  number_with_size[num_ini_seq] = 1;
  roof = 1;
  some_k_became_one = false;

  hsize = (R/2)+1;
  htable = calloc(hsize, sizeof(termList *));
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

  tmp = NEW(jumpList);
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

  il = calloc(sizeof(INTERVALLIST), size);
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

  i = NEW(INTERVAL);
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
  
void updateCoalescens(double from, double to)
{
  termList *t;
  int i;

  if ((int)from>=hsize) {
    fprintf(stderr,"Terminator problemems!!\n");
    exit(1);
  }

  t = htable[(int)from];
  if (t==NULL) {
    for (i=((int)from)-1; htable[i]==NULL; i--);
    t = htable[i];
  }
    
  while (t!=NULL) {
    if ((t->z >= from) && (t->z < to)) {
      number_with_size[t->k]--;
      t->k--;
      number_with_size[t->k]++;
      if (t->k==1) {
	some_k_became_one = true;
      }
    }
    if (t->z >= to) {
      return;
    }
    t = t->next;
  }

}

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

bool theEnd(void)
{
  return (number_with_size[1]==roof);
}


extern SEQUENCE *rootTime;


REALTREE *makeSub(void)
{
  REALTREE *result;

  result = NEW(REALTREE);
  result->left = 0;
  result->right = 0;
  result->time = 0.0;
  result->number = 0;

  return result;
}





static REALTREE *makeOneTree(double p)
{
  int n;
  SEQUENCE *tmp;
  SEQUENCE *tl;

  /* Clear the tree  */
  tmp = rootTime;
  while (tmp) {
    switch (tmp->indegree) {
    case 1:
      tmp->sub = NULL;
      tmp->father->sub = NULL;
      if (tmp->outdegree > 1)
        tmp->mother->sub = NULL;
      break;
    case 2:
      tmp->sub = NULL;
    }
    tmp = tmp->nextTime;
  }
  n = num_ini_seq;
  tl = rootTime;

  while (n>1) {
    tmp = NULL;
    switch (tl->indegree) {
    case 1:
      /* Migration */
      if (tl->son->indegree == 0) {
        tl->sub = makeSub();
        tl->sub->number = tl->son->ID;
      }
      else {
        tl->sub = tl->son->sub;
      }
      if (tl->outdegree == 1) {
	tl->father->sub = tl->sub;
      } else {
	if (p > tl->P) 
	  tl->mother->sub = tl->sub;
	else
	  tl->father->sub = tl->sub;
      }
      break;
    case 2:
      /* Coalescens  */
      if (tl->son->indegree == 0) {
        /* Both children are leaves */
        if (tl->daughter->indegree == 0) {
          tl->sub = makeSub();
          tl->sub->time = tl->Time;
          tl->sub->left = makeSub();
          tl->sub->left->number = tl->son->ID;
          tl->sub->right = makeSub();
          tl->sub->right->number = tl->daughter->ID;
          n--;
          break;
        }
        /* Son is leaf, daughter is bad  */
        if (tl->daughter->sub == NULL) {
          tl->sub = makeSub();
          tl->sub->number = tl->son->ID;
          break;
        }
        /* Son is leaf, daughter is good */
        else {
          tl->sub = makeSub();
          tl->sub->time = tl->Time;
          tl->sub->left = makeSub();
          tl->sub->left->number = tl->son->ID;
          tl->sub->right = tl->daughter->sub;
          n--;
          break;
        }
      }
      if (tl->daughter->indegree == 0) {
        /* Daughter is leaf, son is bad */
        if (tl->son->sub == NULL) {
          tl->sub = makeSub();
          tl->sub->number = tl->daughter->ID;
          break;
        }
        /* Daughter is leaf, son is good  */
        else {
          tl->sub = makeSub();
          tl->sub->time = tl->Time;
          tl->sub->right = makeSub();
          tl->sub->right->number = tl->daughter->ID;
          tl->sub->left = tl->son->sub;
          n--;
          break;
        }
      }
      if (tl->son->sub == NULL) {
        /* Both children are bad  */
        if (tl->daughter->sub == NULL) {
          break;
        }
        /* Son is bad, daughter is good */
        else {
          tl->sub = tl->daughter->sub;
          break;
        }
      }
      else {
        /* Daughter is bad, son is good  */
        if (tl->daughter->sub == NULL) {
          tl->sub = tl->son->sub;
          break;
        }
      }
      /* Both children are good  */
      tl->sub = makeSub();
      tl->sub->time = tl->Time;
      tl->sub->left = tl->son->sub;
      tl->sub->right = tl->daughter->sub;
      n--;
      break;
    default:
      fprintf(stderr," *** Very Bad!!\n");
      exit(1);
    }

    if (n<=1) break;
    tl = tl->nextTime;
  }
  
  return tl->sub;
}




#define bl 9


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

void makeRealTree()
{
  termList *t;
  double p;

  t = root;

  //printf("Here\n");
  while (t->next!=NULL) {
    p = (t->z+t->next->z)/2.0;

    t->realtree = makeOneTree(p);
    dumpTree(t);

    t = t->next;
  }   
  
  p = (t->z+(R/2.0))/2.0;
  t->realtree = makeOneTree(p);
  dumpTree(t);
}
