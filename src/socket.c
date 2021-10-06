#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>

#include "socket.h"

#define BUFFERSIZE 1024

char buffer[BUFFERSIZE] ;

/* static functions */
static int
send_int (int sock_fd, int data)
{
	int s = 0 ;
	int len = sizeof(int) ;
	char * _data = (char *)&data ;

	while (len > 0 && (s = send(sock_fd, _data, len, 0)) > 0) {
		_data += s ;
		len -= s ;
	}
	if (s < 0)
		return 1 ;
	else
		return 0 ;
}

static int
recv_int (int sock_fd)
{
	int r = 0 ;
	int len = 0 ;
	int data = 0 ;
	char * _data = (char *)&data ;
	char * __data = _data ;

	while (len < sizeof(int) && (r = recv(sock_fd, _data, sizeof(int) - len, 0)) > 0) {
		_data += r ;
		len += r ;
	}

	if (r <= 0)
		return -1 ;
	else
		return data ;
}

static int
send_nbytes (int sock_fd, const char * data, int nbytes)
{
	int s = 0 ;
	int len = nbytes ;
	const char * _data = data ;

	while (len > 0 && (s = send(sock_fd, _data, len, 0)) > 0) {
		_data += s ;
		len -= s ;
	}

	if (s < 0)
		return 1 ;
	else
		return 0 ;
}

static int
recv_nbytes (int sock_fd, char * buf, int nbytes)
{
	int len = 0 ;
	int r = 0 ;

	while (len < nbytes && (r = recv(sock_fd, buf, nbytes, 0)) > 0) {
		buf += r ;
		len += r ;
	}
	*buf = '\0' ;

	if (r <= 0)
		return -1 ;
	else
		return 0 ;
}
/* static functions */

/* header functions */
int
send_msg (int sock_fd, const char * msg, int msglen)
{
	if (send_int(sock_fd, msglen) != 0)
		return 1 ;

	if (send_nbytes (sock_fd, msg, msglen) != 0)
		return 1 ;

	return 0 ;
}

char *
recv_msg (int sock_fd)
{
	int msglen = recv_int(sock_fd) ;

	if (msglen < 0 || BUFFERSIZE <= msglen) {
		return 0x0 ;
	} else if (msglen == 0) {
		strcpy(buffer, ":") ;
		return buffer ;
	}
	
	if (recv_nbytes(sock_fd, buffer, msglen) < 0)
		return 0x0 ;

	return buffer ;	
}

int
send_err (int sock_fd, const char * data, const char * err) {
	char msg[BUFFERSIZE] ;
	snprintf(msg, BUFFERSIZE-1, "%s$%s", err, data) ;

	if (send_msg(sock_fd, msg, strlen(msg)) != 0)
		return 1 ;
	
	return 0 ;
}
/* header functions */
