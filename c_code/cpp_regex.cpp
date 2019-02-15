#include <iostream>
#include <string>
#include <regex>

int main(int argc, char** argv) {
	char emails[900000][200];
	char buffer[200];
	int total = 0;
	while(scanf("%s", buffer) == 1) {
		memcpy(emails[total], buffer, 200 * sizeof(char));
		total++;
	}

	std::regex r("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");

	int count = 0;
	for(int i = 0; i < total; i++) {
		if(std::regex_match(emails[i], r))
			count++;
	}
	printf("total matches: %d\n", count);
}
