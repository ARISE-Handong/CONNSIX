package edu.handong.arise;

public class ConnSixException extends Exception {

	public ConnSixException() {
		super();
	}

	public ConnSixException(String message) {
		super(message);
		System.err.println(message);
	}

}
