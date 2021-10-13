/*
	CONNSIX notation(표기 방법):
		The format of single notation is [column][row].
		하나의 좌표는 [행][줄]로 단수 표기한다.

		Strict notation(좌표 기본 표기):
			Columns are notated with alphabets A ~ T excluding I.
			행은 'I'를 제회한 대문자 알파벳 A ~ T로 표기한다.
			Rows are notated with two-digit integers 01 ~ 19.
			줄은 두 자리 정수 01 ~ 19로 표기한다.
			Ex) K10, A01, T11, J04

		Extended notation(좌표 확장 표기):
			Columns are notated with case insensitive alphabets a ~ t excluding i.
			행은 대소문자 구분없이 'i'를 제외한 알파벳 a ~ t로 표기한다.
			Rows are notated with one or two digit integers 1 ~ 19.
			줄은 한 자리 또는 두자리 정수 1 ~ 19로 표기한다.
			Ex) k10, a1, T11, j04, C9
	
		Invalid notation(무효한 표기): 
			When a column is not between A ~ H or J ~ T.
			행이 A ~ H 또는 J ~ T 사이가 아닌 경우.
			When a row is not between 1 ~ 19.
			줄이 1 ~ 19 사이가 아닌 경우.
			When a row is more that 2 digits.
			줄이 세 자리 수 이상인 경우.
			Ex)	T20, i09, U01, b003

		Multiple notation(복수 좌표 표기):
			Single notations are concatenated with ':' as a delimiter
			단수 표기를 ':'를 구분자로 사용하여 표기한다.
			Ex) A10:T19
*/

/*	char * lets_connect (char * ip, int port, char * color):

	Requests a TCP connection to the CONNSIX server
	CONNSIX 서버에게 TCP 연결을 요청한다.
	
	Return Value(반환값):
		On success, a string containing Strict notation of redstones is returned
		If there are no redstones, ":" is returned.
		On error, NULL is returned
		성공한 경우, 기본 표기된 적돌 정보가 담긴 string을 반환한다.
		적돌이 없는 경우 ":"이 반환된다.
		오류가 발생한 경우 NULL이 반환된다.
	
	Parameters(매개변수):
		ip: The IP address of the CONNSIX server represented as a string
			string으로 표현된 CONNSIX 서버의 아이피 주소.
		port: The port number to the CONNSIX server
			  CONNSIX 서버의 포트 번호.
		color: The color of the player. Must be "black" or "white".
			   플레이어의 색깔, "black" 또는 "white"이어야 한다.

	Errors(오류):
		Failure to open a socket.
		소켓 생성을 실패한 경우.
		Failure to make a TCP connection.
		TCP 연결에 실패한 경우.
		Error in receiving and validating redstones.
		적돌을 읽고 확인하는 과정에 오류가 생긴 경우.

	Notes(주의점):
		A player must call lets_connect once before using any other function.
		다른 함수를 호출하기 전에 lets_connect를 무조건 한 번 호출해야 한다.

*/
char *
lets_connect (char * ip, int port, char * color) ;

/*	char * draw_and_read(char * draw):

	Draw stones and read game state
	수를 두고 게임 상황으 읽는다. 

	Return Value(반환값):
		On success, a string containing information of the game state is returned. 
		성공할 경우, 게임 상황에 대한 정보가 담긴 string이 반환된다.
			If the game continues, the string contains a Strict notation of the opponent's next stones.
			게임이 계속 진행될 경우, 기본 표기된 상대방의 수를 담고있는 string이 반환된다.
			If the game is over, the string contains "WIN", "LOSE", or "TIE".
			게임이 종료된 경우, "WIN", "LOSE", 또는 "TIE"가 담긴 string이 반환된다. 
		On error, NULL is returned.
		오류가 발생한 경우, NULL이 반환된다.
		
	
	Parameters(매개변수):
		draw: A string containing a Generous notation of two stones to draw
			  확장 표기된 두개의 수가 담긴 string.
	
	Errors(오류):
		Failure on sending a message
		메세지를 전송하는데 실패한 경우.
		Failure on receiving a message
		메세지를 수신하는데 실패한 경우.

	Notes(주의점):
		A black player must draw "K10" on the first move. 
		흑돌을 두는 플레이어는 첫 수에 무조건 "K10"을 두어야 한다.
		A white player must draw "" on the first move.
		백돌을 두는 플레이어는 첫 수에 무조건 ""을 두어야 한다.

		One of the error codes will be sent to the CONNSIX server for an erroneous arguement
		인수가 잘못된 경우 다음 에러코드 중 하나가 CONNSIX 서버로 전송된다.
			BADCOORD: When a notation is out of range.
					  좌표 범위가 벗어난 경우.
			NOTEMPTY: When the player attempts to draw a stone on an occupied position.
					  빈 좌표가 아닌 곳에 수를 두려고 하는 경우.
			BADINPUT: When the argument does not have exactly two stones to draw or when it is not a valid CONNSIX notation.
					  정확히 2개의 수만 담겨있지 않거나 유효한 CONNSIX 표기 방법이 아닌 경우.
*/
char *
draw_and_read (char * draw) ;

/*	char get_stone_at (char * position):

	Returns the information of the stone at given position
	주어진 좌표에 있는 돌의 정보를 반환한다.

	Return Value(반환값):
		'E' is returned if no stone occupies the position
		빈 좌표인 경우 'E'가 반환된다.
		'B' is returned if a black stone occupies the position.
		흑돌인 경우 'B'가 반환된다.
		'W' is returned if a white stone occupies the position.
		백돌인 경우 'W'가 반환된다.
		'R' is returned if a red stone occupies the position.
		적돌인 경우 'R'이 반환된다.
		'N' is returned if input notation is invalid.
		무효한 좌표 표기인 경우 'N'이 반환된다.
		NULL is returned on error.
		오류가 발생한 경우 NULL이 반환된다.
	
	Parameters(매개변수):
		position: A string containing a single extended notation
				  단수 좌표 확장 표기된 string.

	Errors(오류):
		Unknown stone information(API defection)
		알 수 없는 돌 정보(API 결함)
*/
char
get_stone_at (char * position) ;
