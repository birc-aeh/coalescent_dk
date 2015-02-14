#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "structures.h"
#include "tree.h"
#include "sequence.h"
#include "sets.h"
#include "memory.h"
#include "terminator.h"
#include "arguments.h"

extern int num_ini_seq;
extern int R;
extern int runs;
extern int seed;

int main(int argc, char **argv) 
{
/*    int i,max,index; */
/*    FILE *f; */


  parseArguments(argc,argv);
  initMemory();

  printf("L 0 Initial\n");
  printf("L 1 Coalescent\n");
  printf("L 2 Recombination\n");

  printf("IR %f,%f#\n", 0.0, (double)R/2.0);
  build();

  printf("\n");
  makeRealTree();

  return 0;
}
