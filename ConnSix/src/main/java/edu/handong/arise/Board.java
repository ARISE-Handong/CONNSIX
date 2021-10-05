package edu.handong.arise;

public class Board {

	// static numbers
	final static private int EMPTY = 0;
	final static private int BLACK = 1;
	final static private int WHITE = 2;
	final static private int RED = 3;

	private int[][] board = new int[19][19];

	Board() {
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				this.board[i][j] = EMPTY;
			}
		}
	}

	String putStone(String stone, int color) {
//		System.out.println("Color: " + color);

		// Invalid input
		if (stone.length() != 3) {
//			System.err.println("[putStone] Invalid input - String length");
			return "BADINPUT";
		}

		// parse stone location
		String lowerStone = stone.toLowerCase();
		int letter = lowerStone.charAt(0);
		int tenth = lowerStone.charAt(1);
		int units = lowerStone.charAt(2);

		// Uppercase & lowercase
		int i;
		i = letter - 'a';
		int j = (tenth - '0') * 10 + (units - '0') - 1;
//				System.out.println("index i: " + i + " j: " + j);

		// Invalid stone location input
		if (i == 8) {
//			System.err.println("[putStone] Invalid input - I");
			return "BADINPUT";
		}

		if (i > 7) {
			i -= 1;
		}

		if (i < 0 || i > 18) {
//			System.err.println("[putStone] Invalid input - i: " + i);
			return "BADCOORD";
		}
		if (j < 0 || j > 18) {
//			System.err.println("[putStone] Invalid input - j: " + j);
			return "BADCOORD";
		}

		if (board[i][j] != EMPTY) {
//			System.err.println("[putStone] occupied location");
			return "NOTEMPTY";
		}

		// update board
		board[i][j] = color;
		return null;
	}

	int getColor(String stone) {
		// invalid input
		if (stone.length() != 3) {
//			System.err.println("[getColor] Invalid input - String length");
			return -1;
		}

		// parse location
		String lowerStone = stone.toLowerCase();
		int letter = lowerStone.charAt(0);
		int tenth = lowerStone.charAt(1);
		int units = lowerStone.charAt(2);

		int i = letter - 'a';
		int j = (tenth - '0') * 10 + (units - '0') - 1;

		// invalid location input
		if (i == 8) {
//			System.err.println("[getColor] Invalid input - I");
			return -1;
		}

		if (i > 7) {
			i -= 1;
		}

		if (i < 0 || i > 19) {
//			System.err.println("[getColor] Invalid input - i: " + i);
			return -1;
		}
		if (j < 0 || j > 19) {
//			System.err.println("[getColor] Invalid input - j: " + j);
			return -1;
		}

		System.out.println("index i: " + i + " j: " + j);

		return board[i][j];
	}

//	void printBoard() {
//		System.out.println();
//		System.out.println("----------------------------------------------------------------------");
//		for (int i = 18; i >= 0; i--) {
//			System.out.print("\t\t\t\t");
//			for (int j = 0; j < 19; j++) {
//				System.out.print(board[j][i] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("----------------------------------------------------------------------");
//		System.out.println();
//	}
}
