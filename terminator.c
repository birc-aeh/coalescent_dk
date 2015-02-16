#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "terminator.h"
#include "tree.h"
#include "sets.h"

extern int num_ini_seq;
extern int R;

typedef struct termList {
  double z;
  int k;
  struct termList *next,*prev;
} termList;

static termList *root;
static int *terms_with_size;
static int nterms;
static bool some_k_became_one;

int max(int a, int b)
{
  return a>b ? a:b;
}

void prependElement(termList *t,double z)
{
  termList *n = malloc(sizeof(termList));
  n->prev = t->prev;
  n->prev->next = n;
  n->next = t;
  t->prev = n;
  n->z = z;
  n->k = n->prev->k;
  terms_with_size[n->k]++;
  nterms++;
}

void appendElement(termList *t,double z)
{
  termList *n = malloc(sizeof(termList));
  n->next = NULL;
  n->prev = t;
  t->next = n;
  n->z = z;
  n->k = t->k;
  terms_with_size[n->k]++;
  nterms++;
}

void initTerminator(void)
{
  int i;
  root = malloc(sizeof(termList));
  root->z = 0.0;
  root->k = num_ini_seq;
  root->next = NULL;
  root->prev = NULL;

  terms_with_size = malloc(sizeof(int)*(num_ini_seq+1));
  for (i=0; i<num_ini_seq; i++)
      terms_with_size[i]=0;
  terms_with_size[num_ini_seq] = 1;
  nterms = 1;
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
  int j;
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

  il = malloc(sizeof(INTERVALLIST)*size);
  j = 0;
  t = root;
  if (root != NULL && root->k > 1) {
    t = find_first_k_one_after(t);
    il[0].start = root->z;
    il[0].end = t->z;
    j++;
  }
  while (t != NULL) {
    termList *to = last_k_one_in_this_run(t);
    /* assert (to == jl->to); */
    termList *next = find_first_k_one_after(to);
    /* assert (jl->next == NULL || next == jl->next->from); */
    if (to->next != NULL) {
      il[j].start = to->next->z;
      if (next != NULL)
	il[j].end = next->z;
      else
	il[j].end = (double)R/2.0;
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

  i = malloc(sizeof(INTERVAL));
  i->size = size;
  i->list = il;
  return i;
}

bool updateRecombination(double P)
{
  if (P>=(double)R/2.0)
    return false;

  termList *t = root;
  
  if (t->z > P) {
    prependElement(t,P);
    return true;
  }

  while (t!=NULL) {
    if (t->z > P) {
      prependElement(t,P);
      return true;
    }
    if (t->z == P) {
      return false;
    }
    if (t->next==NULL) {
      appendElement(t,P);
      return true;
    }
    t=t->next;
  }
  return true;
}

void updateCoalescens(double from, double to)
{
  termList *t = root;
  while (t != NULL) {
    if ((t->z >= from) && (t->z < to)) {
      terms_with_size[t->k]--;
      t->k--;
      terms_with_size[t->k]++;
      if (t->k == 1) {
	some_k_became_one = true;
      }
    }
    if (t->z >= to) {
      return;
    }
    t = t->next;
  }

}

double updateOneK(INTERVAL **last_make)
{
  if (some_k_became_one) {
    *last_make = makeIntervals();
    intersectAll(*last_make);
    some_k_became_one = false;
    return 1.0;
  }
  *last_make = NULL;
  return -1.0;
}

bool theEnd(void)
{
  /* Stop when all terms have size 1 */
  return (terms_with_size[1]==nterms);
}

extern SEQUENCE *rootTime;


REALTREE *makeSub(void)
{
  REALTREE *result;

  result = malloc(sizeof(REALTREE));
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

  while (n > 1) {
    tmp = NULL;
    switch (tl->indegree) {
    case 1:
      /* Recombination */
      if (tl->son->indegree == 0) {
        tl->sub = makeSub();
        tl->sub->number = tl->son->ID;
      }
      else {
        tl->sub = tl->son->sub;
      }
      if (p > tl->P) 
        tl->mother->sub = tl->sub;
      else
        tl->father->sub = tl->sub;
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

    if (n <= 1)
      break;
    tl = tl->nextTime;
  }
  
  return tl->sub;
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

void dumpTree(double z, REALTREE *realtree)
{
  printf("%f|",z);
  dumpTreeStructure(realtree);
  printf("\n");
}

void makeRealTree()
{
  termList *t = root;
  while (t->next != NULL) {
    double p = (t->z + t->next->z)/2.0;
    dumpTree(t->z, makeOneTree(p));
    t = t->next;
  }
  double p = (t->z + (R/2.0))/2.0;
  dumpTree(t->z, makeOneTree(p));
}
