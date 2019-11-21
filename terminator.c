#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "terminator.h"
#include "memory.h"
#include "tree.h"
#include "sets.h"

extern int num_ini_seq;
static const int R = 1;

typedef struct termList {
  double z;
  int k;
  struct termList *next,*prev;
  REALTREE *realtree;
} termList;

static termList *root;
static int *number_with_size;
static int roof;
static bool some_k_became_one;

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
}

static termList *last_k_one_in_this_run(termList *t)
{
  while (t != NULL && t->next != NULL && t->next->k == 1)
    t = t->next;
  return t;
}

static termList *find_first_k_one_after(termList *t)
{
  t = t->next;
  while (t != NULL && t->k != 1)
    t = t->next;
  return t;
}

INTERVAL *makeIntervals(void)
{
  INTERVAL *i;
  INTERVALLIST *il;

  termList *t = root;
  int size = 0;
  if (root != NULL && root->k > 1) {
    t = find_first_k_one_after(t);
    size++;
  }

  while (t != NULL) {
    termList *to = last_k_one_in_this_run(t);
    termList *next = find_first_k_one_after(to);
    size++;
    if (next == NULL && to->next == NULL)
      size--;
    t = next;
  }

  if (size == 0)
    return NULL;

  il = calloc(sizeof(INTERVALLIST), size);
  int j = 0;
  t = root;
  if (root != NULL && root->k > 1) {
    t = find_first_k_one_after(t);
    il[0].start = root->z;
    il[0].end = t->z;
    j++;
  }
  while (t != NULL) {
    termList *to = last_k_one_in_this_run(t);
    termList *next = find_first_k_one_after(to);
    if (to->next != NULL) {
      il[j].start = to->next->z;
      if (next != NULL)
        il[j].end = next->z;
      else
        il[j].end = (double)R / 2.0;
    }
    j++;
    t = next;
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

  double sum = 0.0;
  il = i->list;
  for (j=0; j<i->size; j++) {
    sum += il->end - il->start;
    il = il->next;
  }

  matleft = 200.0*sum/(float)R;

  return i;
}

void updateCoalescens(double from, double to)
{
  termList *t = root;
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
        if (p > 0.0) // TODO: always true?
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
