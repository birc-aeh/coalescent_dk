#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include "structures.h"
#include "terminator.h"

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

static REALTREE *makeOneTree(void)
{
  SEQUENCE *tl = rootTime;

  for (;;) {
    bool son_is_leaf      = (tl->indegree >= 1) && (tl->son->indegree == 0);
    bool daughter_is_leaf = (tl->indegree >= 2) && (tl->daughter->indegree == 0);
    if (tl->indegree == 1) {
      /* Migration */
      if (son_is_leaf)
        tl->sub = leaf_node(tl->son->ID);
      else
        tl->sub = tl->son->sub;
      tl->father->sub = tl->sub;
    }
    else if (tl->indegree == 2) {
      /* Coalescent */
      REALTREE *left  = son_is_leaf?      leaf_node(tl->son->ID)        : tl->son->sub;
      REALTREE *right = daughter_is_leaf? leaf_node(tl->daughter->ID)   : tl->daughter->sub;
      tl->sub = coalesence_node(tl->Time, left, right);
    }

    if (tl->nextTime == NULL) break;
    tl = tl->nextTime;
  }
  return tl->sub;
}

static void dumpTreeStructure(REALTREE *t)
{
  if (!t) return;

  if (t->time == 0.0)
    printf("%d ",t->number);
  else
    printf("%f ",t->time);

  dumpTreeStructure(t->left);
  dumpTreeStructure(t->right);
}

void makeRealTree(void)
{
  printf("%f|",0.0);
  dumpTreeStructure(makeOneTree());
  printf("\n");
}
