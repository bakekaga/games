import java.util.*;
import java.io.*;

class Main {
  public static void main(String[] args) throws IOException
  {
    Scanner sc = new Scanner(new File("possible_answers.txt"));
    List<String> possibleAnswers = new ArrayList<String>();
    while(sc.hasNext())
    {
      possibleAnswers.add(sc.next());
    }
    sc = new Scanner(new File("all_words.txt"));
    Set<String> allWords = new HashSet<String>();
    while(sc.hasNext())
    {
      allWords.add(sc.next());
    }
    Absurdle w = new Absurdle(allWords, possibleAnswers);
  }
}