#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "../include/connsix.h"

#define BLACK 1
#define WHITE 2

char wbuf[10] ;	

int
main (int argc, char * argv[])
{
	srand(time(0x0)) ;

	if (argc != 4) {
		fprintf(stderr, "Usage: ./dummy_ai [server ip address] [port number] [color]") ;
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
		fprintf(stderr, "color must be \"black\" or \"white\"") ;
		exit(EXIT_SUCCESS) ;
	}

	char * first = draw_and_read(wbuf) ;
	if (first == 0x0) {
		fprintf(stderr, "first turn error!\n") ;
		exit(EXIT_FAILURE) ;
	}
	printf("first: %s\n", first) ;

	char hor1 = '\0' ;
	char hor2 = '\0' ;
	int ver1 = 0 ;
	int ver2 = 0 ;

	while (1) {
		hor1 = (rand() % 19) + 'A' ;
		hor1 += ('I' <= hor1) ? 1 : 0 ;
		ver1 = (rand() % 19) + 1 ;
			
		hor2 = (rand() % 19) + 'A' ;
		hor2 += ('I' <= hor2) ? 1 : 0 ;
		ver2 = (rand() % 19) + 1 ;
		
		snprintf(wbuf, 10, "%c%02d:%c%02d", hor1, ver1, hor2, ver2) ;
		
		printf("[draw] %s\n", wbuf) ;

		char * rbuf = draw_and_read(wbuf) ;
		if (rbuf == 0x0) {
			printf("Error!\n") ;
			break ;
		}
		if (strcmp(rbuf, "WIN") == 0 || strcmp(rbuf, "LOSE") == 0 || strcmp(rbuf, "EVEN") == 0) {
			printf("Game Over. You %s!\n", rbuf) ;
			break ;
		}

		printf("[read] %s\n", rbuf) ;
	}

	printf("Terminating...\n") ;

	return 0 ;
}
