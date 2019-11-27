#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "sets.h"

int R = 10;


int test_inverse() {
  double A[] = {0.5, 1.0};
  INTERVAL i = {1, A};
  INTERVAL *res = inverse(&i);
  double expected[] = {0.0, 0.5, 1.0, R/2.0};
  return res->size == 2 && memcmp(res->ranges, expected, sizeof(expected)) == 0;
}
int test_inverse_zero() {
  double A[] = {0.0, 1.0};
  INTERVAL i = {1, A};
  INTERVAL *res = inverse(&i);
  double expected[] = {1.0, R/2.0};
  return res->size == 1 && memcmp(res->ranges, expected, sizeof(expected)) == 0;
}
int test_inverse_zero2() {
  double A[] = {0.0, 1.0, 2.0, 3.0};
  INTERVAL i = {2, A};
  INTERVAL *res = inverse(&i);
  double expected[] = {1.0, 2.0, 3.0, R/2.0};
  return res->size == 2 && memcmp(res->ranges, expected, sizeof(expected)) == 0;
}
int test_inverse_end() {
  double A[] = {0.5, R/2.0};
  INTERVAL i = {1, A};
  INTERVAL *res = inverse(&i);
  double expected[] = {0.0, 0.5};
  return res->size == 1 && memcmp(res->ranges, expected, sizeof(expected)) == 0;
}
int test_inverse_end2() {
  double A[] = {0.5, 1.0, 1.5, R/2.0};
  INTERVAL i = {2, A};
  INTERVAL *res = inverse(&i);
  double expected[] = {0.0, 0.5, 1.0, 1.5};
  return res->size == 2 && memcmp(res->ranges, expected, sizeof(expected)) == 0;
}
int test_inverse_complete() {
  double A[] = {0.0, R/2.0};
  INTERVAL i = {1, A};
  INTERVAL *res = inverse(&i);
  return res->size == 0;
}
int test_inverse_empty() {
  double expected[] = {0.0, R/2.0};
  INTERVAL i = {0, NULL};
  INTERVAL *res = inverse(&i);
  return res->size == 1 && memcmp(res->ranges, expected, sizeof(expected)) == 0;
}

int test_and_simple() {
  double A[] = {0.5,      1.5     };
  double B[] = {     1.0,      2.0};
  double R[] = {     1.0, 1.5     };
  INTERVAL a = {1, A};
  INTERVAL b = {1, B};
  INTERVAL *res = copyIntervals(&a);
  intersect(res, &b);
  return res->size == 1 && memcmp(res->ranges, R, sizeof(R)) == 0;
}

int test_and_disjoint() {
  double A[] = {0.5, 1.0          };
  double B[] = {          1.5, 2.0};
  INTERVAL a = {1, A};
  INTERVAL b = {1, B};
  INTERVAL *res = copyIntervals(&a);
  intersect(res, &b);
  return res->size == 0;
}

int test_and_multi() {
  double A[] = {0.0, 0.5, 1.0, 2.1, 2.5, 3.0};
  double B[] = {0.3, 0.6, 2.0, 2.6};
  double R[] = {0.3, 0.5, 2.0, 2.1, 2.5, 2.6};
  INTERVAL a = {3, A};
  INTERVAL b = {2, B};
  INTERVAL *res = copyIntervals(&a);
  intersect(res, &b);
  return res->size == 3 && memcmp(res->ranges, R, sizeof(R)) == 0;
}
int test_and_0() {
  double A[] = {0.0, 0.5};
  double B[] = {0.3, 0.5};
  double R[] = {0.3, 0.5};
  INTERVAL a = {1, A};
  INTERVAL b = {1, B};
  INTERVAL *res = copyIntervals(&a);
  intersect(res, &b);
  return res->size == 1 && memcmp(res->ranges, R, sizeof(R)) == 0;
}

int test_or_simple() {
  double A[] = {0.5, 1.5};
  double B[] = {1.0, 2.0};
  double R[] = {0.5, 2.0};
  INTERVAL a = {1, A};
  INTERVAL b = {1, B};
  INTERVAL *res = uniteNoTerm(&a, &b);
  return res->size == 1 && memcmp(res->ranges, R, sizeof(R)) == 0;
}

int test_or_disjoint() {
  double A[] = {0.5, 1.0};
  double B[] = {1.5, 2.0};
  double R[] = {0.5, 1.0, 1.5, 2.0};
  INTERVAL a = {1, A};
  INTERVAL b = {1, B};
  INTERVAL *res = uniteNoTerm(&a, &b);
  return res->size == 2 && memcmp(res->ranges, R, sizeof(R)) == 0;
}

int test_or_multi() {
  double A[] = {0.0, 0.5, 1.0, 2.0, 2.5, 3.0};
  double B[] = {0.3, 0.6, 2.0, 2.5};
  double R[] = {0.0, 0.6, 1.0, 3.0};
  INTERVAL a = {3, A};
  INTERVAL b = {2, B};
  INTERVAL *res = uniteNoTerm(&a, &b);
  return res->size == 2 && memcmp(res->ranges, R, sizeof(R)) == 0;
}
int test_or_0() {
  double A[] = {0.0, 0.5};
  double B[] = {0.3, 0.5};
  double R[] = {0.0, 0.5};
  INTERVAL a = {1, A};
  INTERVAL b = {1, B};
  INTERVAL *res = uniteNoTerm(&a, &b);
  return res->size == 1 && memcmp(res->ranges, R, sizeof(R)) == 0;
}


int test_from_1() {
  double A[] = {0.0, 0.5, 1.0, 2.0, 2.5, 3.0};
  double R[] = {0.2, 0.5, 1.0, 2.0, 2.5, 3.0};
  INTERVAL a = {3, A};
  INTERVAL *res = intersectFrom(&a, 0.2);
  return res->size == 3 && memcmp(res->ranges, R, sizeof(R)) == 0;
}
int test_from_2() {
  double A[] = {0.0, 0.5, 1.0, 2.0, 2.5, 3.0};
  INTERVAL a = {3, A};
  INTERVAL *res = intersectFrom(&a, 3.2);
  return res->size == 0;
}
int test_from_3() {
  double A[] = {0.0, 0.5, 1.0, 2.0, 2.5, 3.0};
  INTERVAL a = {3, A};
  INTERVAL *res = intersectFrom(&a, 3.0);
  return res->size == 0;
}
int test_from_4() {
  double A[] = {0.0, 0.5, 1.0, 2.0, 2.5, 3.0};
  double R[] = {2.7, 3.0};
  INTERVAL a = {3, A};
  INTERVAL *res = intersectFrom(&a, 2.7);
  return res->size == 1 && memcmp(res->ranges, R, sizeof(R)) == 0;
}

int test_to_1() {
  double A[] = {0.0, 0.5, 1.0, 2.0, 2.5, 3.0};
  double R[] = {0.0, 0.5, 1.0, 2.0, 2.5, 2.7};
  INTERVAL a = {3, A};
  INTERVAL *res = intersectTo(&a, 2.7);
  return res->size == 3 && memcmp(res->ranges, R, sizeof(R)) == 0;
}
int test_to_2() {
  double A[] = {0.3, 0.5, 1.0, 2.0, 2.5, 3.0};
  INTERVAL a = {3, A};
  INTERVAL *res = intersectTo(&a, 0.2);
  return res->size == 0;
}
int test_to_3() {
  double A[] = {0.2, 0.5, 1.0, 2.0, 2.5, 3.0};
  INTERVAL a = {3, A};
  INTERVAL *res = intersectTo(&a, 0.2);
  return res->size == 0;
}
int test_to_4() {
  double A[] = {0.0, 0.5, 1.0, 2.0, 2.5, 3.0};
  double R[] = {0.0, 0.4};
  INTERVAL a = {3, A};
  INTERVAL *res = intersectTo(&a, 0.4);
  return res->size == 1 && memcmp(res->ranges, R, sizeof(R)) == 0;
}

typedef int test_func(void);
typedef struct {
    test_func *f;
  const char *name;
} T;
int main() {
  T tests[] = {
    {test_inverse,          "inverse range"},
    {test_inverse_zero,     "inverse range starting in zero"},
    {test_inverse_end,      "inverse range ending at limit"},
    {test_inverse_complete, "inverse range covering entire possible space"},
    {test_inverse_empty,    "inverse empty range"},
    {test_inverse_zero2,    "inverse multi-range starting in zero"},
    {test_inverse_end2,     "inverse multi-range ending at limit"},

    {test_and_simple,       "intersect two ranges"},
    {test_and_disjoint,     "intersect two non-overlapping ranges"},
    {test_and_multi,        "intersection of two range lists"},
    {test_and_0,            "intersection test"},
    {test_or_simple,        "join two ranges"},
    {test_or_disjoint,      "join two non-overlapping ranges"},
    {test_or_multi,         "union of two range lists"},
    {test_or_0,             "union test"},

    {test_from_1,           "intersect from P - test 1"},
    {test_from_2,           "intersect from P - test 2"},
    {test_from_3,           "intersect from P - test 3"},
    {test_from_4,           "intersect from P - test 4"},

    {test_to_1,           "intersect to P - test 1"},
    {test_to_2,           "intersect to P - test 2"},
    {test_to_3,           "intersect to P - test 3"},
    {test_to_4,           "intersect to P - test 4"},
  };

  int overall_rc = 1;
  for (int i = 0; i < sizeof(tests)/sizeof(tests[0]); i++) {
    printf("%-60s ", tests[i].name);
    int rc = tests[i].f();
    printf("%s\n", rc? "\e[1;32mok\e[0m" : "\e[1;31mNOT OK!\e[0m");
    overall_rc &= rc;
  }
  return overall_rc == 0;
}
