package com.connsix;
import java.io.*;
import java.net.*;
import java.net.Socket;
import java.net.InetAddress;


class Server {
	private int color, clientColor, port;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Board board;
	private ServerSocket serverSocket;

	
	Server(){
		board = new Board();
	}

	public void connect() {
		while(true){
		    if(board.getGameStart() == 1) 
			break;
		}
		this.getArgument();

		try {
			serverSocket = new ServerSocket(port);
			InetAddress ip = InetAddress.getLocalHost();
			board.g.printLog("Connect your AI");
			
			board.g.printLog("IP : " + ip.getHostAddress());
			board.g.printLog("Port : " + port );  
			if(board.color == 1){
				board.g.printLog("AI Color : Black");
			}
			else {
				board.g.printLog("AI Color : White");
			}	
			socket = serverSocket.accept();
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(30000);

			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch(SocketTimeoutException e){ 
			board.g.printLog("Socket Timeout " + e);
			gameEnd();
		} catch(ConnectException e){ 
			board.g.printLog("[ERROR] Couldn't get I/O for the connection " + e);
			gameEnd();
		} catch(SocketException e){
			board.g.printLog("Set tcp_nodelay" + e);
			gameEnd();
		} catch(IllegalArgumentException e){
			board.g.printLog(""+e);
			gameEnd();
		} catch(IOException e){
			board.g.printLog(""+e);
			gameEnd();
		}

		board.g.printLog("Connected!");
		board.g.printNewLine(1);
	}

	public void getArgument() {
		port = board.getPort();
		color = board.getColor();
		
		board.g.printLog("Setting Done");

		if(color == 1){
			clientColor = 2;
		}
		else {
			clientColor = 1;
		}
	}


	public void sendRedStones(){
		String redStones = board.getRedStones();
		int sizeOfRedStones = redStones.length();

		try {
			outputStream.write(Message.intToByte(sizeOfRedStones), 0, 4);
			outputStream.write(redStones.getBytes(), 0, sizeOfRedStones);

			if(redStones.equals("")){
				board.g.printLog("Sent redStones : No redstone selected");
			}
			else {
				board.g.printLog("Sent RedStones : "+redStones);
			}
		} catch (IOException e) {
			board.g.printLog(""+e);
			gameEnd();
		}
    }

	public void sendStones(){
		try {
			while(board.getCount() != 2) {	
				if(board.getGameEnd() == 1){
					sendResult("LOSE");
				}
                if(board.getGameEnd() == 2){
                    sendResult("TIE");
                }
			}
			String stones = board.stoneGenerator();
			int sizeOfStones = stones.length();
			outputStream.write(Message.intToByte(sizeOfStones), 0, 4);
			byte[] bytesOfStones = stones.getBytes();
			outputStream.write(bytesOfStones);
			board.setCount(0);
			board.g.printLog("Sent "+stones);
		} catch(SocketException e){ 
			board.g.printLog("Disconnected " + e);
			gameEnd();
		} catch (IOException e) {
			board.g.printLog(""+e);
			gameEnd();
		}
	}

	public void recvStones(){
		String stones = "";
		try{
			byte[] byteOfSize = new byte[4];
			inputStream.read(byteOfSize, 0, 4);
			int sizeOfStones = Message.byteToInt(byteOfSize);
			byte[] stonesByte = new byte[sizeOfStones];
			inputStream.read(stonesByte, 0, sizeOfStones);
			stones = new String(stonesByte);
			board.g.printLog("Recieved : " + stones);

		} catch (SocketTimeoutException e) {
			board.setGameEnd(1);
			board.g.printLog("[TIMEOUT] Single player Server Win!");
			sendResult("LOSE");
		} catch(SocketException e){ 
			board.g.printLog("Disconnected " + e);
			gameEnd();
		} catch (IOException e) {
			board.g.printLog(""+e);
			gameEnd();
		}

		stones = stones.toUpperCase();
		stones = stones.replace(" ","");

		if(stones.length() > 7) {
			board.g.printLog("Single player Server Win!");
			sendResult("LOSE");
		}

		int[] pointArray = Message.parseString(stones);
		for(int i = 0; i < 2; i++){
			board.updateBoard(pointArray[2 * i], pointArray[2 * i + 1], clientColor); 
			if(board.getGameEnd() == 1){
				board.g.repaint();
				sendResult("WIN");
			}
            if(board.getGameEnd() == 2){
                board.g.repaint();
                sendResult("TIE");
            }
			if(stones.equals("K10"))
				break;
		}
		

	}

	public void sendResult(String message) {
		try {
			int sizeOfMessage = message.length();
			outputStream.write(Message.intToByte(sizeOfMessage), 0, 4);
			byte[] bytesOfMessage= message.getBytes();
			outputStream.write(bytesOfMessage);
		} catch (IOException e) {
			board.g.printLog("" + e);
			gameEnd();
		}
		gameEnd();
	}

	public void start(){
		if(color == 1){ 
			board.setTurn(0);
			board.g.printNewLine(1);
			board.g.printLog("TURN : BLACK");
			recvStones();
		}
		else { 
			board.g.printNewLine(1);
			board.g.printLog("TURN : BLACK");
			board.setTurn(1);
			sendStones();
            board.setTurn(0);
			board.g.printNewLine(1);
			board.g.printLog("TURN : WHITE");
			recvStones();
		}

		while(true) {
			board.g.printNewLine(1);
			if(board.color == 1){
				board.g.printLog("TURN : WHITE");
			}
			else {
				board.g.printLog("TURN : BLACK");
			}

			board.setTurn(1);
			sendStones();
			board.setTurn(0);
			
			board.g.printNewLine(1);
			if(board.color == 1){
				board.g.printLog("TURN : BLACK");
			}
			else {
				board.g.printLog("TURN : WHITE");
			}
			recvStones();
		} 
	}

	private void gameEnd(){
		board.setGameEnd(1);
		try {
			serverSocket.close();	
		} catch (IOException e) {}
		while(true){

		}
	}
}
