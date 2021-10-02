from CONNSIX import lets_connect_six
import random

dummy_board = [[0 for i in range(19)] for j in range(19)]
dummy_home = -1
dummy_away = -1

def create_payload(x1, y1, x2, y2):
	if x1 > 7: 
		x1 = x1 + 1
	if x2 > 7: 
		x2 = x2 + 1
	x1 = chr(x1 + 65)
	x2 = chr(x2 + 65)
	y1 = str(y1)
	y2 = str(y2)
	if len(y1) == 1:
		y1 = "0" + y1
	if len(y2) == 1:
		y2 = "0" + y2
	return "" + x1 + y1 + ":" + x2 + y2
	

def make_move():
	x1 = random.randint(0, 18)
	y1 = random.randint(0, 18)
	while dummy_board[y1][x1] != 0:
		x1 = random.randint(0, 18)
		y1 = random.randint(0, 18)

	dummy_board[y1][x1] = dummy_home
		
	x2 = random.randint(0, 18)
	y2 = random.randint(0, 18)
	while dummy_board[y2][x2] != 0:
		x2 = random.randint(0, 18)
		y2 = random.randint(0, 18)

	dummy_board[y2][x2] = dummy_home
	
	return create_payload(x1, 19 - y1, x2, 19 - y2)


def input_away_move(coor):
	x1 = ord(coor[0]) - 65
	y1 = 19 - int(coor[1:3])
	x2 = ord(coor[4]) - 65
	y2 = 19 - int (coor[5:7])

	if x1 > 8:
		x1 = x1 - 1
	if x2 > 8:
		x2 = x2 - 1

	if x2 < 0 or x2 > 18 or y2 < 0 or y2 > 18 or x1 < 0 or x1 > 18 or y1 < 0 or y1 > 18:
		print("ERROR: input out of bound in " + coor)
		exit(1)

	dummy_board[y1][x1] = dummy_away
	dummy_board[y2][x2] = dummy_away
	#return [x1, y1, x2, y2]


def main():
	global dummy_home, dummy_away, dummy_board
	ip = input("input ip: ")
	port = int(input("input port number: "))
	dummy_home = int(input("input 1 for black, 2 for white: "))
	mode = input("input \"user\" for activating user mode: ")

	if dummy_home == 1:
		dummy_away = 2
	elif dummy_home == 2:
		dummy_away = 1
	red_stones = lets_connect_six.connect('127.0.0.1', port, dummy_home)
	
	looper = red_stones.count(':') + 1
	for i in range(looper):
		chr_pos = 4 * i
		num_pos = 4 * i + 1
		x = ord(red_stones[chr_pos]) - 65
		if x > 8:
			x = x - 1
		y = 19 - int(red_stones[num_pos:num_pos+2])
		dummy_board[y][x] = 3

	if mode == "user":
		while 1:
			coor = input("input coor: ")
			try:
				away_move = lets_connect_six.draw_and_read(coor.replace(" ", ""))
			except lets_connect_six.InputError:
				print("this is how you handle exception")
				exit(1)
			if away_move == "K10":
				dummy_board[9][9] = dummy_away
			else:
				input_away_move(away_move)
				for row in dummy_board:
					print(row)
	else:
		if dummy_home == 1:
			dummy_board[9][9] = dummy_home
			away_move = lets_connect_six.draw_and_read("K10")
			input_away_move(away_move)
		else:
			lets_connect_six.draw_and_read("")
			dummy_board[9][9] = dummy_away
		while 1:
			away_move = lets_connect_six.draw_and_read(make_move())
			input_away_move(away_move)
	
if __name__ == "__main__":
	main()
