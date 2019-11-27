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

  double *il = malloc(sizeof(double)*2*size);
  int j = 0;
  t = root;
  if (root != NULL && root->k > 1) {
    t = find_first_k_one_after(t);
    il[0] = root->z;
    il[1] = t->z;
    j++;
  }
  while (t != NULL) {
    termList *to = last_k_one_in_this_run(t);
    /* assert (to == jl->to); */
    termList *next = find_first_k_one_after(to);
    /* assert (jl->next == NULL || next == jl->next->from); */
    if (to->next != NULL) {
      il[2*j] = to->next->z;
      if (next != NULL)
	il[2*j+1] = next->z;
      else
	il[2*j+1] = R/2.0;
    }
    j++;
    t = next;
  }
  INTERVAL *i = malloc(sizeof(INTERVAL));
  i->size = size;
  i->ranges = il;
  return i;
}

bool updateRecombination(double P)
{
  if (P >= R/2.0)
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

static REALTREE* leaf_node(int id)
{
  REALTREE *res = calloc(1, sizeof(REALTREE));
  res->number = id;
  return res;
}

static REALTREE* coalesence_node(double time, REALTREE *left, REALTREE *right)
{
  REALTREE *res = calloc(1, sizeof(REALTREE));
  res->time = time;
  res->left = left;
  res->right = right;
  return res;
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
    switch (tl->indegree) {
    case 1:
      /* Recombination */
      tl->sub = (tl->son->indegree == 0)? leaf_node(tl->son->ID) : tl->son->sub;
      SEQUENCE *parent = (p > tl->P)? tl->mother : tl->father;
      parent->sub = tl->sub;
      break;
    case 2:
      /* Coalescens  */
      if (tl->son->indegree == 0) {
        /* Both children are leaves */
        if (tl->daughter->indegree == 0) {
          tl->sub = coalesence_node(tl->Time, leaf_node(tl->son->ID), leaf_node(tl->daughter->ID));
          n--;
          break;
        }
        /* Son is leaf, daughter is bad  */
        if (tl->daughter->sub == NULL) {
          tl->sub = leaf_node(tl->son->ID);
          break;
        }
        /* Son is leaf, daughter is good */
        else {
          tl->sub = coalesence_node(tl->Time, leaf_node(tl->son->ID), tl->daughter->sub);
          n--;
          break;
        }
      }
      if (tl->daughter->indegree == 0) {
        /* Daughter is leaf, son is bad */
        if (tl->son->sub == NULL) {
          tl->sub = leaf_node(tl->daughter->ID);
          break;
        }
        /* Daughter is leaf, son is good  */
        else {
          tl->sub = coalesence_node(tl->Time, tl->son->sub, leaf_node(tl->daughter->ID));
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
      tl->sub = coalesence_node(tl->Time, tl->son->sub, tl->daughter->sub);
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
