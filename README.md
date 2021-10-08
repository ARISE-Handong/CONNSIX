# C API for CONNSIX
##	CONNSIX notation:
- The format of single notation is [column][row].

- Strict notation:  
			Columns are notated with alphabets A ~ T excluding I.  
			Rows are notated with two-digit integers 01 ~ 19.  
			Ex) K10, A01, T11, J04  
   
- Generous notation:  
			Columns are notated with case insensitive alphabets a ~ t excluding i.  
			Rows are notated with one or two digit integers 1 ~ 19.  
			Ex) k10, a1, T11, j04, C9  
			  
- Invalid notation:  
			When a column is not between A ~ H and J ~ T.  
			When a row is not between 1 ~ 19.  
			When a row is more thatn 2 digits.  
			Ex)	T20, i09, U01, b003  
  
- Multiple notation:  
			Single notations are concatenated with ':' as a delimiter  
			Ex) A10:T19  

## Makefile
- make: Builds the connsix library and compiles the dummy_ai.
- make connsix: Builds the connsix library.
- make dummy_ai: Compiles the dummy_ai.
- make run: Runs the dummy_ai executable file.
- make clean: Removes all generated files.

## dummy_ai
- An example program on how to use the connsix library.
- It randomly generates two stones to draw and reads game state from the CONNSIX server.
- It does not check whether it draws a valid stone.

