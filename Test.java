import java.util.*;

public class Test {
	public static void main (String [] args)
	{
		Scanner scan = new Scanner(System.in);

		int choice;

		System.out.println("Hello, player! Please choose your difficulty: ");
		System.out.println( "1: Easy\n" +
							"2: Hard");
		
		choice = scan.nextInt();
		
		gameMap newMap = new gameMap(choice * 50, choice * 50, choice);
		
		};
			


	}

