import java.util.*;
import java.io.*;

public class TicTacToe
{
  // private PrintWriter pw;
  private Scanner sc;
  private char[][] gameState; // a gameState is ' ' if that grid can still be played in, 'O' if player 1 won, 'X' if player 2 won, 'D' if it ended in a draw
  private char[][] board;
  private int nextMove; // if 0 <= nextMove <= 8 next move can be played in grid nextMove, if nextMove = 9 next move can be played anywhere
  private boolean player; // player is true if player 1 is moving, false if player 2 is moving
  private char winner; // winner is 'X' if player 1 won a board or the game, 'O' if player 2 won a board or the game, 'D' if a board or the game ended in a draw, 'I' if the current board or the game is still ongoing
  private int moveNum; // moveNum tracks the number of legal moves that have been made
    
  public TicTacToe(Scanner s)
  {
    sc = s;
    gameState = new char[3][3];
    board = new char[9][9];
    nextMove = 9;
    for (int i = 0; i < 9; i++) {
      gameState[i / 3][i % 3] = ' ';
      for (int j = 0; j < 9; j++) {
        board[i][j] = ' ';
      }
    }
    player = true;
    winner = 'I';
    moveNum = 1;
  }

  public void play()
  {
    System.out.println("Player 1 will be X and Player 2 will be O!\nGood luck :D\n");
    while (winner == 'I') {
      if (nextMove != 9) {
        for (int i = 0; i < 3; i++) {
          for (int j = 0; j < 3; j++) {
            if (board[nextMove / 3 * 3 + i][nextMove % 3 * 3 + j] == ' ') board[nextMove / 3 * 3 + i][nextMove % 3 * 3 + j] = '*';
          }
        }
      }
      else {
        for (int i = 0; i < 9; i++) {
          for (int j = 0; j < 9; j++) {
            if (board[i][j] == ' ') board[i][j] = '*';
          }
        }
      }
      print();
      printGameState();
      if (nextMove != 9) {
        for (int i = 0; i < 3; i++) {
          for (int j = 0; j < 3; j++) {
            if (board[nextMove / 3 * 3 + i][nextMove % 3 * 3 + j] == '*') board[nextMove / 3 * 3 + i][nextMove % 3 * 3 + j] = ' ';
          }
        }
      }
      else {
        for (int i = 0; i < 9; i++) {
          for (int j = 0; j < 9; j++) {
            if (board[i][j] == '*') board[i][j] = ' ';
          }
        }
      }
      winner = move();
    }
    print();
    printGameState();
    if (winner == 'X')
      System.out.println("\nPlayer 1 (X) won!");
    else if (winner == 'O')
      System.out.println("\nPlayer 2 (O) won!");
    else if (winner == 'D')
      System.out.println("\nThe game ended in a draw!");
  }

  public void print() {
    System.out.println("\nMove " + moveNum);
    moveNum++;
    System.out.println("    0 1 2   3 4 5   6 7 8");
    for (int i = 0; i < 3; i++) {
      System.out.println("  - - - - - - - - - - - - -");
      for (int j = 0; j < 3; j++) {
        int row = 3 * i + j;
        System.out.print(row + " | ");
        for (int k = 0; k < 9; k++) {
          System.out.print(board[row][k] + " ");
          if ((k + 1) % 3 == 0) System.out.print("| ");
        }
        System.out.println();
      }
    }
    System.out.println("  - - - - - - - - - - - - -");
  }

  public void printGameState(){
    System.out.println("\nUltimate Board:");
    System.out.println("- - - - -");
    for(int a = 0; a < 3; a++){
      System.out.print("| ");
      for(int b = 0; b < 3; b++)
        System.out.print(gameState[a][b] + " ");
      System.out.println("|");
    }
    System.out.println("- - - - -");
  }

  public char move() {
    if (nextMove == 9) System.out.println("\nFree move!");
    else System.out.println("\nCurrent move at ultimate row "  + (nextMove / 3) + ", column " + (nextMove % 3)+ ". Please make your move in one of the squares marked with a *.");
    
    int x = 0, y = 0;
    boolean validMove = false;
    while (!validMove) {
      x = validateBounds("row");
      y = validateBounds("column");
      
      if (nextMove != 9) {
        if (x < nextMove / 3 * 3 || x > nextMove / 3 * 3 + 2) {
          System.out.println("Please make your move in the correct ultimate board. Your row must be between " + (nextMove / 3 * 3) + " and " + (nextMove / 3 * 3 + 2) + ".");
          continue;
        }
        if (y < nextMove % 3 * 3 || y > nextMove % 3 * 3 + 2) {
          System.out.println("Please make your move in the correct ultimate board. Your column must be between " + (nextMove % 3 * 3) + " and " + (nextMove % 3 * 3 + 2) + ".");
          continue;
        }
      }
      if (board[x][y] != ' ') {
        System.out.println("Enter the coordinates of an empty square!");
        continue;
      }
      if (gameState[x / 3][y / 3] != ' ') {
        System.out.println("Make your move in an incomplete board!");
        continue;
      }
      validMove = true;
    }

    if (player) board[x][y] = 'X';
    else board[x][y] = 'O';
    if (gameState[x % 3][y % 3] != ' ') nextMove = 9;
    else nextMove = x % 3 * 3 + y % 3;
    player = !player;

    // x / 3 * 3 is top row of current small grid, y / 3 * 3 is leftmost column of current small grid
    char game = checkWin(false, x, y);
    if (game != 'I') {
      gameState[x / 3][y / 3] = game;
      if (game == 'X')
        System.out.println("\nPlayer 1 (X) won in the ultimate board at row " + (x / 3) + ", column " + (y / 3) + "!");
      else if (game == 'Y')
        System.out.println("\nPlayer 2 (O) won in the ultimate board at row " + (x / 3) + ", column " + (y / 3) + "!");
      else if (game == 'D')
        System.out.println("\nThe ultimate board at row " + (x / 3) + ", column " + (y / 3) + " ended in a draw!");
      game = checkWin(true, 0, 0);
      if (nextMove == x % 3 * 3 + y % 3) nextMove = 9;
    }
    return game;
  }

  public char checkWin(boolean ultim, int row, int col) {
    char[][] temp = new char[3][3];
    if (ultim) {
      temp = gameState;
    }
    else {
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          temp[i][j] = board[row / 3 * 3 + i][col / 3 * 3 + j];
        }
      }
    }
    // check rows
    for (int i = 0; i < 3; i++) {
      if ((temp[0][i] == 'O' || temp[0][i] == 'X') && temp[0][i] == temp[1][i] && temp[1][i] == temp[2][i]) {
        return temp[0][i];
      }
    }
    // check columns
    for (int i = 0; i < 3; i++) { 
      if ((temp[i][0] == 'O' || temp[i][0] == 'X') && temp[i][0] == temp[i][1] && temp[i][1] == temp[i][2]) {
        return temp[i][0];
      }
    }
    // check diag
    if ((temp[1][1] == 'O' || temp[1][1] == 'X') && ((temp[0][0] == temp[1][1] && temp[1][1] == temp[2][2]) || (temp[0][2] == temp[1][1] && temp[1][1] == temp[2][0]))) {
      return temp[1][1];
    }
    // check if board is full (draw)
    boolean draw = true;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (temp[i][j] == ' ') {
          draw = false;
          break;
        }
      }
    }
    if (draw) return 'D';
    return 'I';
  }

  public int validateBounds(String which) {
    if (player) System.out.println("Player 1 (X), please enter the " + which + " of your move.");
    else System.out.println("Player 2 (O), please enter the " + which + " of your move.");
    do {
      String input = sc.next();
      try {
        int cur = Integer.parseInt(input);
        if (player) System.out.println("Player 1 (X) entered " + cur + " as the " + which + " of their move.");
        else System.out.println("Player 2 (O) entered " + cur + " as the " + which + " of their move.");
        if (cur < 0 || cur > 8) {
          System.out.println("Enter a " + which + " between 0 and 8!");
          continue;
        }
        return cur;
      }
      catch (Exception e) {
        System.out.println("Invalid input! >:( Please enter a number!");
      }
    } while (true);
  }
}
