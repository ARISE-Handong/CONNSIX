package edu.handong.arise.dummy;

import edu.handong.arise.ConnectSix;

public class Dummy {

	// static numbers
	final static private int EMPTY = 0;
	final static private int BLACK = 1;
	final static private int WHITE = 2;
	final static private int RED = 3;

	int[][] board = new int[19][19];
	
	public void init() {
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				board[i][j] = EMPTY;
			}
		}
	}
	
	private String generateDraw() {

		String result = oneDraw() + ":" + oneDraw();

//    	System.out.println("[generateHome] result: " + result);

		return result;
	}

	private String oneDraw() {
		int first = -1, second = -1;
		
		while (true) {
			first = (int) (Math.random() * 19);
			second = (int) (Math.random() * 19) + 1;
			
			if(board[first][second] == EMPTY) {
				break;
			}
		}
		
		if (first >= 8) {
			first++;
		}
		char letter = (char) (first + 'A');
		
		String num = "";
		if (second > 9) {
			num = Integer.toString(second);
		} else {
			num = "0" + Integer.toString(second);
		}
		String result = Character.toString(letter) + num;

//    	System.out.println("[oneDraw] result: " + result);

		return result;
	}
	
	private void putStone(String position, int color) {
		String[] stones = position.split(":");
		for(String stone : stones) {
			stone = stone.toLowerCase();
			int i = stone.charAt(0);
			int j = (stone.charAt(1) - '0') * 10 + (stone.charAt(2) - '0') - 1;
			if(i > 'i') {
				i--;
			}
			i = i - 'a';
//			System.out.println("[putStone] " + i + " " + j);
			board[i][j] = color;
		}
	}
	// A B C D E F G H I J K  L  M  N  O  P  Q  R  S  T
	// 0 1 2 3 4 5 6 7   8 9 10 11 12 13 14 15 16 17 18
	
	// commandline arguments: ip port color
	public static void main(String[] args) throws Exception {
		Dummy dummy = new Dummy();
		dummy.init();
		int color = -1;
		int opponent = -1;

		System.out.println("args: " + args.length);
		if (args.length != 3) {
			System.err.println("");
		}

		int port = Integer.parseInt(args[1]);
		ConnectSix conSix = new ConnectSix(args[0], port, args[2]);
		System.out.println("redStones: " + conSix.redStones);

		if (args[2].toLowerCase().compareTo("black") == 0) {
			System.out.println("I am Black");
			color = BLACK;
			opponent = WHITE;
			dummy.putStone("K10", color);
			String first = conSix.drawAndRead("K10");
			dummy.putStone(first, color);
		} else if (args[2].toLowerCase().compareTo("white") == 0) {
			System.out.println("I am White");
			color = WHITE;
			opponent = BLACK;
			String first = conSix.drawAndRead("");
		} else {
			System.err.println("command line argument error");
		}

//		int ind = 0;
//		String posi = "a1:b03,a3:k12,K11:l8,l5:a13,k2:l21";
//		String[] location = posi.split(",");
		while (true) {
			String draw = dummy.generateDraw();
//			String draw = location[ind];
//			ind += 1;
			
			if(draw.compareTo("WIN") == 0 || draw.compareTo("LOSE") == 0) {
				System.out.println(draw + "!!");
				System.exit(0);
			}

			dummy.putStone(draw, color);
			System.out.println("draw: " + draw);

			String read = conSix.drawAndRead(draw);
			dummy.putStone(read, opponent);
			System.out.println("read: " + read);
		}

	}
}
