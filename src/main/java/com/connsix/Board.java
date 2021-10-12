package com.connsix;

class Board {
	Board(){
		count = 0;
		redStoneCount = 0;
		Gui gui = new Gui(this);
		this.g = gui;
	
	}
	public int[][] board = new int[20][20];
	private String redStones = ""; 
	public int color; 
	public int port;
	private int redStoneCount;
	private volatile int turn;
	private volatile int count;
	public Gui g;
	public String result;
	
	private int[] xd = {0,1,-1,1};
	private int[] yd = {1,0,1,1};
	public volatile  int[] point = {-1,0,0,0};
	public volatile int gameStart = 0;
	private volatile int gameEnd = 0;
	private int win = 1, notWin = 2, tie =3 ,notTie=4;

	public int getGameStart(){
		return gameStart;
	}
	public int getPort(){
		return port;
	}
	
	public void setGameEnd(int value){
		gameEnd = value;
	}
	public int getColor(){
		return color;
	}

	public int getGameEnd(){
		return gameEnd;
	}

   
	public void setPoint(int x, int y) {
		point[(count) * 2] = x;
		point[(count) * 2 + 1] = y;
	}

	private void deleteRedStone(int x , int y){
		String redString="";
		board[y][x]=0;
		if(x > 7)
			x += 1;
		y = 19 - y;
		char alphabet = (char)(65 + x);
		redString +=String.valueOf(alphabet);
		if(y < 10){
			redString += Integer.toString(0);
		}
		redString +=  Integer.toString(y);

		redStones = redStones.replaceAll(redString + ":", "");
		redStones = redStones.replaceAll(":" + redString, "");
		redStones = redStones.replaceAll(redString, "");
		redStoneCount -=1;
	
	}
	
	private boolean checkValid(int x, int y){
		if(x < 0 || x > 18){
                if(turn == 1)
				    g.printLog("[ERROR] BADCOORD : alphabet is incorrect");
        		return false;
        	}
        	if(y < 0 || y > 18){
            	if(turn == 1)
				    g.printLog("[ERROR] BADCOORD : number is incorrect");
                return false;
        	}
        	if(board[18 - y][x] != 0){
            	if(turn == 1)
				    g.printLog("[ERROR] NOTEMPTY : invalid point");
        		return false;
        	}
        	return true;	
	}
		
	public int checkTie(){
		for(int i = 0; i< 19; i++){
			for(int j = 0; j< 19; j++){
				if(board[i][j] == 0)
					return notTie;
			}
		}
		return tie;
	}

	public int checkWin(int x, int y){
		if(checkTie() == tie){
            gameEnd = 2;
			g.printLog("Tie. No place to draw a stone");  
			return tie;
		}
		int isWin[] = new int[4];
		for(int i = 0; i < 4; i++) {
			isWin[i] = search(xd[i], yd[i], x, y);
			if(isWin[i] >= 6) {
				gameEnd = 1;
				return win ;
			}
		}
		return notWin;
	}

	public int search(int xd, int yd, int x, int y) {
		int line = 1, value = board[y][x];
		int tmpx = x,tmpy = y;

		for(int i = 0; i < 2; i++) {
			while(true) {
				tmpx += xd;
				tmpy += yd;
				if(tmpx >= 0 && tmpx < 19 && tmpy >= 0 && tmpy < 19) {
					if(board[tmpy][tmpx] == value) {
						line++;
					}
					else {
							break;
					}
				}
				else {
					break;
				}
			}
			tmpx = x; tmpy = y;
			xd =- xd;
			yd =- yd;
		}
		return line;
	}
		
	public void clickEvent(int x, int y){
		if(redStoneClickEvent(x, y))
			return;

		if(gameEnd != 0 || turn == 0 || checkValid(x, 18 - y) == false   ){
			return ;
		}
		if(board[9][9] == 0 && !(x == 9 &&  y == 9)){
			return;	
		}
		setPoint(x, 18 - y);
		board[y][x] = color;
		g.repaint();
        
		if (checkWin(x, y) == win){
			g.printLog("Single player Win Game end");
			result = "Single player Win Game end";
			gameEnd = 1;
			return ;
		}

		if( x == 9 && y == 9 )
			count = 2;
		else
            count = count + 1;
					
	}

	private boolean redStoneClickEvent(int x, int y){
		if(x > 18 || x < 0 || y > 18 || y < 0){
			return false;
		}
		if(gameStart == 0){
			if(board[y][x] == -1){
				deleteRedStone(x,y);
			}
			else if(redStoneCount < 5 && storeRedStones(x, 18 - y)){
				redStoneCount += 1;
				redStonesString(x, y);
			}
			else{
				return false;
			}
			g.repaint();
			return  true ;
		}	
		return false;
	}
	public void updateBoard(int x, int y, int color){
		if(gameEnd != 0) {
			return ;
		}

		if(checkValid(x, y) == false) {
			gameEnd = 1;
            g.printLog("Single player Server Win! Game end");
            result = "Single player Server Win! Game end";
			return ;
		}

		setPoint(x, y);
		if( x == 9 && 18- y == 9 ){ 
			count = 2;
        	} 
		else {
			count = count + 1;
		}
		board[18 - y][x] = color;

		if(count == 2) {
			g.repaint();
    	}
		if(checkWin(x, 18 - y) == win) {
			gameEnd = 1;
            g.printLog("Client Win! Game end");
            result = "Client Win! Game end";
			return;
		}
		if(count == 2) {
			count = 0;
		}		
	}

	public void setColor(int color){
		this.color = color;	
	}
	
	public void setTurn(int turn){
		this.turn = turn;
	}
	
	public void setCount(int count){
		this.count = count;
	}

	public int getCount() {
		return this.count;
	}

	public String stoneGenerator() {
		String stones = "";
		int number = 0 ;
		char alphabet = 0;
		
        if(point[0] == 9 && point[1] == 9){
            	stones = "K10";
            	return stones;
        }

		for( int i = 0 ; i < 2 ; i++ ){
			if( point[2*i] < 8 ){
				alphabet = (char) (point[2*i] + 65);        
			} else {
				alphabet = (char) (point[2*i] + 66);
			}
			
			number = point[2*i+1] + 1;
			
			stones = stones + String.valueOf(alphabet);
			if(number < 10){
				stones = stones + "0";
			}
			stones = stones + Integer.toString(number);
			if( i == 0 ) 
				stones = stones + ":";
		}
		return stones;
	}

	private void resetBoard(){
		for(int i = 0; i < 19; i++){
			for(int j = 0; j < 19; j++){
				board[i][j] = 0;
				}
		}
	}

	public void redStoneGenerater(int redStoneCount){
		int x, y, storedX, storedY;
		resetBoard();
		this.redStoneCount = redStoneCount;
		redStones = "";
		for(int i = 0; i < redStoneCount; i++){
			while(true) {
				x = (int)((Math.random() * 20));
				if(x != 8) {
					break;
				}
			}
			y = (int)((Math.random() * 19) + 1);
			char alphabet = (char)(65 + x);
			if (x > 8) {
				storedX = x-1;
			}
			else {
				storedX = x;
			}
			storedY = y - 1;
			if(storeRedStones(storedX, storedY)){
				if(i != 0) {
					redStones = redStones + ":";
				}
				redStones = redStones+String.valueOf(alphabet);
				if(y < 10){
					redStones = redStones + Integer.toString(0);
				}
				redStones = redStones + Integer.toString(y);
			}
			else {
				System.out.println("" + alphabet + ":" + y);
				i--;
			}
		}
	}
	private void redStonesString(int x, int y){
		if(x > 7)
			x += 1;
		y = 19 - y;
		char alphabet = (char)(65 + x);
		if(redStoneCount > 1){
			redStones = redStones + ":";
		}
		else{
			redStones = "";
		}
		redStones = redStones+String.valueOf(alphabet);
		if(y < 10){
			redStones = redStones + Integer.toString(0);
		}
		redStones = redStones + Integer.toString(y);

	}

	private boolean storeRedStones(int x, int y){
		if(x == 9 && y == 9){
			return false;
		}
		if(this.board[18 - y][x] != -1){
			board[18 - y][x] = -1;
			return true;
		}
		else{
			return false;
		}
	}

	public String getRedStones(){
		return redStones;
	}
}
