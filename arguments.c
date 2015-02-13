#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "arguments.h"

int num_ini_seq;      /* Number of initial sequences            */

double selection_rate;
double M;

void parseArguments(int argc, char **argv)
{
  char *c;
  num_ini_seq = 5;
  int seed = time(NULL);

  selection_rate = 0.0;
  M = 10.0;

  for (argc--;argc>0;argc--) {
    c = argv[argc];
    while (1) {
      if (strncmp("seed=",c,5)==0) {
	seed = atoi(c+5);
	break;
      }
      if (strncmp("m=",c,2)==0) {
	M = strtod(c+2,NULL);
	break;
      }
      if (strncmp("n=",c,2)==0) {
	num_ini_seq = atoi(c+2);
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

  srand(seed);
  srand48(seed);
}
