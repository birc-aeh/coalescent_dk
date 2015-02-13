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

static termList *root;
static termList **htable;
static int hsize;
static int *number_with_size;
static int roof;

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
}

