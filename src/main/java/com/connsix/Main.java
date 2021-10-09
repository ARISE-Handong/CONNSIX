package com.connsix;
class Main {
	public static void main(String[] args){
		Server server = new Server();
		server.connect();		
		server.sendRedStones();	
		server.start();
	}
}
