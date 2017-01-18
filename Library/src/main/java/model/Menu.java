package model;

import java.util.Scanner;

public class Menu {
	private String choice;
	Scanner scan = new Scanner(System.in);
	public String getChoice() {
		return choice;
	}

	public void showMenu(){
		System.out.println("----------------------------------------");
		System.out.println("Select action:");
		System.out.println("1. add book;");
		System.out.println("2. remove book;");
		System.out.println("3. edit book;");
		System.out.println("4. all books;");
		System.out.println("5. help;");
		System.out.println("----------------------------------------");
	}
	
	public void choiceOfClient(){
		showMenu();
		System.out.print("Your choice: ");
		choice = scan.nextLine();
	}
	
	public void showHelpMenu(){
		System.out.println("help menu");
	}
}
