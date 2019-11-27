#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "structures.h"
#include "tree.h"
#include "sequence.h"
#include "arguments.h"
#include "selection.h"

int main(int argc, char **argv) 
{
  parseArguments(argc,argv);

  printf("Selection\n");
  
  printf("L 0 Initial\n");
  printf("L 1 Coalescent?\n");
  printf("L 2 Phony?\n");

  printf("IR %f,%f#\n", 0.0, (double)1/2.0);
  build();

  makeSelection();
  return 0;
}
