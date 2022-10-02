import java.util.*;
import java.io.*;

public class Absurdle
{
  private Set<String> allWords;
  private List<String> possibleAnswers, curAnswers;
  private Scanner sc;
  private String guess;
  private ArrayList<String> guesses;
  private ArrayList<Integer> res;
  private int[] letters, cnt, cur;
  private int rem;

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
  public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
  public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

  public static class cmp implements Comparator<Integer> {
    public int compare(Integer a, Integer b) {
      int cntGa = 0, cntYa = 0, cntGb = 0, cntYb = 0;
      int a_ = a, b_ = b;
      for (int i = 0; i < 5; i++) {
        if (a_ % 10 == 1) cntGa++;
        if (b_ % 10 == 1) cntGb++;
        if (a_ % 10 == 2) cntYa++;
        if (a_ % 10 == 2) cntYb++;
        a_/= 10;
        b_/= 10;
      }
      if (cntGa == cntGb) {
        if (cntYa == cntYb) {
          for (int i = 0; i < 5; i++) {
            if (a % 10 != b % 10) {
              return a % 10 - b % 10;
            }
            a/= 10;
            b/= 10;
          }
        }
        return cntYb - cntYa;
      }
      return cntGb - cntGa;
    }
  }

  public Absurdle(Set<String> a, List<String> p)
  {
    allWords = a;
    possibleAnswers = p;
    // add code below
    play();
  }
  
  // add methods
  public void play() {
    curAnswers = possibleAnswers;
    sc = new Scanner(System.in);
    guesses = new ArrayList<String>();
    res = new ArrayList<Integer>();
    letters = new int[26];
    rem = 1;
    init();
    guess();
    replay();
  }
  
  public void init() {
    // intro
    System.out.println("*************\nWelcome to Absurdle!\nYou have unlimited tries to guess the Absurdle.\nYou can give up after any guess by typing '!quit'.\nYou can make a random guess by typing '!rand'.\nEach guess must be a valid five-letter word.\nHit the enter button to submit.\nAfter each guess, the color of the tiles will change to show how close your guess was to the word.\n*************");
  }

  public void guess() {
    boolean notFound = true, giveUp = true;
    while (notFound && giveUp) {
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
        if (guess.equals("!quit")) {
          giveUp = false;
          break;
        }
        if (guess.equals("!rand")) {
          guess = possibleAnswers.get((int) (possibleAnswers.size() * Math.random()));
        }
      } while (!inpVal(guess, true));

      if (!giveUp) break;

      TreeMap<Integer, ArrayList<String>> buckets = new TreeMap<Integer, ArrayList<String>>(new cmp());
      int outId = 0;
      for (String s : curAnswers) {
        cnt = new int[26];
        cur = new int[5];
        for (int i = 0; i < 5; i++) {
          cnt[s.charAt(i) - 'a']++;
        }
        // process green letters
        for (int i = 0; i < 5; i++) {
          if (guess.charAt(i) == s.charAt(i)) {
            cnt[s.charAt(i) - 'a']--;
            cur[i] = 1;
          }
        }
        // process yellow letters
        for (int i = 0; i < 5; i++) {
          if (cnt[guess.charAt(i) - 'a'] > 0 && guess.charAt(i) != s.charAt(i)) {
            cnt[guess.charAt(i) - 'a']--;
            cur[i] = 2;
          }
        }
        // process absent letters
        for (int i = 0; i < 5; i++) {
          if (cnt[guess.charAt(i) - 'a'] == 0 && cur[i] == 0) {
            cur[i] = 3;
          }
        }
        int id = 0, curPow = 1;
        for (int i = 0; i < 5; i++) {
          id+= curPow * cur[i];
          curPow*= 10;
        }
        if (!buckets.containsKey(id)) {
          buckets.put(id, new ArrayList<String>());
        }
        buckets.get(id).add(s);
        outId = id;
      }

      for (Integer comb : buckets.keySet()) {
        if (buckets.get(comb).size() >= buckets.get(outId).size()) outId = comb;
        // System.out.println(comb + " " + buckets.get(comb) + " " + buckets.get(comb).size());
      }

      if (buckets.get(outId).size() == 1) {
        for (Integer comb : buckets.keySet()) {
          if (!buckets.get(comb).get(0).equals(guess)) outId = comb;
        }
      }

      if (buckets.get(outId).get(0).equals(guess)) notFound = false;
      
      guesses.add(guess);
      res.add(outId);
      curAnswers = buckets.get(outId);

      for (int i = 0; i < 5; i++) {
        if (outId % 10 == 1) {
          letters[guess.charAt(i) - 'a'] = 1;
        }
        if (outId % 10 == 2 && letters[guess.charAt(i) - 'a'] != 1) letters[guess.charAt(i) - 'a'] = 2;
        if (outId % 10 == 3 && letters[guess.charAt(i) - 'a'] != 1 && letters[guess.charAt(i) - 'a'] != 2) letters[guess.charAt(i) - 'a'] = 3;
        outId/= 10;
      }
      possibleAnswers.remove(guess);
      printResults();
      rem++;
    }
    if (!notFound) {
      System.out.println("You won in " + (rem - 1) + " guesses!");
    }
    if (!giveUp) {
      System.out.println("You did not get the Absurdle.");
    }
    // print shareable result
    String out = "";
    for (int i = 0; i < rem - 1; i++) {
      if (i > 0) out+= "\n";
      int outId = res.get(i);
      for (int j = 0; j < 5; j++) {
        if (outId % 10 == 1) out+= ANSI_GREEN_BACKGROUND + " " + ANSI_RESET;
        else if (outId % 10 == 2) out+= ANSI_YELLOW_BACKGROUND + " " + ANSI_RESET;
        else if (outId % 10 == 3) out+= ANSI_WHITE_BACKGROUND + " " + ANSI_RESET;
        out+= " ";
        outId/= 10;
      }
      out+= "\n";
    }
    System.out.println("---------\n" + out + "---------");
  }

  public void printResults() {
    System.out.println("***RESULTS***");
    for (int i = 0; i < guesses.size(); i++) {
      String out = "";
      int curId = res.get(i);
      // add green/yellow highlighting to result of attempt #(i + 1)
      for (int j = 0; j < 5; j++) {
        if (curId % 10 == 1) out+= ANSI_GREEN_BACKGROUND + guesses.get(i).charAt(j) + ANSI_RESET;
        else if (curId % 10 == 2) out+= ANSI_YELLOW_BACKGROUND + guesses.get(i).charAt(j) + ANSI_RESET;
        else if (curId % 10 == 3) out+= guesses.get(i).charAt(j);
        curId/= 10;
      }
      System.out.println(out);
    }
    System.out.println("*************");
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
    else System.out.println("Thanks for playing Absurdle!");
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