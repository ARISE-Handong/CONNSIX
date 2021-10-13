'''
	The CONNSIX package should be located where this input_ai.py is located. 
	input_ai.py is an example to show the server's reaction according to the
	human user's intended input. The human user must input a "" or just a plain
	enter as a white. As a black stone user, "K10" must be entered first. User
	can also input "coor" to call get_stone_at(), user's input will be handed in 
	to get_stone_at() until "quit" is entered. 
'''
from CONNSIX import connsix
import random 

def make_move():
	return chr(random.randint(65, 84)) + str(random.randint(1, 19)) + ":" + chr(random.randint(65, 84)) + str(random.randint(1, 19))

def main():
	ip = input("input ip: ")
	port = int(input("input port number: "))
	dummy_home = input("input BLACK or WHITE: ") 

	red_stones = connsix.lets_connect(ip, port, dummy_home)
	if len(red_stones):	
		print("Received red stones from server: " + red_stones)

	while 1:
		user_in = input("input a coor (enter \"coor\" to call get_stone_at()): ")
		if user_in == "coor":
			while user_in != "quit":
				user_in = input("input a position to query (\"quit\" to quit): ")
				print(connsix.get_stone_at(user_in))
		else:
			away_move = connsix.draw_and_read(user_in)
			print("Received from server: " + away_move)


if __name__ == "__main__":
	main()
