#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "structures.h"
#include "terminator.h"
#include "memory.h"
#include "tree.h"

extern int num_ini_seq;

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
      assert(tl->outdegree == 1);
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

    if (n<=1) break;
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

void dumpTree(REALTREE *realtree)
{
  printf("%f|",0.0);
  dumpTreeStructure(realtree);
  printf("\n");
}

void makeRealTree(void)
{
  const double R = 1.0;
  double p = (R/2.0)/2.0;
  dumpTree(makeOneTree(p));
}
