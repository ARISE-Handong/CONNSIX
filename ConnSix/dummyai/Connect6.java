
public class Connect6 {

	static int ROW = 19;
	static int COL = 19;
	
	static int Empty = 0;
	static int red = 3;
	static int Ai = DummyAI.getAIColor();
	static int opponent = DummyAI.getPlayerColor();	
	
	static int[] dx = {1, 1, 0, -1};
	static int[] dy = {0, 1, 1, 1};
	
	private static int[][] CopyBoard(int [][] originBoard) {
		int[][] board = new int[ROW][COL];
		
		for(int x = 0; x < ROW; x++) {
			for(int y = 0; y < COL; y++) {
				board[x][y] = originBoard[x][y];
			}
		}
		
		return board;
	}
	
	private static void printBoard(int [][] board) {
		for(int y = 0; y < COL; y++) {
			for(int x = 0; x < ROW; x++) {
				System.out.printf("[%3d]", board[x][y]);
			}
			System.out.println();
		}
	}
	
	private static String Result(Stone[] stones) {		
		return stones[0].getPosition() + ":" + stones[1].getPosition();
	}
	
	private static Boolean IsOutOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= ROW || y >= COL;
	}
	
	private static Boolean isImPossibleConn7(int [][] board, int d, int x, int y, int player) {
		return !((IsOutOfBounds(x - dx[d], y - dy[d]) || board[x - dx[d]][y - dy[d]] != player) && (IsOutOfBounds(x + 6 * dx[d], y + 6 * dy[d]) || board[x + 6 * dx[d]][y + 6 * dy[d]] != player));
	}

	// integer 형태의 이상적 좌표를 형식에 맞춘 String으로 바꿔 리턴(다음에 놓을 그거임. 스톤 하나하나 기준.)
	public static String returnStringCoor(ConnectSix consix) {
		Stone[] stones = new Stone[2];
		int[][] playBoard = new int[ROW][COL];

		for (int Y = 0; Y < COL; Y++) {
			System.out.printf("%2d ", COL - Y);
			for (int X = 0; X < ROW; X++) {
				String stone = String.format("%c%02d", (char) ((X < 8) ? (X + 'A') : (X + 'A' + 1)), COL - Y);
				String temp = consix.getStoneAt(stone);
				if(temp.equals("EMPTY") == true)
					playBoard[X][Y] = 0;
				else if(temp.equals("WHITE") == true)
					playBoard[X][Y] = 2;
				else if(temp.equals("BLACK") == true)
					playBoard[X][Y] = 1;
				else if(temp.equals("RED") == true)
					playBoard[X][Y] = 3;
				
				System.out.printf("[%3d]", playBoard[X][Y]);
			}
			System.out.println("");
		}
		
		stones = isPossibleConn6(CopyBoard(playBoard), Ai);
		if(stones != null)
			return Result(stones);
		
		stones = isPossibleConn6(CopyBoard(playBoard), opponent);
		if(stones != null)
			return Result(stones);
		
		
		System.out.println("No Stones");
		
		stones = new Stone[2];
		
		for(int i = 0; i < 2; i++)
			stones[i] = new Stone();
		
		for(int i = 0; i < 2; i++) {
			do {
				stones[i].setStone((int) (Math.random() * 19), (int) (Math.random() * 19));			
			} while(consix.getStoneAt(stones[i].getPosition()).equals("EMPTY") != true);
		}
	


		return Result(stones);

	}
	
	private static Stone FindOneStone(int [][] board, int player, int d, int x, int y) {
		Stone stone = new Stone();
		for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (board[x][y] != Empty || (i == x - dx[d] && j == y - dy[d]) || (i == x + 6 * dx[d] && j == y + dx[d]))
                    continue;

                stone.setStone(i, j);
                return stone;
            }
        }
		
		return stone;
	}
	
	private static Stone[] isPossibleConn6(int [][] board, int player) {
		System.out.println("player = " + player);
		Stone[] stones = new Stone[2];
		for(int i = 0; i < 2; i++)
			stones[i] = new Stone();
		
		for(int d = 0; d < 4; d++) {
			for(int y = 0; y < COL - 6; y++) {
				for(int x = 0; x < ROW - 6; x++) {
					int playerStone = 0;
					
					if(IsOutOfBounds(x + 5 * dx[d], y + 5 * dy[d]))
						continue;
					
					for(int i = 0; i < 6; i++) {
						if(y == 9 && x == 10)
							System.out.printf("[%d] ", board[x + i * dx[d]][y + i * dy[d]]);
						
						if(board[x + i * dx[d]][y + i * dy[d]] == player)
							playerStone++;
						else if(board[x + i * dx[d]][y + i * dy[d]] != Empty) {
							playerStone = 0;
							if(y == 9 && x == 10) {
								System.out.println("stone reset " + board[x + i * dx[d]][y + i * dy[d]]);
							}
								
							break;
						}
					}
					if(y == 9 && x == 10)
						System.out.println("dir = " + d);
					
						
					if(playerStone >= 4 && !isImPossibleConn7(board, d, x, y, player)) {
						System.out.println("Find " + x + " " + y + " color = " + player);
						int putcnt = 0;
						for (int i = 0; i < 6; i++) {
	                        if (board[x + i*dx[d]][y + i*dy[d]] == Empty) {
	                        	board[x + i*dx[d]][y + i*dy[d]] = player;
	                        	stones[putcnt++].setStone(x + i*dx[d], y + i*dy[d]);
	                        }
	                    }
						
						if(putcnt == 2)
							return stones;
						
						stones[putcnt] = FindOneStone(board, player, d, x, y);
						
						if(stones[putcnt].x == -1) {
							System.out.println("no more");
							stones[0].x = -1;
						}
					}
				}
			}
		}
		
		return null;
	}

}
