int test_2(char* str) {
int state = 4;
while(*str){
switch(state) {
case 0:
switch(*str) {
case 97: state=2; break;

default: return 0;
}break;
case 2:
switch(*str) {
case 97: state=1; break;

default: return 0;
}break;
case 5:
switch(*str) {
case 97: state=0; break;

default: return 0;
}break;
case 4:
switch(*str) {
case 97: state=5; break;

default: return 0;
}break;
case 3:
switch(*str) {
case 97: state=3; break;

default: return 0;
}break;
case 1:
switch(*str) {
case 97: state=3; break;

default: return 0;
}break;
}
str++;
}
switch(state) {
case 1:
case 0:

return 1;
default: return 0;
}
}