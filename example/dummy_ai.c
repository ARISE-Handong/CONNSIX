#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "../include/connect6.h"

#define BLACK 1
#define WHITE 2

char wbuf[10] ;
char * rbuf ;

char *
generate_msg (char hor1, int ver1, char hor2, int ver2)
{
	snprintf(wbuf, 10, "%c%02d:%c%02d", hor1, ver1, hor2, ver2) ;

	return wbuf ;
}

int
is_empty(char hor, int ver)
{
	char pos[5] ;
	snprintf(pos, 4, "%c%02d", hor, ver) ;

	char status = get_board(pos) ;
	printf("%s is %c\n", pos, status) ;
	if (status == 'E')
		return 1 ;
	else
		return 0 ;
}

int
main (int argc, char * argv[])
{
	srand(time(0x0)) ;

	if (argc != 4) {
		fprintf(stderr, "Usage: ./a.out \"server ip address\" \"port number\" \"color\"") ;
		exit(EXIT_SUCCESS) ;
	}
	char * ip = argv[1] ;
	int port = atoi(argv[2]) ;
	int color = atoi(argv[3]) ;

	char * redstones = lets_connect(ip, port, color) ;
	if (redstones == 0x0) {
		fprintf(stderr, "lets_connect() error!\n") ;
		exit(EXIT_FAILURE) ;
	}
	printf("redstone: %s\n", redstones) ;

	if (color == BLACK) {
		strcpy(wbuf, "K10") ;
	} else if (color == WHITE) {
		strcpy(wbuf, "") ;
	} else {
		printf("color must be \"black\" or \"white\"") ;
		exit(EXIT_SUCCESS) ;
	}

	char * first = draw_and_read(wbuf) ;
	if (first == 0x0) {
		fprintf(stderr, "first turn error!\n") ;
		exit(EXIT_FAILURE) ;
	}
	printf("first: %s\n", first) ;

	while (1) {
		char hor1 = '\0' ;
		char hor2 = '\0' ;
		int ver1 = 0 ;
		int ver2 = 0 ;
		do {
			hor1 = (rand() % 19) + 'A' ;
			hor1 += ('I' <= hor1) ? 1 : 0 ;
			ver1 = (rand() % 19) + 1 ;
			do {	
				hor2 = (rand() % 19) + 'A' ;
				hor2 += ('I' <= hor2) ? 1 : 0 ;
				ver2 = (rand() % 19) + 1 ;
			} while (hor1 == hor2 && ver1 == ver2) ;
			printf("%c%d:%c%d\n", hor1, ver1, hor2, ver2) ;
		} while (is_empty(hor1, ver1) == 0 || is_empty(hor2, ver2) == 0) ;
		
		char * msg = generate_msg(hor1, ver1, hor2, ver2) ;
		printf("draw: %s\n", msg) ;

		rbuf = draw_and_read(msg) ;
		if (rbuf == 0x0) {
			printf("Error!\n") ;
			break ;
		}
		if (strcmp(rbuf, "WIN") == 0 || strcmp(rbuf, "LOSE") == 0) {
			printf("Game Over. You %s!\n", rbuf) ;
			break ;
		}
		printf("read: %s\n", rbuf) ;
	}
	printf("Terminating...\n") ;

	return 0 ;
}
