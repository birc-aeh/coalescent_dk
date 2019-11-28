#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "arguments.h"

int num_ini_seq;      /* Number of initial sequences            */
int R;                /* Recombination rate                     */
int RZ;               /* Flag if R is zero                      */
int seed;
double exprate;

void parseArguments(int argc, char **argv)
{
  char *c;

  R = 10;
  RZ = 0;
  num_ini_seq = 5;
  seed = time(NULL);
  exprate = 0.0;

  for (argc--;argc>0;argc--) {
    c = argv[argc];
    while (1) {
      if (strncmp("seed=",c,5)==0) {
	seed = atoi(c+5);
	break;
      }
      if (strncmp("r=",c,2)==0) {
	R = atoi(c+2);
	if (R==0) { R=1; RZ=1; }
	break;
      }
      if (strncmp("n=",c,2)==0) {
	num_ini_seq = atoi(c+2);
	break;
      }
      if (strncmp("exp=",c,4)==0) {
	exprate = strtod(c+4,NULL);
	break;
      }
      printf("Bad parameter %s\n",c);
      exit(0);
      break;
    }
  }

  srand(seed);
  srand48(seed);
}
