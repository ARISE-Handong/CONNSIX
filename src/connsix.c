#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>

#include "../include/connsix.h"
#include "socket.h"

/* static data structures */
static int sock_fd ;
static char * bufptr ;

typedef enum _status {
	EMPTY,
	BLACK,
	WHITE,
	RED,
} status_t ;

static status_t board[19][19] ; 

static status_t player_color ;
static status_t opponent_color ;

static int first_turn ;

typedef enum _errcode {
	BADCOORD,
	NOTEMPTY,
	BADINPUT,
	GOOD,
} errcode_t ;

static const char * err_str[3] = {
	"BADCOORD",
	"NOTEMPTY",
	"BADINPUT",
} ;
/* static data structures */

/* static functions */
static void
print_board() {
	char visual[] = "*@OX" ;
	printf("   ABCDEFGHJKLMNOPQRST\n") ;
	for (int ver = 19; ver > 0; ver--) {
		printf("%2d ", ver) ;
		for (int hor = 0; hor < 19; hor++)
			printf("%c", visual[board[ver-1][hor]]) ;
		printf(" %d\n", ver) ;
	}
	printf("   ABCDEFGHJKLMNOPQRST\n") ;
}

static errcode_t 
parse (char * stone, int * hor, int * ver)
{
	char a = '\0' ;
	int _hor = -1 ;
	int _ver = -1 ;
	int n = 0 ;
	if (sscanf(stone, "%c%2d%n", &a, &_ver, &n) != 2 || stone[n] != '\0')
		return BADINPUT ;

	if ('a' <= a && a <= 'h') {
		_hor = a - 'a' ;
	} else if ('A' <= a && a <= 'H') {
		_hor = a - 'A' ;
	} else if ('j' <= a && a <= 't') {
		_hor = a - 'a' - 1 ;
	} else if ('J' <= a && a <= 'T') {
		_hor = a - 'A' - 1 ;
	} else {
		return BADCOORD ;
	}
	
	if (0 < _ver && _ver <= 19) {
		_ver -= 1 ;
	} else {
		return BADCOORD ;
	}
	
	*hor = _hor ;
	*ver = _ver ;

	return GOOD ;	
}

static int
set_redstones (char * redstones)
{
	char * _redstones = strdup(redstones) ;
	if (_redstones == 0x0) {
		return 1 ;
	}
	char * stone = strtok(_redstones, ":") ;
	while (stone != 0x0) {

		int hor = -1 ;
		int ver = -1 ;
		errcode_t err = parse(stone, &hor, &ver) ;
		if (err != GOOD) {
			return 1 ;
		}
		
		if (board[ver][hor] == EMPTY) {
			board[ver][hor] = RED ;
		} else {
			return 1 ;
		}

		stone = strtok(0x0, ":") ;
	} 
	free(_redstones) ;

#ifdef PRINT
	print_board() ;
#endif

	return 0 ;
}

static errcode_t 
update_board(char * stones, status_t color)
{
	char * _stones = strdup(stones) ;
	if (_stones == 0x0) {
		return BADINPUT ;
	}

	char * stone[2] ;
	stone[0] = strtok(_stones, ":") ;
	if (stone[0] == 0x0) {
		return BADINPUT ;
	}
	stone[1] = strtok(0x0, ":") ;
	if (stone[1] == 0x0) {
		return BADINPUT ;
	}
	char * tok = strtok(0x0, ":") ;
	if (tok != 0x0) {
		return BADINPUT ;
	}
		
	for (int i = 0; i < 2; i++) {
		int hor = -1 ;
		int ver = -1 ;
		errcode_t err = parse(stone[i], &hor, &ver) ;
		if (err != GOOD) {
			return err ;
		}

		if (board[ver][hor] == EMPTY) {
			board[ver][hor] = color ;
		} else {
			return NOTEMPTY ;
		}
	}
	free(_stones) ;	

	return GOOD ;
}

static void
strict_format (char * stones) {
	char hor1 = '\0' ;
	char hor2 = '\0' ;
	int ver1 = -1 ;
	int ver2 = -1 ;

	int n = 0 ;
	if (sscanf(stones, "%c%2d:%c%2d%n", &hor1, &ver1, &hor2, &ver2, &n) != 4 || stones[n] != '\0')
		return ;
	
	if ('a' <= hor1)
		hor1 = hor1 - 'a' + 'A' ;
	if ('a' <= hor2)
		hor2 = hor2 - 'a' + 'A' ;

	sprintf(stones, "%c%02d:%c%02d", hor1, ver1, hor2, ver2) ;
	
	return ;
}

void
cleanup (void)
{
	close(sock_fd) ;
}
/* static functions */

/* API functions */
char *
lets_connect (char * ip, int port, char * color)
{
	if (strcmp(color, "black") == 0) {
		player_color = BLACK ;
		opponent_color = WHITE ;
	} else if (strcmp(color, "white") == 0) {
		player_color = WHITE ;
		opponent_color = BLACK ;
	} else {
		return 0x0 ;
	}
	
	for (int i = 0; i < 19; i++)
		for (int j = 0; j < 19; j++)
			board[i][j] = EMPTY ;

	first_turn = 1 ;

	struct sockaddr_in serv_addr ;

	sock_fd = socket(AF_INET, SOCK_STREAM, 0) ;
	if (sock_fd <= 0) {
		return 0x0 ;
	} else {
		atexit(cleanup) ;
	}

	int optval = 1 ;
	if (setsockopt(sock_fd, IPPROTO_TCP, TCP_NODELAY, &optval, sizeof(optval)) < 0) {
		return 0x0 ;
	}

	memset(&serv_addr, 0, sizeof(serv_addr)) ;
	serv_addr.sin_family = AF_INET ;
	serv_addr.sin_port = htons(port) ;
	if (inet_pton(AF_INET, ip, &serv_addr.sin_addr) <= 0) {
		return 0x0 ;
	}

	if (connect(sock_fd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
		return 0x0 ;
	}

	bufptr = recv_msg(sock_fd) ;
	if (bufptr == 0x0) {
		return 0x0 ;
	}
	
	if (set_redstones(bufptr) != 0) {
		return 0x0 ;
	}

#ifdef PRINT	
	print_board() ;
#endif
	
	strict_format(bufptr) ;
	return bufptr ;
}

char *
draw_and_read (char * draw)
{
	if (first_turn) {
		first_turn = 0 ;
		if (player_color == BLACK && (strcmp(draw, "K10") == 0 || strcmp(draw, "k10") == 0)) {
			board[9][9] = player_color ;
			if (send_msg(sock_fd, draw, strlen(draw)) != 0)
				return 0x0 ;
		} else if (player_color == WHITE && strcmp(draw, "") == 0) {
			bufptr = recv_msg(sock_fd) ;
			if (bufptr == 0x0)
				return 0x0 ;
			if (strcmp(bufptr, "K10") != 0 && strcmp(bufptr, "k10") != 0)
				return 0x0 ;
			board[9][9] = opponent_color ;

			return bufptr ;
		} else {
			send_err(sock_fd, draw, err_str[BADINPUT]) ;
		}
	} else {
		errcode_t err = update_board(draw, player_color) ;

		if (err != GOOD) {
			send_err(sock_fd, draw, err_str[err]) ;
		} else {
			strict_format(draw) ;
			if (send_msg(sock_fd, draw, strlen(draw)) != 0)
				return 0x0 ;
		}
	}

	bufptr = recv_msg(sock_fd) ;
	if (bufptr == 0x0)
		return 0x0 ;

	if (strcmp(bufptr, "WIN") != 0 && strcmp(bufptr, "LOSE") != 0 && strcmp(bufptr, "TIE") != 0) {
		update_board(bufptr, opponent_color) ; 
		strict_format(bufptr) ;
	}

	return bufptr ;
}

char
get_stone_at (char * position) {
	int hor = -1 ;
	int ver = -1 ;
	errcode_t err = parse(position, &hor, &ver) ;
	if (err != GOOD)
		return 'N' ;

	switch (board[ver][hor]) {
		case EMPTY : return 'E' ;
		case BLACK : return 'B' ;
		case WHITE : return 'W' ;
		case RED : return 'R' ; 
		default : return 0x0 ;
	}
}
/* API functions */
