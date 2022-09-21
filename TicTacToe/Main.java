import java.util.Scanner;
import java.io.*;
class Main 
{
  public static void main(String[] args) throws FileNotFoundException 
  {
    // print intro
    System.out.println("****************************************");
    System.out.println("     Welcome to Ultimate TicTacToe!     ");
    System.out.println("****************************************\n");
    TicTacToe t; 
    // Uncomment below to test cases
    // Scanner sc = new Scanner(new File("tie.dat"));
    Scanner sc = new Scanner(System.in);
    t = new TicTacToe(sc);
    t.play();    
  }
}
