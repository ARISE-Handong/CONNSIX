from CONNSIX import connsix
import random 

def make_move():
	return chr(random.randint(65, 84)) + str(random.randint(1, 19)) + ":" + chr(random.randint(65, 84)) + str(random.randint(1, 19))

def main():
	ip = input("input ip: ")
	port = int(input("input port number: "))
	dummy_home = int(input("input 1 for black, 2 for white: "))
	
	if dummy_home == 1:
		dummy_away = 2
	elif dummy_home == 2:
		dummy_away = 1

	red_stones = connsix.lets_connect(ip, port, dummy_home)
	if len(red_stones):	
		print("Received red stones from server: " + red_stones)

	while 1:
		user_in = input("input a coor: ")
		if user_in == "coor":
			while user_in != "quit":
				user_in = input("input a query (quit to quit): ")
				print(connsix.query(user_in))
		else:
			away_move = connsix.draw_and_read(user_in)
			print("Received away move from server: " + away_move)


if __name__ == "__main__":
	main()
