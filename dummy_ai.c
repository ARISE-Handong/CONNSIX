#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "connect6.h"

#define BLACK 1
#define WHITE 2

char wbuf[10] ;
char * rbuf ;

char *
generate_string (char hor1, int ver1, char hor2, int ver2)
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
	printf("redstone: %s\n", redstones) ;

	char * first ;
	if (color == BLACK) {
		first = draw_and_read("K10") ;
	} else if (color == WHITE) {
		first = draw_and_read("") ;
	} else {
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
		
		char * msg = generate_string(hor1, ver1, hor2, ver2) ;
		printf("draw: %s\n", msg) ;

		rbuf = draw_and_read(msg) ;
		if (rbuf == 0x0)
			break ;

		printf("read: %s\n", rbuf) ;
	}
	printf("Terminating...\n") ;
}
