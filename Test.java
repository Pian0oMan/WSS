import java.util.*;

public class Test {
	public static void main (String [] args)
	{
		Scanner scan = new Scanner(System.in);

		int choiceDifficulty;
		String choiceBrainType;
		String choiceVisionType;

		System.out.println("Hello, player! Please choose your difficulty: ");
		System.out.println( "1: Easy\n" +
							"2: Hard");
		
		choiceDifficulty = scan.nextInt();
		
		while (choiceDifficulty != 1 && choiceDifficulty != 2) {
    	System.out.println("Invalid choice. Try again:");
    	choiceDifficulty = scan.nextInt();
		}	
		
		gameMap newMap = new gameMap(choiceDifficulty * 50, choiceDifficulty * 50, choiceDifficulty);
		
		System.out.println("Please select the BRAIN TYPE for Player: ");
		System.out.println("A: \n" +
							"B: \n" + 
						    "C: \n" + 
							"D: \n");
		choiceBrainType = scan.next();

		System.out.println("Please select the VISION TYPE for Player: ");
		System.out.println("A: \n" +
							"B: \n" + 
						    "C: \n" + 
							"D: \n");
		choiceVisionType = scan.next();

		Player player = new Player(newMap, 0, (choiceDifficulty * 50)/2, choiceBrainType, choiceVisionType);

		System.out.println("If you can get through the whole map without dying, you win!");
		System.out.print("Game starting in: ");
		for (int i = 3; i > 0; i--)
		{
			System.out.print(i + "...\n");
			try{
			Thread.sleep(1000);
			} catch (InterruptedException ignored) {}

		}

		System.out.println("GO!");

		}
			
		public void displayMap()			
		{

		}
	}

	

