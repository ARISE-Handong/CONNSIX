# C API for CONNSIX
##	CONNSIX notation:  CONNSIX 기보
- The format of single notation is [column][row].  
  하나의 좌표는 [행][줄]로 단수 표기된다.

- Strict notation(기본 좌표 표기):  
			Columns are notated with uppercase alphabets A ~ T excluding I.  
			행은 'I'를 제외한 대문자 알파벳 A ~ T로 표기한다.   
			Rows are notated with two-digit integers 01 ~ 19.  
			줄은 두 자리 정수 01 ~ 19로 표기한다.  
			Ex) K10, A01, T11, J04  
   
- Generous notation():  
			Columns are notated with case insensitive alphabets a ~ t excluding i.  
			행은 대소문자 구분 없이 'i'를 제외한 알파벳 a ~ t로 표기한다.  
			Rows are notated with one or two digit integers 1 ~ 19.  
			줄은 한 자리 또는 두자리 정수 1 ~ 19로 표기한다.  
			Ex) k10, a1, T11, j04, C9  
			  
- Invalid notation(무효한 표기):  
			When a column is not between A ~ H and J ~ T.  
			행이 A ~ H 또는 J ~ T 사이가 아닌 경우.  
			When a row is not between 1 ~ 19.  
			줄이 1 ~ 19 사이가 아닌 경우.  
			When a row is more than 2 digits.  
			줄이 세 자리 수 이상인 경우
			Ex)	T20, i09, U01, b003  
  
- Multiple notation(복수 좌표 표기):  
			Single notations are concatenated with ':' as a delimiter.  
			단수 표기를 ':'를 구분자를 사용하여 결합한다.  
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

