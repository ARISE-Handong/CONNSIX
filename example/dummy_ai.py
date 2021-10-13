'''
	The CONNSIX package should be located where this dummy_ai.py is located. 
	dummy_ai.py is an example to demonstrate the usage of CONNSIX package. Since 
	it is an example, dummy_ai.py may produce invalid input. Once it receives the
	return value from server, the message is printed to the console for checking 
	results. 

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

	if dummy_home == "BLACK":
		away_move = connsix.draw_and_read("K10")
		print("Received first away move from server: " + away_move)
	else:
		away_move = connsix.draw_and_read("")
		print("Received first away move from server: " + away_move)
	while 1:
		away_move = connsix.draw_and_read(make_move())
		print("Received away move from server: " + away_move)


if __name__ == "__main__":
	main()
