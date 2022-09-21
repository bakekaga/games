import java.io.*;
import java.util.*;

public class SpellingBee {
  private ArrayList<String> dict, pangrams, found;
  private TreeMap<String, Boolean> valid;
  private Scanner sc;
  private String letters, rank;
  private int points, rankNum, totalPossible;
  private int[] rankCutoffs;
  private String[] rankNames = {"Beginner", "Good Start", "Moving Up", "Good", "Solid", "Nice", "Great", "Amazing", "Genius", "Queen Bee"};
  private double[] rankPercentages = {0, 0.02, 0.05, 0.08, 0.15, 0.25, 0.4, 0.5, 0.7, 1};
  private boolean quit, validGuess, printOptions;

  public SpellingBee() throws FileNotFoundException {
    dict = new ArrayList<String>();
    pangrams = new ArrayList<String>();
    sc = new Scanner(new File("dictionary.txt"));
    while (sc.hasNext()) {
      String str = sc.next();
      HashSet<Character> chars = new HashSet<Character>();
      for (int i = 0; i < str.length(); i++) {
        chars.add(str.charAt(i));
      }
      if (chars.size() == 7) pangrams.add(str);
      dict.add(str);
    }
    sc = new Scanner(System.in);
    //System.out.println(pangrams.get(0));
    //System.out.println(pangrams.size() + " " + dict.size());
    play();
  }

  public void play() {
    points = rankNum = 0;
    rank = rankNames[rankNum];
    found = new ArrayList<String>();
    valid = new TreeMap<String, Boolean>();
    totalPossible = 0;
    rankCutoffs = new int[10];
    quit = false;
    printOptions = true;
    
    intro();
    while (points < totalPossible && !quit) {
      validGuess = false;
      while (!validGuess && !quit) {
        if (printOptions) System.out.println("Rank: " + rank + " Points: " + points + "\n***********\n     " + letters.charAt(1) + "     \n   " + letters.charAt(2) + "   " + letters.charAt(3) + "   \n     " + letters.charAt(0) + "     \n   " + letters.charAt(4) + "   " + letters.charAt(5) + "\n     " + letters.charAt(6) + "     \n***********\nType out any word using letters above. You must use the center letter.\nType !info for detailed rules.\nType !list for the list of words you've found already.\nType !shuffle to shuffle the letters displayed.\nType !rank to display all ranks.\nType !quit to quit game.\n");
        input();
      }
    }
    // finish game, ask for replay
    if (points == totalPossible) System.out.println("You found every word and achieved the rank of Queen Bee!");
    System.out.println("Would you like to play again?\nType '1' to play again.\nType '2' to quit.");
    String replay = sc.nextLine();
    while (!replay.equals("1") && !replay.equals("2")) {
      System.out.println("Please enter '1' or '2'.");
      replay = sc.nextLine();
    }
    if (replay.equals("1")) play();
    else System.out.println("Thanks for playing!");
  }

  public void intro() {
    System.out.println("Would you like to select the seven letters for the Spelling Bee Game (enter '1') or would you like to play a random Spelling Bee game (enter '2')?");
    String mode = sc.nextLine();
    while (!mode.equals("1") && !mode.equals("2")) {
      System.out.println("Please enter '1' or '2'.");
      mode = sc.nextLine();
    }
    // check if user inputted set of 7 letters is valid
    if (mode.equals("1")) {
      System.out.println("Enter the seven letters you would like to use in lowercase with no spaces between them. The first letter must be the center letter.");
      boolean ok = false;
      while (!ok) {
        letters = sc.nextLine();
        HashSet<Character> chars = new HashSet<Character>();
        for (int i = 0; i < letters.length(); i++) {
          if (letters.charAt(i) < 'a' || letters.charAt(i) > 'z' || chars.contains(letters.charAt(i))) {
            ok = true;
            break;
          }
          else {
            chars.add(letters.charAt(i));
          }
        }
        // check if string only consists of 7 alphabet letters
        if (ok || chars.size() != 7) {
          System.out.println("Please enter a set of seven distinct lowercase letters.");
          ok = false;
        }
        // check if set of letters has at least one pangram
        else {
          for (String s : pangrams) {
            ok = true;
            for (int i = 0; i < s.length(); i++) {
              if (letters.indexOf(s.charAt(i)) < 0) {
                ok = false;
              }
            }
            if (ok) {
              break;
            }
          }
          if (!ok) {
            System.out.println("Please enter a set of seven letters that has at least one pangram.");
          }
        }
      }
    }
    else if (mode.equals("2")) {
      letters = pangrams.get((int) (pangrams.size() * Math.random()));
      System.out.println(letters);
      List<String> temp = Arrays.asList(letters.split(""));
      Collections.shuffle(temp);
      letters = "";
      HashSet<String> used = new HashSet<String>();
      for (String s : temp) {
        if (used.add(s)) letters+= s;
      }
    }
    // generate word list and rank cutoff scores
    for (String s : dict) {
      boolean ok = s.indexOf(letters.charAt(0)) >= 0;
      for (int i = 0; i < s.length(); i++) {
        if (letters.indexOf(s.charAt(i)) < 0) {
          ok = false;
          break;
        }
      }
      if (ok) {
        valid.put(s, false);
        if (s.length() > 4) {
          totalPossible+= s.length();
        }
        else totalPossible++;
        if (pangrams.contains(s)) {
          totalPossible+= 7;
        }
      }
    }
    for (int i = 0; i < 10; i++) {
      rankCutoffs[i] = (int) (rankPercentages[i] * totalPossible);
    }

    // print rules
    System.out.println("\nHOW TO PLAY:\nWords must contain at least 4 letters.\nWords must include the center letter.\nOur word list does not include words that are obscure, hyphenated, or proper nouns.\nNo cussing either, sorry.\nLetters can be used more than once.\n\nSCORING RULES:\n4-letter words are worth 1 point each.\nLonger words earn 1 point per letter.\nEach puzzle includes at least one \"pangram\" which uses every letter. These are worth 7 extra points!\n");
  }

  public void input() {
    String guess = sc.nextLine();
    if (guess.equals("!info")) {
      // print options
      System.out.println("HOW TO PLAY:\nWords must contain at least 4 letters.\nWords must include the center letter.\nOur word list does not include words that are obscure, hyphenated, or proper nouns.\nNo cussing either, sorry.\nLetters can be used more than once.\n\nSCORING RULES:\n4-letter words are worth 1 point each.\nLonger words earn 1 point per letter.\nEach puzzle includes at least one \"pangram\" which uses every letter. These are worth 7 extra points!\n");
      printOptions = true;
    }
    else if (guess.equals("!list")) {
      if (found.size() == 0) System.out.println("You haven't found any valid words yet!");
      else {
        // print list
        Collections.sort(found);
        for (String s : found) {
          if (pangrams.contains(s)) System.out.println(s + "\t\tPANGRAM");
          else System.out.println(s);
        }
      }
      System.out.println();
      printOptions = true;
    }
    else if (guess.equals("!shuffle")) {
      // shuffle letters like in intro() method for random set of letters
      List<String> temp = Arrays.asList(letters.split(""));
      Collections.shuffle(temp);
      letters = letters.charAt(0) + "";
      for (String s : temp) if (!s.equals(letters.substring(0, 1))) letters+= s;
      System.out.println();
      printOptions = true;
    }
    else if (guess.equals("!rank")) {
      // print precomputed ranks
      System.out.println("Ranks are based on a percentage of possible points in a puzzle. The minimum scores to reach each rank for today's are:");
      for (int i = 0; i < 10; i++) {
        System.out.println(rankNames[i] + ": " + rankCutoffs[i]);
      }
      System.out.println();
      printOptions = true;
    }
    else if (guess.equals("!quit")) {
      quit = true;
      System.out.println("Would you like to see the solution for the Spelling Bee game you just played?\nType '1' to see the word list.\nType '2' to pass.");
      String reveal = sc.nextLine();
      while (!reveal.equals("1") && !reveal.equals("2")) {
        System.out.println("Please enter '1' or '2'.");
        reveal = sc.nextLine();
      }
      if (reveal.equals("1")) {
        // print all words
        System.out.println("Total possible points: " + totalPossible + "\nTotal number of words: " + valid.size() + "\n* indicated a found word.");
        for (String s : valid.keySet()) {
          if (valid.get(s)) System.out.print("* ");
          if (pangrams.contains(s)) System.out.println(s + "\t\tPANGRAM");
          else System.out.println(s);
        }
      }
    }
    else if (!valid.containsKey(guess)) {
      if (guess.length() < 4) System.out.println("Your guess needs more than 4 letters.");
      else {
        int which = 0;
        for (int i = 0; i < guess.length(); i++) {
          if (guess.charAt(i) < 'a' || guess.charAt(i) > 'z') {
            which = 1;
            break;
          }
          else if (letters.indexOf(guess.charAt(i)) < 0) {
            which = 2;
            break;
          }
        }
        if (which == 0 && !dict.contains(guess)) System.out.println("Your guess is not a recognized word.");
        else if (which == 0) System.out.println("Your guess needs to contain the center letter.");
        else if (which == 1) System.out.println("Your guess can only contain lowercase alphabet letters.");
        else if (which == 2) System.out.println("Your guess can only contain letters from the seven given ones.");
      }
      printOptions = false;
    }
    else if (valid.get(guess)) {
      System.out.println("You already guessed this word.");
    }
    else {
      // valid guess
      validGuess = printOptions = true;
      valid.put(guess, true);
      found.add(guess);
      if (guess.length() > 4) {
        points+= guess.length();
      }
      else points++;
      if (pangrams.contains(guess)) {
        System.out.println("Your guess was a valid pangram!");
        points+= 7;
      }
      else {
        if (guess.length() == 4) System.out.print("Good! ");
        else if (guess.length() == 5 || guess.length() == 6) System.out.print("Nice! ");
        else if (guess.length() >= 7) System.out.print("Awesome! ");
        System.out.println("Your guess was a valid word!\n");
      }
      if (points >= rankCutoffs[rankNum + 1]) {
        rankNum++;
        rank = rankNames[rankNum];
      }
    }
  }
}
