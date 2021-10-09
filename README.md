# CONNSIX

# About CONNSIX package
	* CONNIX is used for establish connection between connsix server and the process. 
	* The process must call the lets_connect() function first to establish the connection, then
	use the draw_and_read() to communicate with the server. 
	* The package must exist as a directory along side with the source code of the program in 
	order to import. Note that the example code in current directory structure will not run,
	one must put them into the same directory.


# About [coordinate]
	* A [coordinate] is consisted with a character followed by a number. The character should
	be an alphabet from 'A' to 'T' excluding 'I'. Both upper and lowercase are accepted across
	all functions.
	* The number should be in range between 1 and 19, both 1 and 19 are accepted. The number 
	can be written as 1 and 2 digits. ex) 1 or 01 are accepted, however 001 is not.
	Following are some example of a [coordinate]:
		F09
		F9
		f09
		f9
	* The following image shows where the coordinate is mapped to the board. Note that I or i does 
	not exist on the board.
	![the board](/image/connsix_board.png)

# About dummy_ai
	* It is only an example of using the provided functions in the package.
	* It randomly generates two stones and send to the server. It may send invalid coordinate to
	the server.
