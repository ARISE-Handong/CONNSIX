# C API for CONNSIX
##	CONNSIX notation(좌표 표기 방법)
- The format of single notation is [column][row].  
  하나의 좌표는 [행][줄]로 단수 표기한다.

- Strict notation(좌표 기본 표기):  
			Columns are notated with uppercase alphabets A ~ T excluding I.  
			행은 'I'를 제외한 대문자 알파벳 A ~ T로 표기한다.   
			Rows are notated with two-digit integers 01 ~ 19.  
			줄은 두 자리 정수 01 ~ 19로 표기한다.  
			Ex) K10, A01, T11, J04  
   
- Extended notation(좌표 확장 표기):  
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
			Ex) T20, i09, U01, b003  
  
- Multiple notation(복수 좌표 표기):  
			Single notations are concatenated with ':' as a delimiter.  
			단수 표기를 ':'를 구분자를 사용하여 표기한다.  
			Ex) A10:T19  
* The following image shows where the coordinate is mapped to the board. Note that I or i does not exist on the board.
<img width="400" alt="connsix_board" src="https://user-images.githubusercontent.com/36878832/136660098-0283f97a-a7ca-466e-bcb2-dd87aa4bdb24.png">  

## Makefile
- make: Builds the connsix library and compiles the dummy_ai.
- make connsix: Builds the connsix library.
- make dummy_ai: Compiles the dummy_ai.
- make run: Runs the dummy_ai executable file.
- make clean: Removes all generated files.

## dummy_ai
- An example program on how to use the connsix library.  
connsix 라이브러리를 사용하는 예제 프로그램입니다.  
- It randomly generates two stones to draw and reads opponent's stone from the CONNSIX server.  
랜덤으로 2수를 CONNSIX 서버로 전송하고 상대방의 수를 읽는다.  
- It does not check whether it draws a valid stone. Thus it may attempt to send stones that are out of bounds or draw a stone on a non-emmpty position.  
유효한 수를 두는지 확인하지 않는다. 즉, 좌표 범위를 벗어나거나 빈 좌표가 아닌 곳에 수를 둘 수도 있다

