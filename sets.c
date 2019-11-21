#include <stdio.h>
#include <stdlib.h>
#include <string.h> /* memcpy */
#include <assert.h>
#include "memory.h"
#include "sets.h"
#include "memory.h"
#include "terminator.h"

static const double R = 1.0;

static double fmin(double a,double b)
{
  return (a<b?a:b);
}

/* Make an interval with one entry (<x1>,<y1>). */
INTERVAL *initInterval(double x1, double y1)
{
  INTERVAL *result = malloc(sizeof(INTERVAL));
  result->size = 1;
  result->ranges = malloc(2*sizeof(double));
  result->ranges[0] = x1;
  result->ranges[1] = y1;
  return result;
}

/* Return an exact copy of interval <i>. */
INTERVAL *copyIntervals(INTERVAL *i)
{
  size_t byte_size = 2*i->size*sizeof(double);
  INTERVAL *new = malloc(sizeof(INTERVAL));
  new->size = i->size;
  new->ranges = malloc(byte_size);
  memcpy(new->ranges, i->ranges, byte_size);
  return new;
}

static INTERVAL *merge_intervals(INTERVAL *a, INTERVAL *b, int limit)
{
  int new_size = (a->size + b->size)*2;
  int count = 0;
  INTERVAL *res = malloc(sizeof(INTERVAL));
  res->ranges = malloc(new_size*sizeof(double));
  int i = 0, j = 0, k = 0;
  double r = 0.0;
  while (i < 2*a->size || j < 2*b->size) {
    double A = i >= 2*a->size? R : a->ranges[i];
    double B = j >= 2*b->size? R : b->ranges[j];
    int new_count = count;
    r = fmin(A,B);
    if (A <= B) {
      new_count += (i % 2 == 0? 1 : -1);
      i += 1;
    }
    if (A >= B) {
      new_count += (j % 2 == 0? 1 : -1);
      j += 1;
    }
    if ((count <= limit && new_count > limit)
        || (count > limit && new_count <= limit))
      res->ranges[k++] = r;
    count = new_count;
  }
  assert(k % 2 == 0);
  res->size = k/2;
  return res;
}

/* Unite intervals <i1> with <i2> */
INTERVAL *unite(INTERVAL *i1, INTERVAL *i2, coal_callback cb)
{
  INTERVAL *res = merge_intervals(i1, i2, 0);
  INTERVAL *intersection = merge_intervals(i1, i2, 1);
  int i = 0;
  for (; i < 2*intersection->size; i += 2)
    cb(intersection->ranges[i], intersection->ranges[i+1]);
  free(intersection->ranges);
  free(intersection);
  return res;
}

/* Unite without updating terminator */
INTERVAL *uniteNoTerm(INTERVAL *i1, INTERVAL *i2)
{
    return merge_intervals(i1, i2, 0);
}

void prettyInterval(INTERVAL *i)
{
  if (i==NULL) {
    printf("<null>\n");
    return;
  }
  int j;
  for (j=0; j < 2*i->size; j += 2)
    printf("%f,%f#",i->ranges[j],i->ranges[j+1]);
}
