import java.util.Scanner;

public class DummyAI {
	static int mine;
	static int opponent;
	static int red = 3;
	
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input the ip address > ");
		String ip = scanner.nextLine();
		System.out.print("Input the port number > ");
		int port = Integer.parseInt(scanner.nextLine());
		System.out.print("Input the color > ");
		String color = scanner.nextLine();
		
		ConnectSix conSix = new ConnectSix(ip, port, color);
		System.out.println("Red Stone positions are " + conSix.redStones);

		if (color.toLowerCase().compareTo("black") == 0) {
			String first = conSix.drawAndRead("K10");
		} else if (color.toLowerCase().compareTo("white") == 0) {
			String first = conSix.drawAndRead("");
		}
		
		while (true) {
			
			String draw = Connect6.returnStringCoor();;
			
			String read = conSix.drawAndRead(draw);
			
			if(read.compareTo("WIN") == 0 || read.compareTo("LOSE") == 0 || read.compareTo("EVEN") == 0) {
				 break;
			}
		}

	}
	
	// return the AI color. black = 1, white = 2, red = 3, empty = 0
		public static int getMyColor() {
			return mine;
		}
		// return the person color. black = 1, white = 2, red = 3, empty = 0
			public static int getYourColor() {
				return opponent;
		}	
		
			public static int getRedColor() {
				return red;
		}
			
}
