int
send_msg (int sock_fd, const char * data, int nbytes) ;

char *
recv_msg (int sock_fd) ; 

int
send_err (int sock_fd, const char * data, const char * err) ;
