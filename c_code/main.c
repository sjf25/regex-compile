#include <stdio.h>
#include <string.h>
#include "email.h"

int main(int argc, char** argv) {
	//matches(argv[1]) ? puts("accept") : puts("reject");
	//char emails[900000][200];
	char emails[1000][200];
	char buffer[200];
	int total = 0;
	//while(scanf("%s", buffer) == 1) {
	while(fgets(buffer, 199, stdin) != NULL) {
		char* pos;
		if((pos = strchr(buffer, '\n')) != NULL)
			*pos = '\0';
		memcpy(emails[total], buffer, 200 * sizeof(char));
		total++;
	}

	int count = 0;
	int fail = 0;
	for(int i = 0; i < total; i++) {
		printf("'%s'\n", emails[i]);
		if(matches(emails[i]))
			count++;
		else
			fail++;
	}
	printf("total matches: %d\n", count);
	printf("total fail: %d\n", fail);
}
