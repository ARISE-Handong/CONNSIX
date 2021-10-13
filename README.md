# PYTHON API for CONNSIX

## About CONNSIX package
* CONNIX is used for establish connection between connsix server and the process. 
* The process must call the lets_connect() function first to establish the connection, then use the draw_and_read() to communicate with the server. 
* The package must exist as a directory along side with the source code of the program in order to import. Note that the example code in current directory structure will not run, one must put them into the same directory.


## About [coordinate] notations
* A [coordinate] is consisted with a character followed by a number. The character should be an alphabet from `A` to `T` excluding `I`. The alphabet represents the column and the number represents the row. The following are some alias for the notations:

* Strict single notation:
	* Columns are notated with uppercase alphabets `A` ~ `T` excluding `I`. 
	* Rows are notated with two-digit integers `01` ~ `19`. 
	* EX) `K10`, `A01`, `T11`, `J04`

* Extended single notation:
	* Columns are notated with case insensitive alphabets `a` ~ `t` excluding `i`.
	* Rows are notated with one or two digit integers `1` ~ `19`.
	* Ex) `k10`, `a1`, `t11`, `j04`, `C9`
* Multiple notation:
	* Single notations are concatenated with `:` as a delimiter.
	* Ex) `A10:T19`
* Invalid notaion: 
	* When a column is not between `A` ~ `H` and `J` ~ `T`.
	* When a row is not between `1` ~ `19`.
	* When a row is more than 2 digits.
	* Ex) `T20`, `i09`, `U01`, `b003`

> The following image shows where the coordinate is mapped to the board. Note that I or i does not exist on the board.
<img width="400" alt="connsix_board" src="https://user-images.githubusercontent.com/36878832/136660098-0283f97a-a7ca-466e-bcb2-dd87aa4bdb24.png">


## About dummy_ai.py and input_ai.py
* They are only some examples reside under the `example` directory of using the provided functions in the package.
* dummy_ai.py randomly generates two stones and send to the server. It may send invalid coordinate to the server.
* input_ai.py receives input from the user and send it to the server. 


---

## CONNSIX 패키지에 대하여
* CONNSIX는 connsix 서버와 TCP 연결을 하기 위한 패키지다. 
* 서버에 연결을 시도하는 프로그램은 lets_connect()를 먼저 호출해서 연결한 뒤에 draw_and_read()를 통해 서버와 교신을 할 수 있다. 
* CONNSIX 패키지를 import하기 위해서 CONNSIX 패키지는 프로그램의 소스코드가 있는 디렉토리에 있어야한다. 현재 예시로 있는 dummy_ai.py와 input_ai.py는 CONNSIX와 같은 디랙토리에 있지 않기 때문에 이들의 위치를 변경한 뒤 프로그램을 실행해야한다. 

## 좌표 표기 방법
* 하나의 [coordinate]는 하나의 알파벳과 하나의 숫자로 이루어져 있다. 알파벳은 `A`부터 `T`까지이며 `I`는 제외된다. 하나의 [coordinate]에서의 알파벳은 행을 의미하고 숫자는 열을 의미한다. 이하는 좌표 표기의 명칭이다:

* 단수 좌표 기본 표기 (Strict single notation):
	* 행은 `I`를 제외한 대문자 알파벳 `A` ~ `T`로 표기한다.
	* 줄은 두 자리 정수 `01` ~ `19`로 표기한다.
	* Ex) `K10`, `A01`, `T11`, `J04`

* 단수 좌표 확장 표기 (Extended single notation):
	* 행은 대소문자 구분 없이 `i`를 제외한 알파벳 `a` ~ `t`로 표기한다.
	* 줄은 한 자리 또는 두자리 정수 `1` ~ `19`로 표기한다.
	* Ex) `k10`, `a1`, `T11`, `j04`, `C9`
* 복수 좌표 표기 (Multiple notation):
	* 단수 표기를 `:`를 구분자를 사용하여 표기한다.
	* Ex) `A10:T19`
* 무효한 표기 (Invalid notation):
	* 행이 `A` ~ `H` 또는 `J` ~ `T` 사이가 아닌 경우.
	* 줄이 `1` ~ `19` 사이가 아닌 경우.
	* 줄이 세 자리 수 이상인 경우 
	* Ex) `T20`, `i09`, `U01`, `b003`

> 위에 바둑판 사진에서 각 좌표가 어느 위치에 매핑되어 있는지 확인할 수 있다. 바둑판에 I가 없이 H 다음에 바로 J가 나오는 것을 확인할 수 있다. 

## dummy_ai.py과 input_ai.py
* `example` 디랙토리 아래에 있는 dummy_ai.py과 input_ai.py는 CONNSIX 패키지 사용법에 대한 예시다. 오로지 사용법의 대한 예시로 작성되었으며 그 이상의 기능을 하지 않는다.
* dummy_ai.py는 무작위로 좌표를 생성한 뒤 서버로 보낸다. 무작위로 생성한 좌표들이 무효한 좌표를 생성할 수 있다.
* input_ai.py는 사람 유저한테 입력값을 받아 서버와 통신한다. 
