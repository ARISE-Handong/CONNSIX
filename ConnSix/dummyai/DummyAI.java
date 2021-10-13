import java.util.Scanner;

public class DummyAI {
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
			char alpha1 = (char) ((Math.random() * 19) + 'A');
			int num1 = (int)( Math.random() * 19) + 1;
			char alpha2 = (char) ((Math.random() * 19) + 'A');
			int num2 = (int)( Math.random() * 19) + 1;
			
			String draw = String.format("%c%02d:%c%02d", alpha1, num1, alpha2, num2);
			
			String read = conSix.drawAndRead(draw);
			
			if(read.compareTo("WIN") == 0 || read.compareTo("LOSE") == 0 || read.compareTo("EVEN") == 0) {
				 break;
			}
		}

	}
}
