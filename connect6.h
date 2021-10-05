/********************** 
	Return 1 on success
	Return 0 on failure

	Request TCP connection to connect6 server at ip/port as color.
	Returns redstones on success
***********************/
char *
lets_connect(char * ip, int port, int color) ;

/**********************
	Return 1 on success
	Return error code on failure

	String format example: A01:B01(fixed 7 bytes)

	If home is "K10" or "", skip drawing and just wait
	Black must draw_and_wait("K10", away) on first move
	White must draw_and_wait("", away) on first move
***********************/
char *
draw_and_read(char * draw) ;

/***********************
	get status of board at given coordinate
	Black = B
	White = W
	Empty = E
	Red   = R
	Error = N // ex) out of bounds
************************/
char
get_board(char * position) ;
