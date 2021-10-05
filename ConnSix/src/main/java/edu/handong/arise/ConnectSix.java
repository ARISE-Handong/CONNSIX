package edu.handong.arise;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ConnectSix {

//	public static void main(String[] args) {
//		System.out.println("hi");
//	}

	// static numbers
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
	
	public String redStones = null;

	/**
	 * Creates an instance of the class ConnectSix and connects to the platform. When success, the field 'redStones' will contain the location of the red stones.
	 * @param ip String that contains the ip information. For example, "127.0.0.1".
	 * @param port Integer that contains the port number information. For example, 8080.
	 * @param color String that contains the stone color that the client will be using. For example, "White".
	 * @throws ConnSixException Throws an exception that happens when the network connection failed.
	 */
	public ConnectSix(String ip, int port, String color) throws ConnSixException {
		this.board = new Board();

		this.redStones = letsConnect(ip, port, color);
	}

	/**
	 * Connects to the platform and gets the red stones' locations from the platform
	 * @param ip String that contains the ip information
	 * @param port Integer that contains the port number information
	 * @param color String that contains the stone color that the client will be using.
	 * @return The String with the location of the red stones. The locations will be separated with the delimiter ':'. For example, "A01:K11"
	 * @throws ConnSixException Throws an exception that happens when the network connection failed.
	 */
	public String letsConnect(String ip, int port, String color) throws ConnSixException {
		
		try {
			socket = new Socket(ip, port);
			socket.setTcpNoDelay(true);
	
			output = socket.getOutputStream(); // to server
			input = socket.getInputStream(); // from server
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

		// get color
		if (color.toLowerCase().compareTo("white") == 0) {
			this.color = WHITE;
			this.opponent = BLACK;
		} else if (color.toLowerCase().compareTo("black") == 0) {
			this.color = BLACK;
			this.opponent = WHITE;
		} else {
			throw new ConnSixException("Incorrect stone color input. Input white or black.");
		}

		// get redstones from server
		String redString = null;
		try {
			byte[] numBytes = new byte[4];
			input.read(numBytes, 0, 4);
			int byteSize = byteToInt(numBytes);
			byte[] redBytes = new byte[byteSize];
			input.read(redBytes, 0, byteSize);
			redString = new String(redBytes);
		}
		catch (IOException e){
			throw new ConnSixException("IOException on reading red stones from platform.");
		}

		// update board
		String[] redStones = redString.split(":");
		for (String stone : redStones) {
			this.board.putStone(stone, RED);
		}

		return redString;
	}

	/**
	 * Sends the location of the user's next move to the platform and receives the location of the opponent's move.
	 * @param draw The location where the user will put their stone. The locations will be separated with the delimiter ':'. 
	 * @return The String that contains the location of the opponent's move. The locations will be separated with the delimiter ':'. If the game is over, the return String will contain "WIN" or "LOSE".
	 * @throws ConnSixException Throws an exception that happens when the network connection failed.
	 */
	public String drawAndRead(String draw) throws ConnSixException {
		if (draw.toLowerCase().compareTo("") != 0) {
			draw(draw);
		}

		String result = readStones();

		return result;
	}

	/**
	 * Returns the current state of the location which can be empty, white, black or red.
	 * @param position the location of the state that the user is curious about.
	 * @return A String that can be "EMPTY", "WHITE", "BLACK" or "RED"
	 */
	public String getBoard(String position) {
		// TODO return type?
		int colorInt = this.board.getColor(position);
		String returnValue = "ERROR";
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

	private void draw(String draw) throws ConnSixException {
		String drawValid = getValid(draw);
		String error = null;

		String[] stones = drawValid.split(":");

		if (stones.length == 1) {
			if (stones[0].toLowerCase().compareTo("k10") != 0) {
				error = "BADINPUT";
			}
		} else if (stones.length != 2) {
			error = "BADINPUT";
		}

		// update board
		for (String stone : stones) {
			String err = this.board.putStone(stone, this.color);
			if (err != null) {
				error = err;
				break;
			}
		}

		String message = "";
		if (error != null) {
			message = error + "$" + drawValid;
		} else {
			message = drawValid;
		}

		// send message
		byte[] send = message.getBytes();
		int messageLength = send.length;

		try {
			output.write(intToByte(messageLength));
			output.write(send);
			output.flush();
		}
		catch (IOException e) {
			throw new ConnSixException("IOException on sending message to platform.");
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
			throw new ConnSixException("IOException on reading stones from platform.");
		}
		
		if(result.toLowerCase().compareTo("win") == 0 || result.toLowerCase().compareTo("lose") == 0) {
			return result;
		}

		// update board
		String[] stones = result.split(":");
		for (String stone : stones) {
			this.board.putStone(stone, this.opponent);
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
}
