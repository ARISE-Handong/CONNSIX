'''
	About [coordinate] notations:
		A [coordinate] is consisted with a character followed by a number. The character should be an 
		alphabet from A to T excluding I. The alphabet represents the column and the number represents 
		the row. The following are some alias for the notations:
			Strict single notation:
				Columns are notated with uppercase alphabets A ~ T excluding I. 
				Rows are notated with two-digit integers 01 ~ 19. 
				EX) K10, A01, T11, J04
			Extended single notation:
				Columns are notated with case insensitive alphabets a ~ t excluding i.
				Rows are notated with one or two digit integers 1 ~ 19.
				Ex) k10, a1, t11, j04, C9
			Multiple notation:
				Single notations are concatenated with : as a delimiter.
				Ex) A10:T19
			Invalid notaion: 
				When a column is not between A ~ H and J ~ T.
				When a row is not between 1 ~ 19.
				When a row is more than 2 digits.
				Ex) T20, i09, U01, b003
'''

import socket

class ConnectionError(Exception):
	pass

class InputError(Exception):
	pass

class ApiError(Exception):
	pass

def __init__():
	global _lcs_board, _conn, _home, _away, _first, _red
	_lcs_board = [[0 for i in range(19)] for j in range(19)]
	_conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	_conn.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
	_home = -1
	_away = -1
	_first = 1
	_red = 3 


'''
	lets_connect() requests a TCP connection to the CONNSIX server.

	PARAMETER of lets_connect():
		Ip as string, port as integer, color as a string. Color should be either "BLACK" or "WHITE".

	RETURN of lets_connect():
		1. An empty string indicating there is no red stone 
		2. String that contain red stone information in such format:
			"[coordinate]:[coordinate]:[coordinate]...." 
		The following are some examples of returns of connect():
			"A10:B01:C17:J11"
			"F14"
			"J11:K12"
			""

	EXCEPTIONS of lets_connect():
		1. ConnectionError: This happens when the input ip or port is incorrect	or the server is 
			busy or invalid so that the connection can't be made.
		2. InputError: This happens when the input color is neither "BLACK" or "WHITE"

	NOTE THAT:
		The return of lets_connect() will always be in strict notation.
		lets_connect() MUST be called before calling draw_and_read().
'''
def lets_connect(ip:str, port:int, color:str) -> str:
	global _home, _away, _lcs_board
	if color == "BLACK":
		_home = 1
		_away = 2
	elif color == "WHITE":
		_home = 2
		_away = 1
	else:
		raise InputError("lets_connect(): input \"BLACK\" or \"WHITE\" for color")
	try:
		_conn.connect((ip, port))
	except ConnectionRefusedError:
		raise ConnectionError("lets_connect(): failed to connect with ip: " + ip + " port: " + str(port))
		
	size = int.from_bytes(_conn.recv(4), "little")
	data = _conn.recv(size).decode("utf-8")
	if size:
		if data[0] == ':':
			raise ApiError("lets_connect() failed, please contact maintainance team: " + data)
		red_coor = data.split(':')
		for coors in red_coor:
			(x, y) = _a_coor_to_num(coors)
			_lcs_board[y][x] = _red
	return data


'''
	draw_and_read() sends the move to the server and read the opponent's move from server.

	Parameter of draw_and_read():
		user_move as string with one of the following three cases:
			"K10" : the very first move for the user with black stone
			"" : the very first move for ther user with white stone or
			"[coordinate]:[coordinate]" : both user's move during the game. 

	RETURN of draw_and read():
		1. The opponent's move in one of the two following two cases:
			"K10" or 
			"[coordinate]:[coordinate]"
		2. Game termination signal:
			"WIN" indicating the client won the game and
			"LOSE" indicating the client lost,
			"EVEN" indicating the draw.

	EXCEPTION of draw_and_read() (raised to the server):
		"BADCOORD$": the input [coordinate] is out of bound(n > 19 or n < 1), both character and number 
			can cause this error. 
		"NOTEMPTY$": the input [coordinate] is occupied position.
		"BADINPUT$": the input [coordinate] is in invalid notation form or a random string. 
	
	NOTE THAT:
		The first move of users should be "K10" or "", the user will be disqualified otherwise.
		Extended notation is accepted as the parameter of draw_and_read().
		draw_and_read() will always return in strict notation format.
		The exception (or the error message) is always sent to the server not the client. Once an error 
			message is sent to the server, there is no second chance provided to correct the move.
		lets_connect() MUST be called before calling draw_and_read().
'''
def draw_and_read(user_move:str) -> str:
	global _lcs_board, _first
	msg = user_move.replace(" ", "").upper()
	if msg == "K10":
		if _first == 0 or _home != 1:
			msg = "BADINPUT$" + user_move
		else:
			_lcs_board[9][9] = _home
			_first = 0
	elif msg == "":
		if _first == 0 or _home != 2:
			msg = "BADINPUT$" + user_move
		else:
			_first = 0
	else:
		stones = msg.split(':')
		if _first:
			msg = "BADINPUT$" + user_move
		elif len(stones) != 2:
			msg = "BADINPUT$" + user_move
		else:
			msg = "" + stones[0][0] + "{:02d}:".format(int(stones[0][1:])) + stones[1][0] + "{:02d}".format(int(stones[1][1:]))
			for coors in stones:
				parsed_num = _a_coor_to_num(coors)
				if parsed_num == "BADINPUT":
					msg = "BADINPUT$" + user_move
				else:
					(x,y) = parsed_num
					if x > 18 or x < 0 or y > 18 or y < 0:
						msg = "BADCOORD$" + user_move
					elif _lcs_board[y][x] != 0:
						msg = "NOTEMPYT$" + user_move
					else:
						_lcs_board[y][x] = _home
	if len(msg):
		_conn.sendall((len(msg)).to_bytes(4, byteorder='little') + str.encode(msg))

	size = int.from_bytes(_conn.recv(4), "little")
	away_move = _conn.recv(size).decode("utf-8").replace(" ", "").upper()
	if away_move == "WIN" or away_move == "LOSE" or away_move == "EVEN":
		pass
	elif away_move == "K10":
		_lcs_board[9][9] = _away
	else: 
		away_coor = away_move.split(':')
		for coors in away_coor:
			(x, y) = _a_coor_to_num(coors)
			_lcs_board[y][x] = _away
	return away_move


'''
	get_stone_at() gets the state of the given position.

	PARAMETER of get_stone_at():
		Position as a string. The position should be in "[coordinate]" format.

	RETURN of get_stone_at():
		A character which indicates the stone placed in the requested coordinate. 
		Each character means the following:
			'E' : empty
			'B' : black stone
			'W' : white stone
			'R' : red stone
			'N' : wrong input
	
	NOTE THAT:
		get_stone_at() may throw ApiError indicating api failure. 
		Extended notation is accepted as the parameter of get_steon_at().
'''
def get_stone_at(position:str) -> chr:
	result = _a_coor_to_num(position.replace(" ",""). upper())
	if result == "BADINPUT": 
		return 'N'
	(x,y) = result
	if x > 18 or x < 0 or y > 18 or y < 0:
		return 'N'
	if _lcs_board[y][x] == 0:
		return 'E'
	elif _lcs_board[y][x] == 1:
		return 'B'
	elif _lcs_board[y][x] == 2: 
		return 'W'
	elif _lcs_board[y][x] == 3:
		return 'R'
	raise ApiError("query() failed, please contact maintainance team: " + position)


def _a_coor_to_num(coor): 
	if not coor[0].isalpha() or not coor[1:].isnumeric() or coor[0] == 'I':
		return "BADINPUT" 
	x = ord(coor[0]) - 65 
	y = 19 - int(coor[1:])
	if x > 8:
		x = x - 1
	return (x, y)
