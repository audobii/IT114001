import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class NumberGuesserHW {
	private int level = 1;
	private int strikes = 0;
	private int maxStrikes = 5;
	private int number = 0;
	private boolean isRunning = false;
	private int loseStreak = 0; //new variable to keep track of losing streak
	final String saveFile = "numberGuesserSave.txt";

	/***
	 * Gets a random number between 1 and level.
	 * 
	 * @param level (level to use as upper bounds)
	 * @return number between bounds
	 */
	public static int getNumber(int level) {
		int range = 9 + ((level - 1) * 5);
		System.out.println("I picked a random number between 1-" + (range + 1) + ", let's see if you can guess.");
		return new Random().nextInt(range) + 1;
	}

	//changed position of saveData(); (previously saveLevel();) in win() and lose() functions
	//loseStreak resets to 0 when win, increases by 1 when lose
	private void win() {
		System.out.println("That's right!");
		level++;// level up!
		strikes = 0;
		System.out.println("Welcome to level " + level);
		number = getNumber(level);
		loseStreak = 0;
		saveData();
	}

	private void lose() {
		System.out.println("Uh oh, looks like you need to get some more practice.");
		System.out.println("The correct number was " + number);
		strikes = 0;
		level--;
		if (level < 1) {
			level = 1;
		}
		number = getNumber(level);
		loseStreak++;
		saveData();
		//if lose streak gets too high, let player know and reset everything to beginning values
		if(loseStreak > 5) {
			System.out.println("Too many losses! Returning to beginning...");
			level = 1;
			strikes = 0;
			loseStreak = 0;
			System.out.println("Please guess a number.");
		}
	}

	private void processCommands(String message) {
		if (message.equalsIgnoreCase("quit")) {
			System.out.println("Tired of playing? No problem, see you next time.");
			isRunning = false;
		}
	}

	private void processGuess(int guess) {
		if (guess < 0) {
			return;
		}
		System.out.println("You guessed " + guess);
		if (guess == number) {
			win();
		} else {
			System.out.println("That's wrong");
			strikes++;
			if (strikes >= maxStrikes) {
				lose();
			} else {
				int remainder = maxStrikes - strikes;
				System.out.println("You have " + remainder + "/" + maxStrikes + " attempts remaining");
				//inserted saveData(); here
				saveData();
				if (guess > number) {
					System.out.println("Lower");
				} else if (guess < number) {
					System.out.println("Higher");
				}
			}
		}
	}

	private int getGuess(String message) {
		int guess = -1;
		try {
			guess = Integer.parseInt(message);
		} catch (NumberFormatException e) {
			System.out.println("You didn't enter a number, please try again");

		}
		return guess;
	}

	private void saveLevel() {
		try (FileWriter fw = new FileWriter(saveFile)) {
			fw.write("" + level);// here we need to convert it to a String to record correctly

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//new method saveData()
	//does what saveLevel does but also writes strikes and number AND loseStreak to be guessed to file
	private void saveData() {
		try (FileWriter fw = new FileWriter(saveFile)) {
			fw.write("" + level + " " + strikes + " " + number + " " + loseStreak); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean loadLevel() {
		File file = new File(saveFile);
		if (!file.exists()) {
			return false;
		}
		try (Scanner reader = new Scanner(file)) {
			while (reader.hasNextLine()) {
				//reads strikes and number AND loseStreak from file in addition to level
				int _level = reader.nextInt();
				int _strikes = reader.nextInt();
				int _number = reader.nextInt();
				int _loseStreak = reader.nextInt();
				if (_level > 1) {
					//sets current strikes and number AND loseStreak to ints read in from file
					level = _level;
					strikes = _strikes;	
					number = _number;
					loseStreak = _loseStreak;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
		return level > 1;
	}

	void run() {
		try (Scanner input = new Scanner(System.in);) {
			System.out.println("Welcome to Number Guesser 4.0!");
			System.out.println("I'll ask you to guess a number between a range, and you'll have " + maxStrikes
					+ " attempts to guess.");
			if (loadLevel()) {
				System.out.println("Successfully loaded level " + level + " let's continue then");
				//tell to user how many strikes they have from their previous save
				System.out.println("You have " + strikes + " strikes.");
			}
			//if starting a new level, get a new number rather than the one read from the file
			if(strikes == 0) {
				number = getNumber(level);	
			}
			isRunning = true;
			while (input.hasNext()) {
				String message = input.nextLine();
				processCommands(message);
				if (!isRunning) {
					break;
				}
				int guess = getGuess(message);
				processGuess(guess);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		NumberGuesserHW guesser = new NumberGuesserHW();
		guesser.run();
	}
}