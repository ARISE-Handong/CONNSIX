# JAVA API for CONNSIX

## About
This API is for Connect Six programs developed with the Java programming language. Its functions connect and communicate with the single mode server.
<br>
이 API는 자바 프로그래밍 언어로 개발한 육목 AI를 프로그램들을 위한 API입니다. 모든 method들은 싱글 모드 서버와 연결하고 소통하기 위한 기능들이 구현되어있습니다.

## Methods
* public ConnectSix(String ip, int port, String color)
* public String letsConnect(String ip, int port, String color)
* public String drawAndRead(String draw)
* public String query(String position)
<br>
<b>Please refer to the javadoc for specific information about the functions.</b>
<br>
<b>각 method에 대한 구체적인 설명은 javadoc을 참조하시기 바랍니다.</b>
<br>

## Coordinate System
The coordinate is consisted with an alphabet character and a number. The columns are notated with 'A' to 'T' with 'I' not included. The rows are notated with 1 to 19. The one digit numbers may or may not have a leading 0. To express more than one coordinates, the coordinates should be separated with ':'.
<br>
바둑판의 좌표는 alphabet character 하나와 숫자로 표기합니다. 바둑판의 세로줄은 alphabet 'A' 부터 'T' 로 표기하고 이때 'I'는 포함되지 않습니다. 가로줄은 숫자 1 부터 19로 표기하며 10 이하의 숫자들은 십의 자리를 0으로 표기해도 안해도됩니다. 하나의 좌표는 이 두 정보를 합친 String으로 표현해야합니다. 여러 좌표들을 표기하기 위해서는 좌표들 사이에 ":"를 넣어 하나의 String으로 만들어야합니다.
* Ex) "A01", "A1", "B03", "B3", "J12", "A01:E13", "E11:J18:K10:T19"
<br>
Strict notation: coorinate with leading 0.
* Ex) "A01", "C04"
<br>
Extended notation: coorinate without leading 0.
* Ex) "A1", "C4"
<br>
Below is an image that explains the coordinate system.
<br>
밑의 이미지는 앞서 설명한 좌표 시스템을 나타낸 것입니다.
<br>
<img src="./ConnSix/image/coordinate_system.png" alt="coordinate system" width="500"/>

## Dummy AI
There is an example that randomly generates 2 coordinates and sends to the single mkode server. Due to the fact that it just generates coordinates, it may send invalid coordinates and end the game.
<br>
'dummyAi'라는 폴더 안에는 이 API를 사용하는 예시 프로그램이 있습니다. 이 프로그램은 무작위로 두개의 좌표를 생성해내 API내의 method들을 이용해 싱글 모드 서버에 보냅니다. 이때 무작위로 생성하는 두 좌표는 유효한 좌표인지를 확인하지 않기 때문에 유효하지 않은 좌표로 인해 싱글 모드 서버에서 게임을 종료할 수 있습니다.