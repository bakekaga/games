import java.util.*;

import java.io.*;

/* Allows you to use out.println() or out.print() instead of out.println() or out.print() */
import static java.lang.System.*;

public class Game
{
  // declare instance variables
  private File bestScoresFile;
  private final char EMDASH = 8212;
  private final char VERTLINE = 9474;
  private int[][] board;
  private int[] vals;
  private int score, bestScore, prevScore, prevBestScore;
  private String mode, name;
  private Scanner sc;
  private boolean quit, undo, keepPlaying;
  private int[][] prevBoard;
  
  
  
  // constructor
  public Game(File f) throws IOException
  {
    bestScoresFile = f;
    // add code below
    sc = new Scanner(in);
    play();
  }

  void gameMode() {
    out.println("Type 'debug' if you would like to enter debug mode; type 'play' if you would like to play the game normally.");
    mode = sc.nextLine();
    while (!mode.equals("debug") && !mode.equals("play")) {
      out.println("Please type 'debug' or 'play'.");
      mode = sc.nextLine();
    }
    if (mode.equals("debug")) {
      for (int i = 1; i <= 4; i++) {
        out.println("Please type in the entries of row " + i + " separated by spaces.");
        String[] entries = new String[4];
        do { 
          entries = sc.nextLine().split(" ");
        } while (!validateDebugInput(entries));
        board[i - 1] = vals;
      }
    }
  }

  boolean validateDebugInput(String[] entries) {
    if (entries.length != 4) {
      out.println("Please enter exactly four entries.");
      return false;
    }
    else {
      vals = new int[4];
      for (int j = 0; j < 4; j++) {
        try {
          vals[j] = Integer.parseInt(entries[j]);
        }
        catch (Exception e) {
          out.println("Please enter only integer entries.");
          return false;
        }
      }
      int[] valCopy = vals.clone();
      for (int j = 0; j < 4; j++) {
        if (valCopy[j] < 0) {
          out.println("Please enter only positive integers.");
          return false;
        }
        // while (valCopy[j] % 2 == 0) valCopy[j]/= 2;
        // if (valCopy[j] != 1) {
        //   out.println("Please enter only powers of two as entries.");
        //   return false;
        // }
      }
    }
    return true;
  }

  // add methods
  void play() throws IOException {
    sc = new Scanner(new FileReader("bestScore.dat"));
    String curBest = sc.nextLine();
    bestScore = prevBestScore = Integer.parseInt(sc.nextLine());
    board = new int[4][4];
    prevBoard = new int[4][4];
    quit = keepPlaying = undo = false;
    score = prevScore = 0;
    tileGen();
    tileGen();
    sc = new Scanner(System.in);
    gameMode();
    
    out.println("Keep Calm Emily and Rachel are Awesome!");
    out.println("Enter your name:");
    name = sc.nextLine();
    out.println("* Welcome to 2048! *");
    out.println("Combine tiles to reach 2048");
    out.println("Controls: w = up, a = left, s = down, d = right, q = quit, u = undo (but you cannot undo two times in a row)");
    out.println("Hit enter after every move");
    //score();
    out.println("BEST SCORE OWNED BY : " + curBest + "  BEST SCORE : " + bestScore);
    printBoard();

    while (winCon().equals("ok") && !quit) {
      out.println("Please enter your move.");
      String dir = sc.nextLine();
      while (!dir.equals("w") && !dir.equals("a") && !dir.equals("s") && !dir.equals("d") && !dir.equals("q") && !dir.equals("u")) {
        out.print("Please enter a valid input! (w = up, a = left, s = down, d = right, q = quit");
        if (undo) out.println(", undo)");
        else out.println(")");
        dir = sc.nextLine();
      }
      if (dir.equals("u") && undo){
        for (int i = 0; i < 4; i++) {
          board[i] = prevBoard[i].clone();
        }
        score = prevScore;
        bestScore = prevBestScore;        
        undo=false;
      }
      else if (dir.equals("u") && !undo) {
        if (prevBoard[0] == null) out.println("You cannot undo as the first move!");
        else out.println("You already undid a move!");
      }
      else{
        undo = true;
        prevScore = score;
        prevBestScore= bestScore;
        for (int i = 0; i < 4; i++) {
          prevBoard[i] = board[i].clone();
        }
        if (!move(dir, true)){
          tileGen();
        }
        else if (!quit) out.println("Please make a move that changes the board state!");
      }
      printBoard();
    }
    if (quit) out.println("You quitter !!");
    else if (winCon().equals("L")) out.println("Take this L bruh");      
    else if (keepPlaying) out.println("You got the victory royale!");


    sc = new Scanner(new FileReader("bestScore.dat"));
    sc.nextLine();
    int top = Integer.parseInt(sc.nextLine());
    if (bestScore > top) {
      PrintWriter pw = new PrintWriter(new FileWriter("bestScore.dat"));
      pw.println(name);
      pw.println(bestScore);
      pw.close();
    }
    
    out.println("Play again ? Enter 'y' or 'n'.");
    sc = new Scanner(in);
    String end = sc.nextLine();
    while(!end.equals("y") && !end.equals("n")){
      out.println("Please type a valid response 'y' or 'n'");
      end = sc.nextLine();
    }
    if(end.equals("y")){
        play();
    }else if(end.equals("n")){
      out.println("bye bye! kiss kiss alby and jess ");
    }

  }

  void printBoard() {
    System.out.println("Score : " + score + " Best score : " + bestScore);
    for(int j=0;j<4;j++){
      for(int i=0;i<25;i++){
        out.print(EMDASH);
      }
      out.println();
      for(int i=0;i<15;i++){
        if(i%5==0 && i!=0){
          out.println();
        }
        if(i%5==4){
          out.print(VERTLINE+"     ");
        }else if(i/5==1){
          //the value line 
          out.print(VERTLINE);
          int val = board[j][i % 5];
          if (val == 0) out.print("     ");
          else {
        // adjust spacing depending on length of value
            int len = (val + "").length();
            for (int k = 0; k < (5 - len) / 2; k++) out.print(" ");
            out.print(val);
            for (int k = 0; k < 5 - len - (5 - len) / 2; k++) out.print(" ");
          }
        }else{
          out.print(VERTLINE+"     ");
        }
      }   
      out.println();
    }
    for(int i=0;i<25;i++){
      out.print(EMDASH);
    }
    out.println();
  }

  // reverses rows of board, helper method
  void reverseRow() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 2; j++) {
        int temp = board[i][3 - j];
        board[i][3 - j] = board[i][j];
        board[i][j] = temp;
      }
    }
  }

  // transposes board, helper method
  void transpose() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j <= i; j++) {
        int temp = board[i][j];
        board[i][j] = board[j][i];
        board[j][i] = temp;
      }
    }
  }

  // user input and board modification for each move
  boolean move(String dir, boolean actual) {
    int[][] boardCopy = new int[4][4];
    for (int i = 0; i < 4; i++) {
      boardCopy[i] = board[i].clone();
    }
    if (dir.equals("w")) {
      transpose();
      reverseRow();
      right(actual);
      reverseRow();
      transpose();
      // out.println("wKeep Calm Emily and Rachel are Awesome!");
    }
    else if (dir.equals("a")) {
      reverseRow();
      right(actual);
      reverseRow();
      // out.println("aKeep Calm Emily and Rachel are Awesome!");
    }
    else if (dir.equals("s")) {
      transpose();
      right(actual);
      transpose();
      // out.println("sKeep Calm Emily and Rachel are Awesome!");
    }
    else if (dir.equals("d")) {
      right(actual);
      // out.println("dKeep Calm Emily and Rachel are Awesome!");
    }
    else if (dir.equals("q")) quit = true;
    boolean ans = true;
    for (int i = 0; i < 4; i++) {
      if (!Arrays.equals(boardCopy[i], board[i])) ans = false;
      if (!actual) board[i] = boardCopy[i].clone();
    }
    return ans;
  }

  String winCon() {
    if (!keepPlaying) {
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
          if (board[i][j] == 2048) {
            out.println("You got 2048! Would you like to keep playing? (y/n)");
            String cont = sc.nextLine();
            while(!cont.equals("y") && !cont.equals("n")){
              out.println("Please type a valid response 'y' or 'n'");
              cont = sc.nextLine();
            }
            keepPlaying = true;
            if (cont.equals("y")) {
              return "ok";
            }else {
              return "W";
            }
          }
        }
      }
    }
    String s = "wasd";
    boolean ans = true;
    for (int i = 0; i < 4; i++) {
      ans = move(s.substring(i, i + 1), false);
      if (!ans) return "ok";
    }
    return "L";
  }

  // move all values in board to the right
  void right(boolean actual) {
    for (int i = 0; i < 4; i++) {
      ArrayList<Integer> arr = new ArrayList<Integer>();
      // boolean conditional for if past 2 values were combined
      boolean comb = false;
      for (int j = 3; j >= 0; j--) {
        if (board[i][j] == 0) continue; 
        if (arr.size() == 0) arr.add(board[i][j]);
        else if (board[i][j] == arr.get(arr.size() - 1)) {
          // double value if two adjacent values are equal and one of them wasn't just created during the move
          if (!comb) {
            arr.add(arr.remove(arr.size() - 1) * 2);
            if (actual) {
              score+= arr.get(arr.size()-1);
              bestScore = Math.max(score, bestScore);
            }
            comb = true;
          }
          else {
            arr.add(board[i][j]);
            comb = false;
          }
        }
        else {
          arr.add(board[i][j]);
          comb = false;
        }
      }
      // change board values to values after moving
      for (int j = 3; j >= 4 - arr.size(); j--) {
        board[i][j] = arr.get(3 - j);
      }
      for (int j = 3 - arr.size(); j >= 0; j--) {
        board[i][j] = 0;
      }
    }
  }

  void tileGen() {
    int tileValue= 0;
    int tileXPos = 0;
    int tileYPos = 0;
    ArrayList<Integer> open = new ArrayList<Integer>();
    
    double valChance = Math.random();
    if(valChance>=.9){
      tileValue=4;
    }else{
      tileValue=2;
    }
    for(int i=0;i<board.length;i++){
      for(int j=0;j<board[0].length;j++){
        int temp=i*10 +j;
        if(board[i][j]==0){
          open.add(temp);
        }
      }
    }
    int tilePos= (int)(Math.random()*open.size());
    int index = open.get(tilePos);
    tileXPos= index/10;
    tileYPos= index%10;
    board[tileXPos][tileYPos] = tileValue; 
  }
}
