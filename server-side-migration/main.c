#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "structures.h"
#include "tree.h"
#include "sequence.h"
#include "memory.h"
#include "terminator.h"
#include "arguments.h"

int main(int argc, char **argv) 
{
  parseArguments(argc,argv);

  printf("Migration\n");

  printf("L 0 Initial pop. 1\n");
  printf("L 3 Initial pop. 2\n");
  printf("L 1 Coalescent\n");
  printf("L 5 Migration\n");

  printf("IR %f,%f#\n", 0.0, 0.5);
  build();

  printf("\n");
  makeRealTree();

  return 0;
}
