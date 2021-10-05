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
		int first = (int) (Math.random() * 19);
		char letter = (char) (first + 'A');
		if (first >= 8) {
			letter = (char) (first + 'A' + 1);
		}

		int second = (int) (Math.random() * 19) + 1;
		String num = "";
		if (second > 9) {
			num = Integer.toString(second);
		} else {
			num = "0" + Integer.toString(second);
		}

		String result = Character.toString(letter) + num;

//    	System.out.println("[oneHome] result: " + result);

		return result;
	}
	
	// commandline arguments: ip port color
	public static void main(String[] args) throws Exception {
		Dummy dummy = new Dummy();
		dummy.init();

		System.out.println("args: " + args.length);
		if (args.length != 3) {
			System.err.println("");
		}

		int port = Integer.parseInt(args[1]);
		ConnectSix conSix = new ConnectSix(args[0], port, args[2]);
		System.out.println("redStones: " + conSix.redStones);

		if (args[2].toLowerCase().compareTo("black") == 0) {
			System.out.println("I am Black");
			String first = conSix.drawAndRead("K10");
		} else if (args[2].toLowerCase().compareTo("white") == 0) {
			System.out.println("I am White");
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

			System.out.println("draw: " + draw);

			String wait = conSix.drawAndRead(draw);
			System.out.println("wait: " + wait);
		}

	}
}
