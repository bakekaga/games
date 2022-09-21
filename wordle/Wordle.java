import java.util.*;
import java.io.*;

public class Wordle
{

  private boolean testMode; // if true, test mode on, else play mode
  // add instance and/or static variables
  private Set<String> allWords;
  private List<String> possibleAnswers;
  private Scanner sc;
  private String word, guess;
  private ArrayList<String> guesses;
  private ArrayList<int[]> res;
  private int[] letters, cnt, cur;
  private int rem;

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
  public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
  public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

  
  public Wordle(Set<String> a, List<String> p, boolean testM)
  {
    allWords = a;
    possibleAnswers = p;
    testMode = testM; 
    // add code below
    play();
  }
  
  // add methods
  public void play() {
    sc = new Scanner(System.in);
    guesses = new ArrayList<String>();
    res = new ArrayList<int[]>();
    letters = new int[26];
    rem = 1;
    init();
    guess();
    replay();
  }
  
  public void init() {
    // intro
    System.out.println("*************\nWelcome to Wordle!\nGuess the WORDLE in six tries.\nEach guess must be a valid five-letter word.\nHit the enter button to submit.\nAfter each guess, the color of the tiles will change to show how close your guess was to the word.\n*************");
    // select secret word
    if (testMode) {
      System.out.println("Enter the secret word. It must consist of five lowercase letters in the English alphabet.");
      do {
        word = sc.nextLine();
      } while (!inpVal(word, false));
    }
    else {
      word = possibleAnswers.get((int) (possibleAnswers.size() * Math.random()));
    }
  }

  public void guess() {
    boolean notFound = true;
    while (rem < 7 && notFound) {
      // display remaining letters and prompt for guess
      System.out.println("ATTEMPT #" + rem + "\nLetters still in play: ");
      for (int i = 0; i < letters.length; i++) {
        if (letters[i] == 0) {
          System.out.print(ANSI_RESET + (char) (i + 'a') + ANSI_RESET + " ");
        }
        else if (letters[i] == 1) {
          System.out.print(ANSI_GREEN + (char) (i + 'a') + ANSI_RESET + " ");
        }
        else if (letters[i] == 2) {
          System.out.print(ANSI_YELLOW + (char) (i + 'a') + ANSI_RESET + " ");
        }
      }
      System.out.println("\nEnter your guess:");
      // take input
      do {
        guess = sc.nextLine();
      } while (!inpVal(guess, true));

      String out = "";
      cnt = new int[26];
      cur = new int[5];
      for (int i = 0; i < word.length(); i++) {
        cnt[word.charAt(i) - 'a']++;
      }
      // process green letters
      for (int i = 0; i < guess.length(); i++) {
        if (guess.charAt(i) == word.charAt(i)) {
          letters[guess.charAt(i) - 'a'] = 1;
          cnt[guess.charAt(i) - 'a']--;
          cur[i] = 1;
        }
      }
      // process yellow letters
      for (int i = 0; i < guess.length(); i++) {
        if (cnt[guess.charAt(i) - 'a'] > 0 && guess.charAt(i) != word.charAt(i)) {
          if (letters[guess.charAt(i) - 'a'] != 1) letters[guess.charAt(i) - 'a'] = 2;
          cnt[guess.charAt(i) - 'a']--;
          cur[i] = 2;
        }
      }
      // process absent letters
      for (int i = 0; i < guess.length(); i++) {
        if (cnt[guess.charAt(i) - 'a'] == 0 && cur[i] == 0) {
          if (letters[guess.charAt(i) - 'a'] != 1 && letters[guess.charAt(i) - 'a'] != 2) letters[guess.charAt(i) - 'a'] = 3;
          cur[i] = 3;
        }
      }
      // add green/yellow highlighting to guess in results
      for (int i = 0; i < guess.length(); i++) {
        if (cur[i] == 1) out+= ANSI_GREEN_BACKGROUND + guess.charAt(i) + ANSI_RESET;
        else if (cur[i] == 2) out+= ANSI_YELLOW_BACKGROUND + guess.charAt(i) + ANSI_RESET;
        else if (cur[i] == 3) out+= guess.charAt(i);
      }
      guesses.add(out);
      res.add(cur);
      
      System.out.println("***RESULTS***");
      for (String s : guesses) System.out.println(s);
      System.out.println("*************");
      if (guess.equals(word)) {
        notFound = false;
      }
      rem++;
    }
    if (!notFound) {
      System.out.println("You won in " + (rem - 1) + " guesses!");
    }
    else {
      System.out.println("You did not guess today's wordle. The secret word was: " + word);
    }
    // print shareable result
    String out = "";
    for (int i = 0; i < rem - 1; i++) {
      if (i > 0) out+= "\n";
      for (int j = 0; j < 5; j++) {
        if (res.get(i)[j] == 1) out+= ANSI_GREEN_BACKGROUND + " " + ANSI_RESET;
        else if (res.get(i)[j] == 2) out+= ANSI_YELLOW_BACKGROUND + " " + ANSI_RESET;
        else if (res.get(i)[j] == 3) out+= ANSI_WHITE_BACKGROUND + " " + ANSI_RESET;
        out+= " ";
      }
      out+= "\n";
    }
    System.out.println("---------\n" + out + "---------");
  }

  public void replay() {
    // ask if player wants to start a new game
    System.out.println("Would you like to play again? (y/n)");
    String response = sc.nextLine();
    while (!response.equals("y") && !response.equals("n")) {
      System.out.println("Please enter either \'y\' or \'n\'.");
      response = sc.nextLine();
    }
    if (response.equals("y")) play();
    else System.out.println("Thanks for playing Wordle!");
  }

  public boolean inpVal(String input, boolean which) {
    if (input.length() != 5) {
      System.out.println("Please enter a word consisting of only 5 characters.");
      return false;
    }
    for (int i = 0; i < input.length(); i++) {
      if (input.charAt(i) - 'a' < 0 || input.charAt(i) - 'a' > 25) {
        System.out.println("Please enter a word consisting only of lowercase alphabet characters.");
        return false;
      }
    }
    if (which && !allWords.contains(input)) {
      System.out.println("Please enter a valid word in the English language.");
      return false;
    }
    if (!which && !possibleAnswers.contains(input)) {
      System.out.println("Please enter a valid word in the list of possible answers.");
      return false;
    }
    return true;
  }
}
