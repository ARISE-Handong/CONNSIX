import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 
 * The ConnSix API connects to the single mode server and communicate with it.<br>
 * The rules of position notation are listed below.
 * <ol>
 * 	<li>The positions should be String types.</li>
 * <li>The columns are notated with 'A~T' with 'I' not included. 'A' starts from the left and 'T' notates the most right row.</li>
 * 	<li>The rows are notated with numbers 1~19. 1 notates the most bottom column and 19 notates the most top column.
 * 		Numbers with only one digit may or may not have a leading 0.</li>
 * 	<li>The position notations will be the combination of the row and column notation. That is, a character followed by a number. The number should be 2 digits.<br>  
 * 		Ex) "A01", E18"</li>
 * 	<li>To express more than one positions, the positions should be separated with the delimiter ":".<br>
 * 		Ex) "B11:K10", "C03:H15:N07"</li>
 * </ol>
 *
 */
public class ConnectSix {
	
	final static private int EMPTY = 0;
	final static private int BLACK = 1;
	final static private int WHITE = 2;
	final static private int RED = 3;

	private InputStream input;
	private OutputStream output;
	private Board board;
	private Socket socket = null;
	private int color = 0;
	private int opponent = 0;
	private boolean firstStone = true;
	
	/**
	 * The String type field that contains the positions of the red stones. The positions will follow the strict notation explained above.
	 */
	public String redStones = null;

	/**
	 * Creates an instance of the class ConnectSix and connects to the single mode server.
	 * When success, the field 'redStones' will contain the positions of the red stones.
	 * The positions of the red stones will follow the strick notation explained above.
	 * On connection failure, the constructor will throw ConnSixException.
	 * If there is no red stones to begin with, the field 'redStones' will contain the null value.
	 * The user must make a instance of the class using this constructor in order to use the single mode server and other functions.
	 * @param ip String type that contains the ip information. For example, "127.0.0.1".
	 * @param port Integer type that contains the port number information. For example, 8080.
	 * @param color String type that contains the color of the stone that the client will be using. For example, "White" or "Black".
	 * @throws ConnSixException Throws an exception that happens when the network connection fail.
	 * 			Connection failure can happen because of ip, port information error, underlying protocol error and IOException related to socket creation.
	 */
	public ConnectSix(String ip, int port, String color) throws ConnSixException {
		this.board = new Board();

		this.redStones = letsConnect(ip, port, color);
	}

	/**
	 * Connects to the single mode server and gets the red stones' positions from the single mode server.
	 * This function will be called from the constructor function.
	 * Therefore making an instance of this class will automatically connect to the single mode server by calling this function.
	 * @param ip String type that contains the ip information.
	 * @param port Integer type that contains the port number information.
	 * @param color String type that contains the stone color that the client will be using.
	 * @return The String type with the positions of the red stones. The positions will follow the strick notation explained above.
	 * @throws ConnSixException Throws an exception that happens when the network connection fail.
	 * 			Connection failure can happen because of ip, port information error, underlying protocol error and IOException related to socket creation.
	 */
	public String letsConnect(String ip, int port, String color) throws ConnSixException {
		
		try {
			socket = new Socket(ip, port);
			socket.setTcpNoDelay(true);
	
			output = socket.getOutputStream();
			input = socket.getInputStream();
		}
		catch (UnknownHostException e) {
			throw new ConnSixException("IP not determined");
		}
		catch (IllegalArgumentException e) {
			throw new ConnSixException("Invalid port values");
		}
		catch (SocketException e1) {
			throw new ConnSixException("Socket Exception");
		}
		catch (IOException e) {
			throw new ConnSixException("IOException");
		}

		if (color.toLowerCase().compareTo("white") == 0) {
			this.color = WHITE;
			this.opponent = BLACK;
		} else if (color.toLowerCase().compareTo("black") == 0) {
			this.color = BLACK;
			this.opponent = WHITE;
		} else {
			throw new ConnSixException("Incorrect stone color input. Input white or black.");
		}

		String redString = null;
		try {
			byte[] numBytes = new byte[4];
			input.read(numBytes, 0, 4);
			int byteSize = byteToInt(numBytes);
			if(byteSize == 0) {
				return null;
			}
			byte[] redBytes = new byte[byteSize];
			input.read(redBytes, 0, byteSize);
			redString = new String(redBytes);
		}
		catch (IOException e){
			throw new ConnSixException("IOException on reading red stones from single mode server.");
		}

		String[] redStones = redString.split(":");
		for (String stone : redStones) {
			this.board.updateBoard(stone, RED);
		}

		return redString;
	}

	/**
	 * Sends the position of the user's next move to the single mode server.
	 * The first move of black must be "K10" and the first move of white must be "", an empty String.
	 * If the user sends an invalid coordinate, an error message will be sent to the single mode server.
	 * <ol>
	 * 	<li>"BADCOORD"<br>
	 * 		- The coordinate if out of bounds</li>
	 * 	<li>"NOTEMPTY"<br>
	 * 		- The position is already occupied by another stone.</li> 
	 * 	<li>"BADINPUT"<br>
	 * 		- The first move is not "K10" for black or "" for white.<br>
	 * 		- The moves other than the first move don't hold two positions.<br>
	 * 		- Any other inputs that doesn't follow the position notation.
	 * 	</li>
	 * </ol>
	 * All positions will follow the position notation explained above.
	 * 
	 * @param draw The position where the user will put their stones. 
	 * @return When the game continues, the position of the opponent's move, expressed in strict notation, will be returned.<br> 
	 * 			When the game is over, the return value will be "WIN", "LOSE" or "EVEN".<br>
	 * @throws ConnSixException Throws an exception when communication with the single mode server failed.
	 */
	public String drawAndRead(String draw) throws ConnSixException {
		
		if (draw.toLowerCase().compareTo("") != 0) {
			drawStones(draw);
		}
		
		if(this.firstStone == true) {
			this.firstStone = false;
		}
		
		return readStones();
	}

	/**
	 * Returns the current state of the position.
	 * @param position The position of the state that the user is curious about.
	 * @return Returns can be "EMPTY", "WHITE", "BLACK" or "RED" according to the state of the position.<br>
	 * 			When the position does not follow the position notation, the function will return the null value.
	 */
	public String getStoneAt(String position) {
		int colorInt = this.board.getColor(getValid(position));
		String returnValue = null;
		switch (colorInt) {
			case EMPTY:
				returnValue = "EMPTY";
				break;
			case WHITE:
				returnValue = "WHITE";
				break;
			case BLACK:
				returnValue = "BLACK";
				break;
			case RED:
				returnValue = "RED";
				break;
		}
		return returnValue;

	}

	private int byteToInt(byte[] bytes) {
		return ((bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8)
				| ((bytes[0] & 0xFF) << 0);
	}

	private byte[] intToByte(int intValue) {
		byte[] byteArray = new byte[4];
		byteArray[3] = (byte) (intValue >> 24);
		byteArray[2] = (byte) (intValue >> 16);
		byteArray[1] = (byte) (intValue >> 8);
		byteArray[0] = (byte) (intValue);
		return byteArray;
	}

	private void drawStones(String draw) throws ConnSixException {
		String drawValid = getValid(draw);
		String error = null;

		String[] stones = drawValid.split(":");

		if (this.firstStone == true && this.color == BLACK) {
			if (stones.length != 1 && stones[0].toLowerCase().compareTo("k10") != 0) {
				error = "BADINPUT";
			}
		}
		else if (this.firstStone == true && this.color == WHITE) {
			if(draw.compareTo("") != 0) {
				error = "BADINPUT";
			}
		}
		else if (stones.length != 2) {
			error = "BADINPUT";
		}

		for (String stone : stones) {
			String err = this.board.updateBoard(stone, this.color);
			if (err != null) {
				error = err;
				break;
			}
		}

		String message = "";
		if (error != null) {
			message = error + "$" + draw;
		} else {
			message = drawValid;
		}

		byte[] send = message.getBytes();
		int messageLength = send.length;

		try {
			output.write(intToByte(messageLength));
			output.write(send);
			output.flush();
		}
		catch (IOException e) {
			throw new ConnSixException("IOException on sending message to single mode server.");
		}
	}

	private String readStones() throws ConnSixException {
		byte[] numBytes = new byte[4];
		String result = null;

		try {
			input.read(numBytes, 0, 4);
			int byteSize = byteToInt(numBytes);
			byte[] stoneBytes = new byte[byteSize];
			input.read(stoneBytes, 0, byteSize);
			result = new String(stoneBytes);
		}
		catch (IOException e) {
			throw new ConnSixException("IOException on reading stones from single mode server.");
		}
		
		if(result.compareTo("WIN") == 0 || result.compareTo("LOSE") == 0 || result.compareTo("EVEN") == 0) {
			return result;
		}

		String[] stones = result.split(":");
		for (String stone : stones) {
			this.board.updateBoard(stone, this.opponent);
		}

		return result;
	}

	private String getValid(String input) {
		String[] stones = input.split(":");
		for (int i = 0; i < stones.length; i++) {
			stones[i] = stones[i].toUpperCase();
			if (stones[i].length() == 2) {
				stones[i] = stones[i].charAt(0) + "0" + stones[i].charAt(1);
			}
		}

		return String.join(":", stones);
	}
	
	private class Board {
		private int[][] board = new int[19][19];
		
		Board() {
			for(int i = 0; i < 19; i++) {
				for (int j = 0; j < 19; j++) {
					this.board[i][j] = EMPTY;
				}
			}
		}
		
		String updateBoard(String stone, int color) {
			
			if (stone.length() != 3) {
				return "BADINPUT";
			}

			String lowerCaseStone = stone.toLowerCase();
			int letter = lowerCaseStone.charAt(0);
			int tenth = lowerCaseStone.charAt(1);
			int units = lowerCaseStone.charAt(2);

			int i = letter - 'a';
			int j = (tenth - '0') * 10 + (units - '0') - 1;

			if (i == 8) {
				return "BADCOORD";
			}

			if (i > 7) {
				i -= 1;
			}

			if (i < 0 || i > 18 || j < 0 || j > 18) {
				return "BADCOORD";
			}

			if (board[i][j] != EMPTY) {
				return "NOTEMPTY";
			}

			board[i][j] = color;
			return null;
		}
		
		int getColor(String stone) {

			if (stone.length() != 3) {
				return -1;
			}

			String lowerStone = stone.toLowerCase();
			int letter = lowerStone.charAt(0);
			int tenth = lowerStone.charAt(1);
			int units = lowerStone.charAt(2);

			int i = letter - 'a';
			int j = (tenth - '0') * 10 + (units - '0') - 1;

			if (i == 8) {
				return -1;
			}

			if (i > 7) {
				i -= 1;
			}

			if (i < 0 || i > 19) {
				return -1;
			}
			if (j < 0 || j > 19) {
				return -1;
			}

			return board[i][j];
		}
	}
}
