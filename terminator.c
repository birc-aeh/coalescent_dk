#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "terminator.h"
#include "memory.h"
#include "tree.h"
#include "sets.h"

extern int num_ini_seq;
static const int R = 1;

static double root_z = 0.0;
static int root_k;

static int *number_with_size;
static bool some_k_became_one;

static double matleft;

void initTerminator(void)
{
  root_k = num_ini_seq;

  number_with_size = calloc(sizeof(int), (num_ini_seq+1));
  number_with_size[num_ini_seq] = 1;
  some_k_became_one = false;
}

void updateCoalescens(double from, double to)
{
  if ((root_z >= from) && (root_z < to)) {
    number_with_size[root_k]--;
    root_k--;
    number_with_size[root_k]++;
    if (root_k==1) {
      some_k_became_one = true;
    }
  }
}

INTERVAL *last_make;

double updateOneK(void)
{
  if (some_k_became_one) {
    last_make = NULL;
    intersectAll(last_make);
    some_k_became_one = false;
    return matleft;
  }
  some_k_became_one = false;
  return -1.0;
}

bool theEnd(void)
{
  return (number_with_size[1] == 1);
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

void dumpTree(double z, REALTREE *realtree)
{
  printf("%f|",z);
  dumpTreeStructure(realtree);
  printf("\n");
}

void makeRealTree()
{
  double p = (root_z + (R/2.0))/2.0;
  dumpTree(root_z, makeOneTree(p));
}
