/*
	CONNSIX notation:
		Rows are notated with case insensitive alphabets a ~ t excluding i.
		Columns are notated with integers 1 ~ 19. It may have leading 0s.

		The format of a single notation is [row][coloumn].
		Multiple notations are a concatination of single notations using ':' as a delimiter.

	Examples for a valid CONNSIX notation:
		K10, k10, a09:b3, A9:B03, H19:t1
	
	Examples for an invalid CONNSIX notation:
		T01:U02, i13:j14, 
*/

/* 
	Requests a TCP connection to the CONNSIX server
	
	Return Value:
		On success, a string containing redstone coordinates is returned
		On error, NULL is returned
	
	Parameters:
		ip: The IP address of the CONNSIX server represented as a string
		port: The port number to the CONNSIX server
		color: The color of the player. 1 as black, 2 as white
	
	Errors:
		Failure to open a socket.
		Failure to make a connection.
		Error in receiving and validating red stones.

	Notes:
		A player must call lets_connect before using any other function.
*/
char *
lets_connect(char * ip, int port, int color) ;

/*
	Draw stones and read game state

	Return Value:
		On success, string containing information of the game state is returned. 
		If the game continues, the string contains the one or two notations of the opponent's next stones.
		If the game is over, the string contains either "WIN" or "LOSE".
		On error, Null is returned.
	
	Parameters:
		draw: A string containing a notations of stones to draw
	
	Errors:
		Failure on sending a message
		Failure on receiving a message

	Notes:
		A black player must draw "K10" on the first move.
		A white player must draw "" on the first move.
*/
char *
draw_and_read(char * draw) ;

/*
	Get the board state of the given position

	Return Value:
		'E' is returned if no stone occupies the position
		'B' is returned if a black stone occupies the position.
		'W' is returned if a white stone occupies the position.
		'R' is returned if a red stone occupies the position.
		'N' is returned on error.
	
	Parameters:
		position: A string containing a single notation

	Errors:
		Invalid notation
		Unknown board state
*/
char
get_board(char * position) ;
