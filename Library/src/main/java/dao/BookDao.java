package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import connection.ConnectionToDB;
import model.BookModel;

public class BookDao {
	private static final String ADD_BOOK_STATEMENT = "INSERT INTO books (nameOfBook, author) VALUES (?,?);";
	private static final String UPDATE_BOOK_STATEMENT = "UPDATE books SET nameOfBook=(?), author=(?) WHERE idBooks = (?);";
	private static final String REMOVE_BOOK_BY_ID_STATEMENT = "DELETE FROM books WHERE idBooks = (?)";
	private static final String REMOVE_BOOK_BY_NAME_STATEMENT = "DELETE FROM books WHERE nameOfBook  = (?)";
	private static final String SELECT_SAME_BOOK_STATEMENT = "SELECT * FROM books WHERE nameOfBook = (?)";
	private static final String GET_INFORMATION_STATEMENT = "SELECT * FROM books;";
	private Connection connection;
	Scanner scan = new Scanner(System.in);
	private PreparedStatement preparedStatement;

	ArrayList<Integer> idList = new ArrayList<>();
	int counter = 0;

	public boolean addBook(BookModel book) {
		connection = ConnectionToDB.getInstance();
		int inserted = 0;
		try {
			preparedStatement = connection.prepareStatement(ADD_BOOK_STATEMENT);
			preparedStatement.setString(1, book.getNameOfBook());
			preparedStatement.setString(2, book.getAuthor());
			inserted = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inserted > 0;
	}

	public boolean editBook(BookModel book) {
		connection = ConnectionToDB.getInstance();
		int inserted = 0;
		try {
			preparedStatement = connection.prepareStatement(UPDATE_BOOK_STATEMENT);
			preparedStatement.setString(1, book.getNameOfBook());
			preparedStatement.setString(2, book.getAuthor());
			preparedStatement.setInt(3, book.getIdBooks());
			inserted = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inserted > 0;
	}

	public boolean removeBook(BookModel book) {
		String sameBooks = checkSame(book.getNameOfBook());
		if (counter > 1) {
			System.out.println("we have few books with such name please choose one by typing a number of book:");
			System.out.println(sameBooks);
			int inputedId = Integer.parseInt(scan.nextLine());
			for (Integer integer : idList) {
				if (inputedId == integer) {
					return deleteBook(inputedId);
				}
			}
			counter = 0;
		} else {
			return deleteBook(book.getNameOfBook());
		}
		return false;
	}

	private boolean deleteBook(String nameOfBook) {
		int inserted = 0;
		connection = ConnectionToDB.getInstance();
		try {
			preparedStatement = connection.prepareStatement(REMOVE_BOOK_BY_NAME_STATEMENT);
			preparedStatement.setString(1, nameOfBook);
			inserted = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("incorect!");
		}
		return inserted > 0;
	}

	private boolean deleteBook(int inputedId) {
		int inserted = 0;
		connection = ConnectionToDB.getInstance();
		try {
			preparedStatement = connection.prepareStatement(REMOVE_BOOK_BY_ID_STATEMENT);
			preparedStatement.setInt(1, inputedId);
			inserted = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("incorect!");
		}
		return inserted > 0;
	}

	public String checkSame(String nameOfBook) {
		connection = ConnectionToDB.getInstance();
		int id;
		String author;
		String name;
		String result = "";
		try {
			preparedStatement = connection.prepareStatement(SELECT_SAME_BOOK_STATEMENT);
			preparedStatement.setString(1, nameOfBook);
			ResultSet setOfBooks = preparedStatement.executeQuery();
			while (setOfBooks.next()) {
				id = setOfBooks.getInt(1);
				idList.add(id);
				author = setOfBooks.getString(3);
				name = setOfBooks.getString(2);
				result += id + ". " + author + " " + name + ";\n";
				counter++;
			}

		} catch (SQLException e) {
			System.out.println("incorect!");
		}
		return result;
	}

	public List<String> getAllBooks() {
		List<String> listOfBooks = new ArrayList<>();
		int id;
		String author;
		String name;
		String result = "";
		connection = ConnectionToDB.getInstance();

		try {
			preparedStatement = connection.prepareStatement(GET_INFORMATION_STATEMENT);
			ResultSet setOfBooks = preparedStatement.executeQuery();
			while (setOfBooks.next()) {
				id = setOfBooks.getInt(1);
				author = setOfBooks.getString(3);
				name = setOfBooks.getString(2);
				result = id + ". " + author + " " + name;
				listOfBooks.add(result);
			}
		} catch (SQLException e) {
			System.out.println("Can not connect to database...	");
		}
		return listOfBooks;
	}
}
