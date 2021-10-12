/*
	CONNSIX notation:
		The format of single notation is [column][row].

		Strict notation:
			Columns are notated with alphabets A ~ T excluding I.
			Rows are notated with two-digit integers 01 ~ 19.
			Ex) K10, A01, T11, J04

		Generous notation:
			Columns are notated with case insensitive alphabets a ~ t excluding i.
			Rows are notated with one or two digit integers 1 ~ 19.
			Ex) k10, a1, T11, j04, C9
	
		Invalid notation: 
			When a column is not between A ~ H and J ~ T.
			When a row is not between 1 ~ 19.
			When a row is more thatn 2 digits.
			Ex)	T20, i09, U01, b003

		Multiple notation:
			Single notations are concatenated with ':' as a delimiter
			Ex) A10:T19
*/

/* 
	Requests a TCP connection to the CONNSIX server
	
	Return Value:
		On success, a string containing notations of redstones is returned
		If there are no redstones, ":" is returned.
		On error, NULL is returned
	
	Parameters:
		ip: The IP address of the CONNSIX server represented as a string
		port: The port number to the CONNSIX server
		color: The color of the player. 1 as black, 2 as white
	
	Errors:
		Failure to open a socket.
		Failure to make a connection.
		Error in receiving and validating redstones.

	Notes:
		A player must call lets_connect before using any other function.
*/
char *
lets_connect(char * ip, int port, char * color) ;

/*
	Draw stones and read game state

	Return Value:
		On success, a string containing information of the game state is returned. 
			If the game continues, the string contains a strict notation of the opponent's next stones.
			If the game is over, the string contains "WIN", "LOSE", or "EVEN".
		On error, NULL is returned.
	
	Parameters:
		draw: A string containing a generous notation of stones to draw
	
	Errors:
		Failure on sending a message
		Failure on receiving a message

	Notes:
		A black player must draw "K10" on the first move.
		A white player must draw "" on the first move.

		One of the error codes will be sent to the CONNSIX server for an erroneous arguement
			BADCOORD: When a notation is out of range.
			NOTEMPTY: When the player attempts to draw a stone on an occupied position.
			BADINPUT: When the arguement is not a valid CONNSIX notation.
*/
char *
draw_and_read(char * draw) ;

/*
	Returns the current board state of the given position

	Return Value:
		'E' is returned if no stone occupies the position
		'B' is returned if a black stone occupies the position.
		'W' is returned if a white stone occupies the position.
		'R' is returned if a red stone occupies the position.
		'N' is returned if input notation is invalid.
		NULL is returned on error.
	
	Parameters:
		position: A string containing a single notation

	Errors:
		Unknown board state(API malfunction)	
*/
char
get_stone_at (char * position) ;
