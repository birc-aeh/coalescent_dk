#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "arguments.h"

int num_ini_seq;      /* Number of initial sequences            */
int R;                /* Recombination rate                     */
int RZ;               /* Flag if R is zero                      */
int alloc_size;       /* Size of a sequence pool (see graphics) */
int mblock_size;      /* Size of a memory block (see graphics)  */
int runs;
int seed;
double exprate;

double selection_rate;
double M;


static int max(int a,int b)
{
  return a>b?a:b;
}

void parseArguments(int argc, char **argv)
{
  char *c;

  R = 1;
  RZ = 1;
  num_ini_seq = 5;
  alloc_size = 10;
  mblock_size = 30000;
  runs = 100;
  seed = time(NULL);
  exprate = 0.0;

  selection_rate = 0.0;
  M = 10.0;

  for (argc--;argc>0;argc--) {
    c = argv[argc];
    while (1) {
      if (strncmp("runs=",c,5)==0) {
	runs = atoi(c+5);
	break;
      }
      if (strncmp("seed=",c,5)==0) {
	seed = atoi(c+5);
	break;
      }
      if (strncmp("sblock=",c,7)==0) {
	alloc_size = atoi(c+7);
	break;
      }
      if (strncmp("mblock=",c,7)==0) {
	mblock_size = atoi(c+7);
	break;
      }
      if (strncmp("m=",c,2)==0) {
	M = strtod(c+2,NULL);
	break;
      }

/*        if (strncmp("r=",c,2)==0) { */
/*  	R = atoi(c+2); */
/*  	if (R==0) { R=1; RZ=1; } */
/*  	break; */
/*        } */
      if (strncmp("n=",c,2)==0) {
	num_ini_seq = atoi(c+2);
	break;
      }
      if (strncmp("exp=",c,4)==0) {
	exprate = strtod(c+4,NULL);
	break;
      }
      if (strncmp("s=",c,2)==0) {
	selection_rate = strtod(c+2,NULL);
	break;
      }
      
      printf("Bad parameter %s\n",c);
      exit(0);
      break;
    }
  }

  mblock_size = max(mblock_size,(R/2+1)*sizeof(void *));
  mblock_size = max(mblock_size,sizeof(int)*(num_ini_seq+1));
  mblock_size = mblock_size + (1000-mblock_size%1000);

  srand(seed);
  srand48(seed);
}
