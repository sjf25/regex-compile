int test_10(char* str) {
int state = 0;
while(*str){
switch(state) {
case 7:
switch(*str) {
case 97: state=19; break;

default: return 0;
}break;
case 0:
switch(*str) {
case 97: state=16; break;

default: return 0;
}break;
case 3:
switch(*str) {
case 97: state=6; break;

default: return 0;
}break;
case 4:
switch(*str) {
case 97: state=18; break;

default: return 0;
}break;
case 21:
switch(*str) {
case 97: state=11; break;

default: return 0;
}break;
case 17:
switch(*str) {
case 97: state=10; break;

default: return 0;
}break;
case 13:
switch(*str) {
case 97: state=4; break;

default: return 0;
}break;
case 2:
switch(*str) {
case 97: state=8; break;

default: return 0;
}break;
case 19:
switch(*str) {
case 97: state=1; break;

default: return 0;
}break;
case 1:
switch(*str) {
case 97: state=20; break;

default: return 0;
}break;
case 15:
switch(*str) {
case 97: state=5; break;

default: return 0;
}break;
case 11:
switch(*str) {
case 97: state=2; break;

default: return 0;
}break;
case 6:
switch(*str) {
case 97: state=6; break;

default: return 0;
}break;
case 9:
switch(*str) {
case 97: state=15; break;

default: return 0;
}break;
case 8:
switch(*str) {
case 97: state=14; break;

default: return 0;
}break;
case 10:
switch(*str) {
case 97: state=9; break;

default: return 0;
}break;
case 16:
switch(*str) {
case 97: state=21; break;

default: return 0;
}break;
case 18:
switch(*str) {
case 97: state=12; break;

default: return 0;
}break;
case 14:
switch(*str) {
case 97: state=7; break;

default: return 0;
}break;
case 5:
switch(*str) {
case 97: state=3; break;

default: return 0;
}break;
case 12:
switch(*str) {
case 97: state=17; break;

default: return 0;
}break;
case 20:
switch(*str) {
case 97: state=13; break;

default: return 0;
}break;
}
str++;
}
switch(state) {
case 3:
case 20:

return 1;
default: return 0;
}
}