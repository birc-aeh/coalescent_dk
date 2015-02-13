#include <stdlib.h>
#include <stdio.h>
#include <string.h>
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

typedef struct jumpList {
  termList *from,*to;
  struct jumpList *prev,*next;
} jumpList;

static termList *root;
static termList **htable;
static int hsize;
static int *number_with_size;
static int roof;

static jumpList *jlist;

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

  hsize = (R/2)+1;
  htable = malloc(hsize*sizeof(termList *));
  for (i=0; i<hsize; i++) 
    htable[i] = NULL;
  htable[0] = root;

  jlist = NULL;
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

INTERVAL *last_make;

double updateOneK(void)
{
  return -1.0;
}

