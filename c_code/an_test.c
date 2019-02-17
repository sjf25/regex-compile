#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#include "repeat_test_1.h"
#include "repeat_test_2.h"
#include "repeat_test_10.h"
#include "repeat_test_20.h"
#include "repeat_test_30.h"
#include "repeat_test_40.h"
#include "repeat_test_50.h"
#include "repeat_test_60.h"
#include "repeat_test_70.h"
#include "repeat_test_80.h"
#include "repeat_test_90.h"
#include "repeat_test_100.h"

int main(int argc, char** argv) {
	if(argc != 3) {
		fprintf(stderr, "incorrect usage\n");
		return -1;
	}
	int indices[] = {1, 2, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	int (*f_table[])(char*)= {
		test_1,
		test_2,
		test_10,
		test_20,
		test_30,
		test_40,
		test_50,
		test_60,
		test_70,
		test_80,
		test_90,
		test_100
	};
	int n = atoi(argv[1]);
	int (*func)(char*) = NULL;
	for(int i = 0; i < sizeof(indices)/sizeof(int); i++) {
		if(n == indices[i])
			func = f_table[i];
	}
	if(func == NULL) {
		fprintf(stderr, "invalid n value\n");
	}
	bool accept = func(argv[2]);
	if(accept)
		puts("accept");
	else
		puts("reject");
}
