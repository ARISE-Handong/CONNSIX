# CONNSIX
## 개요
- 2021 한동SW페스티벌 - 인공지능 육목대회를 위해 개발한 API, 1인용 디버깅용 게임 모드.  
## 육목 게임 소개
- 육목은 타이완 우이천 교수가 오목을 개량해서 만든 2인용 보드게임입니다. 2006년 Computer Olympiad에 공식 종목으로 채택된 이후로는 게임 인공지능을 개발하는 프로그래밍 경연의 주제로 사용되고 있습니다. 우리나라에서는 삼성전자 DS부문에서 육목 SW 알고리즘 대회가 개최된 적이 있습니다.  
## 한동SW페스티벌 - 인공지능 육목대회 경기 규칙
- 본 대회를 위해 개발한 육목 게임 서버인 CONNSIX 프레임워크를 사용함.  
- 팀별 프로그램은 CONNSIX가 제공하는 API를 통해 게임 서버에 입장, 대국을 진행해야 함(착수, 상대방 수 받아오기 가능). API로는 C/C++, Java, Python 3 가 제공됨.  
- 팀별 프로그램 설치된 컴퓨터 1대와 선수 1명 출전.  
- 흑백은 경기직전 결정.  
- 게임의 다양성을 높이기 위해 심판이 게임 시작 직전에 0~5개의 착수금지점(적돌)을 임의로 배치함.  
- 흑 돌 하나가 정중앙에 착수한 상태에서, 백흑이 교대로 한번에 2개씩 바둑돌을 착수해야 하며, 착수는 30초 이내에 해야 함.  
- 6개 이상의 돌이 같은 색으로 일렬로 만드는 팀이 승리.  
- 잘못된 위치에 착수하는 경우, 몰수패.  
- 비길 경우, 해당 게임은 무효이며, 적돌을 변경하여 다시 시작.  
- 3x3 과 같은 특별한 금지 없음.  

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


