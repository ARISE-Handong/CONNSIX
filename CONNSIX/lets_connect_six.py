import socket

class ConnectionError(Exception):
	pass

class InputError(Exception):
	pass

class ApiError(Exception):
	pass

def __init__():
	global _lcs_board, _socket_to_server, _home, _away
	_lcs_board = [[0 for i in range(19)] for j in range(19)]
	_socket_to_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	_socket_to_server.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
	_home = -1
	_away = -1

'''
	input: ip as string, port as integer, color between 1 or 2 as integer.

	connect() throws 2 exceptions: 
		1. ConnectionError: this happens when the input ip or port is incorrect
			or the server is invalid so that the connection can't be made.
		2. InputError: this happens when the input color is neither 1 or 2.

	connect() returns string that contain red stone information in such format:
			"[coordination]:[coordination]:[coordination]...." 
		some examples:
			"A10:B01:C17:J11"
			"F14"
			"J11:K12"
	occationally the message from server are cut and the function may cause the abortion 
	of program.
'''
def connect(ip:str, port:int, color:int) -> str:
	global _home, _away, _lcs_board
	_home = color
	if _home == 1: 
		_away = 2
	elif _home == 2: 
		_away = 1
	else:
		raise InputError("connect(): input 1 or 2 for color")
	try:
		_socket_to_server.connect((ip, port))
	except ConnectionRefusedError:
		raise ConnectionError("connect(): failed to connect with ip: " + ip + " port: " + str(port))
		
	size = int.from_bytes(_socket_to_server.recv(4), "little")
	data = _socket_to_server.recv(size).decode("utf-8")
	print("DEBUG: from server: " + str(size) + " " + data) 
	looper = data.count(':') + 1
	for i in range(looper):
		chr_pos = 4 * i
		num_pos = 4 * i + 1
		x = ord(data[chr_pos]) - 65
		if x > 8:
			x = x - 1
		y = 19 - int(data[num_pos:num_pos+2])
		_lcs_board[y][x] = 3
	return data

'''
	input: user_move as string with one of the following format:
		"K10" : the very first move of the game for the user with black stone. 
		"" : the very first move of the game for the user with white stone. 
		"[coordinate]:[coordinate]" : both user's move in the game. 
	a [coordinate] is consisted with a character followed by a number. the character should
	be an alphabet from 'A' to 'T' excluding 'I'. both upper and lowercase are accepted.
	the number should be in range between 1 and 19, both 1 and 19 are accepted. the number 
	can be written as 1 and 2 digits. ex) 1 or 01. 001 is not accepted. 

	draw_and_read() throws InputError exception. the error message of the error means the following:
		"BADCOORD$": the input coordination is out of bound(n > 19 or n < 1, both character
			and number can cause this error. 
		"NOTEMPTY$": the coordination is occupied by other stones before the input. 
		"BADINPUT$": everything else other than the previous two cases. it is not in the format
			most likely. 
	once an exception is raised, there is no second chance provided to correct the move.

	draw_and read() returns the opponent's move in one of the two following format:
		"K10" and "[coordinate]:[coordinate]"
	there will be no empty string returned from the function and the character will always be
	in uppercase. 
'''
def draw_and_read(user_move:str) -> str:
	global _lcs_board
	msg = user_move.replace(" ", "").upper()
	if msg == "K10":
		_lcs_board[9][9] = _home
	elif msg == "":
		pass
	else:
		stones = msg.split(':')
		if len(stones) != 2:
			msg = "BADINPUT$" + user_move
		else:
			for coors in stones:
				parsed_num = _a_coor_to_num(coors)
				if parsed_num == "BADINPUT$":
					msg = parsed_num + user_move
				else:
					(x,y) = parsed_num
					if x > 18 or x < 0 or y > 18 or y < 0:
						msg = "BADCOORD$" + user_move
					elif _lcs_board[y][x] != 0:
						msg = "NOTEMPYT$" + user_move
					else:
						_lcs_board[y][x] = _home
						_print_board()
	if len(msg):
		_socket_to_server.sendall((len(msg)).to_bytes(4, byteorder='little') + str.encode(msg))
		if len(msg) > 7:
			raise InputError("draw_and_read(): " + msg)
	size = int.from_bytes(_socket_to_server.recv(4), "little")
	away_move = _socket_to_server.recv(size).decode("utf-8")
	away_move = away_move.replace(" ", "").upper()
	if away_move == "K10":
		_lcs_board[9][9] = _away
	else: 
		away_coor = away_move.split(':')
		for coors in away_coor:
			(x, y) = _a_coor_to_num(coors)
			_lcs_board[y][x] = _away
	_print_board()
	return away_move

'''
	input position as string. position should be in "[coordinate]" format.
	[coordinate] consists of a character and a number like "K10". read description of
	draw_and_read() for further information.

	get_lcs_board() throws following 2 exceptions:
		1. InputError, the message means the following:
			"BADCOORD$" : out of bound. both character and number can cause this error.
			"BADINPUT$" : everything else. it is most likely that the input did not follow the
				format. 
		2. ApiError:
			this is an error caused by a fault in the api, please contact the maintainance team
			for the fix.
			
	get_lcs_board() returns a character which indicates the stone placed in the requested 
	coordinate. each character means the following:
		'E' : empty
		'B' : black stone
		'W' : white stone
		'R' : red stone
'''

def get_lcs_board(position:str) -> chr:
	result = _a_coor_to_num(ask)
	if result == "BADINPUT": 
		raise InputError("get_lcs_board: BADINPUT$" + position)
	(x,y) = result
	if x > 18 or x < 0 or y > 18 or y < 0:
		raise InputError("get_lcs_board: BADCOORD$" + position)
	if _lcs_board[y][x] == 0:
		return 'E'
	elif _lcs_board[y][x] == 1:
		return 'B'
	elif _lcs_board[y][x] == 2: 
		return 'W'
	elif _lcs_board[y][x] == 3:
		return 'R'
	raise ApiError("ERROR get_lcs_board, plz contact maintainance team")


def _a_coor_to_num(coor): 
	if not coor[0].isalpha() or not coor[1:].isnumeric() or coor[0] == 'I':
		return "BADINPUT" 
	x = ord(coor[0]) - 65 
	y = 19 - int(coor[1:])
	if x > 8:
		x = x - 1
	return (x, y)


def _print_board():
	for row in _lcs_board:
		print(row)
