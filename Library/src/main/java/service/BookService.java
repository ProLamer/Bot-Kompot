package service;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dao.BookDao;
import model.BookModel;
import model.Menu;

public class BookService {
	List<String> books;
	BookDao bookDao;
	Menu menu;
	BookModel book;
	Scanner scan;

	public BookService() {
		book = new BookModel();
		bookDao = new BookDao();
		menu = new Menu();
		scan = new Scanner(System.in);
	}

	private BookModel initBookModel(String author, String nameOfBook) {
		BookModel bookP = new BookModel();
		bookP.setAuthor(author);
		bookP.setNameOfBook(nameOfBook);
		return bookP;
	}

	public void run() {
		String inputedString = "";
		String command = "";
		String author = "";
		String nameOfBook = "";
		while (true) {
			menu.choiceOfClient();
			inputedString = menu.getChoice();

			if (inputedString.equals("all books") || inputedString.equals("help")) {
				command = inputedString;
			} else {
				command = findCommand(inputedString);
				author = findAuthor(inputedString);
				nameOfBook = findNameOfBook(inputedString);
			}

			switch (command) {
			case "add":
				if (author.isEmpty() || nameOfBook.isEmpty()) {
					System.out.println("Incorrect input. Current request is: add -a=your author -n=name of book");
				} else {
					book = initBookModel(author, nameOfBook);
					if (bookDao.addBook(book)) {
						System.out.println("Book " + author + " " + nameOfBook + " was added");
					} else {
						System.out.println("Error. Please try again...");
					}
				}
				break;

			case "remove":
				books = bookDao.getAllBooks();
				if (nameOfBook.isEmpty()) {
					System.out.println("Input name of book. Our books:");
					showAllBooks();
					System.out.print("Name of book = ");
					nameOfBook = scan.nextLine();
					if (nameOfBook.isEmpty()) {
						System.out.println("Incorrect input.");
					} else {
						book.setNameOfBook(nameOfBook);
					}

					if (bookDao.removeBook(book)) {
						System.out.println("Book " + author + " " + nameOfBook + " was removed");
					} else {
						System.out.println("Error. Please try again...");
					}

				} else {
					book.setNameOfBook(nameOfBook);

					if (bookDao.removeBook(book)) {
						System.out.println("Book " + author + " " + nameOfBook + " was removed");
					} else {
						System.out.println("Error. Please try again...");
					}
				}
				break;

			case "edit":
				books = bookDao.getAllBooks();
				System.out.println("Choose id of book:");
				showAllBooks();
				String idStr = scan.nextLine();
				int id = correctParseToInt(idStr);
				book.setIdBooks(id);

				System.out.print("Input new author: ");
				String newAuthor = scan.nextLine();
				book.setAuthor(newAuthor);

				System.out.print("Input new name: ");
				String newName = scan.nextLine();
				book.setNameOfBook(newName);

				if (bookDao.editBook(book)) {
					System.out.println("Book changed successfully.");
				} else {
					System.out.println("Error. Please try again...");
				}
				break;

			case "all books":
				books = bookDao.getAllBooks();
				showAllBooks();
				break;

			case "help":
				menu.showHelpMenu();
				break;

			default:
				System.out.println("Incorrect input, please input help for see documentation.");
				break;
			}
		}
	}

	private void showAllBooks() {
		for (String book : books) {
			System.out.println(book);
		}
	}

	private String findNameOfBook(String inputedString) {
		String result = "";
		Pattern p = Pattern.compile("-n=(.*?) ?(-a|$)");
		Matcher m = p.matcher(inputedString);

		if (m.find()) {
			result = m.group(1);
			return result;
		}

		return result;
	}

	private String findCommand(String inputedString) {
		String result = "";
		Pattern p = Pattern.compile(" ?(.*?)( |-a|-n|$)");
		Matcher m = p.matcher(inputedString);

		if (m.find()) {
			result = m.group(1);
			return result;
		}

		return result;
	}

	private String findAuthor(String input) {
		String result = "";
		Pattern p = Pattern.compile("-a=(.*?) ?(-n|$)");
		Matcher m = p.matcher(input);

		if (m.find()) {
			result = m.group(1);
			return result;
		}

		return result;
	}

	private int correctParseToInt(String id) {
		int idInt = 0;
		try {
			idInt = Integer.parseInt(id);
			return idInt;
		} catch (Exception e) {
			System.out.println("incorect input!");
		}
		return idInt;
	}
}
