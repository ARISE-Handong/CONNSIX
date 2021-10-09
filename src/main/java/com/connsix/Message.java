package com.connsix;
import java.util.*;

class Message{
	public static int byteToInt(byte[] bytes) {
		return ((bytes[3] & 0xFF) << 24) | 
			((bytes[2] & 0xFF) << 16) | 
			((bytes[1] & 0xFF) << 8 ) | 
			((bytes[0] & 0xFF) << 0 );
	}

	public static byte[] intToByte(int intValue) {
		byte[] byteArray = new byte[4];
		byteArray[3] = (byte)(intValue >> 24);
		byteArray[2] = (byte)(intValue >> 16);
		byteArray[1] = (byte)(intValue >> 8);
		byteArray[0] = (byte)(intValue);
		return byteArray;
	}

	public static int[] parseString(String stones){
		int start = 0, end=0;
		int[] pointArray = new int[4];
		char firstAlphabet='0', secondAlphabet='0';
		String firstPoint="", secondPoint="";
		if(stones.equals("K10")){
			Arrays.fill(pointArray, 9);
			return pointArray;
		}
		for(int i=0; i<stones.length(); i++){
			if(stones.charAt(i)==':'){
				end = i;
				firstPoint = stones.substring(start , end);
				start = end + 1;
			}
		}
		secondPoint = stones.substring(end+1);
		firstAlphabet = firstPoint.charAt(0);
		if(firstAlphabet - 65 > 8){
			pointArray[0] = firstAlphabet - 66;
		}
		else{
			pointArray[0] = firstAlphabet - 65;
		}
		pointArray[1] = Integer.parseInt(firstPoint.substring(1))-1;
		secondAlphabet = secondPoint.charAt(0);
		if(secondAlphabet - 65 > 8){
			pointArray[2] = secondAlphabet - 66;
		}
		else{
			pointArray[2] = secondAlphabet - 65;
		}
		pointArray[3] = Integer.parseInt(secondPoint.substring(1))-1;

		return pointArray;
	}



}
