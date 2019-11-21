#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "arguments.h"

int num_ini_seq;      /* Number of initial sequences            */
int runs;
int seed;
double M1,M2;

void parseArguments(int argc, char **argv)
{
  char *c;

  num_ini_seq = 5;
  runs = 100;
  seed = time(NULL);

  M1 = 0.5;
  M2 = 0.5;

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
      if (strncmp("m1=",c,3)==0) {
	M1 = strtod(c+3,NULL);
	break;
      }
      if (strncmp("m2=",c,3)==0) {
	M2 = strtod(c+3,NULL);
	break;
      }
      if (strncmp("n=",c,2)==0) {
	num_ini_seq = atoi(c+2);
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
