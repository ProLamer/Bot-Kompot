package service.serviceImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dao.daoImpl.BookDao;
import model.BookModel;
import model.Menu;

public class BookService {
	List<BookModel> books;
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
				addBook(author, nameOfBook);
				break;

			case "remove":
				removeBook(nameOfBook, author);
				break;

			case "edit":
				editBook();
				break;

			case "all books":
				showAllBooks();
				break;

			case "help":
				menu.showHelpMenu();
				break;

			case "exit":
				scan.close();
				System.exit(0);
				break;

			default:
				System.out.println("Incorrect input, please input help for see documentation.");
				break;
			}
		}
	}

	private void showAllBooks() {
		try {
			books = bookDao.getAllBooks();
		} catch (InstantiationException | IllegalAccessException | SQLException e) {
			System.out.println("Error. Please try again...");
			e.printStackTrace();
		}
		for (BookModel book : books) {
			System.out.println(book.toString());
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

	private void addBook(String author, String nameOfBook) {
		if (author.isEmpty() || nameOfBook.isEmpty()) {
			System.out.println("Incorrect input. Current request is: add -a=your author -n=name of book");
		} else {
			book = initBookModel(author, nameOfBook);
			try {
				if (bookDao.addBook(book)) {
					System.out.println("Book " + author + " " + nameOfBook + " was added");
				} else {
					System.out.println("Error. Please try again...");
				}
			} catch (SQLException e) {
				System.out.println("Error. Please try again later...");
				e.printStackTrace();
			}
		}
	}

	private void removeBook(String nameOfBook, String author) {
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

	}

	private void editBook() {
		System.out.println("Choose id of book:");
		showAllBooks();
		String idStr = scan.nextLine();
		book.setIdBooks(idStr);

		System.out.print("Input new author: ");
		String newAuthor = scan.nextLine();
		book.setAuthor(newAuthor);

		System.out.print("Input new name: ");
		String newName = scan.nextLine();
		book.setNameOfBook(newName);

		try {
			if (bookDao.editBook(book)) {
				System.out.println("Book changed successfully.");
			} else {
				System.out.println("Error. Please try again...");
			}
		} catch (SQLException e) {
			System.out.println("Error. Please try again later...");
			e.printStackTrace();
		}

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
}
