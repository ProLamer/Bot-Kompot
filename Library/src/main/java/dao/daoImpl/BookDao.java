package dao.daoImpl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import connection.ConnectionToDB;
import dao.BookDaoInterface;
import model.BookModel;

public class BookDao implements BookDaoInterface {
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

	private <T> T getClass(Class<T> model, ResultSet rs)
			throws InstantiationException, IllegalAccessException, SQLException {

		T t = model.newInstance();

		Field[] fields = model.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			field.set(t, rs.getString(field.getName()));
		}
		return t;
	}

	@Override
	public boolean addBook(BookModel book) throws SQLException {
		connection = ConnectionToDB.getInstance();
		preparedStatement = connection.prepareStatement(ADD_BOOK_STATEMENT);
		preparedStatement.setString(1, book.getNameOfBook());
		preparedStatement.setString(2, book.getAuthor());

		return preparedStatement.executeUpdate() > 0;
	}

	@Override
	public boolean editBook(BookModel book) throws SQLException {
		connection = ConnectionToDB.getInstance();
		preparedStatement = connection.prepareStatement(UPDATE_BOOK_STATEMENT);
		preparedStatement.setString(1, book.getNameOfBook());
		preparedStatement.setString(2, book.getAuthor());
		preparedStatement.setString(3, book.getIdBooks());

		return preparedStatement.executeUpdate() > 0;
	}

	@Override
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
		boolean result = false;
		connection = ConnectionToDB.getInstance();
		try {
			preparedStatement = connection.prepareStatement(REMOVE_BOOK_BY_NAME_STATEMENT);
			preparedStatement.setString(1, nameOfBook);
			result = preparedStatement.execute();
		} catch (SQLException e) {
			System.out.println("incorect!");
		}
		return result;
	}

	private boolean deleteBook(int inputedId) {
		boolean result = false;
		connection = ConnectionToDB.getInstance();
		try {
			preparedStatement = connection.prepareStatement(REMOVE_BOOK_BY_ID_STATEMENT);
			preparedStatement.setInt(1, inputedId);
			result = preparedStatement.execute();
		} catch (SQLException e) {
			System.out.println("incorect!");
		}
		return result;
	}

	private String checkSame(String nameOfBook) {
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

	@Override
	public List<BookModel> getAllBooks() throws InstantiationException, IllegalAccessException, SQLException {
		List<BookModel> listOfBooks = new ArrayList<>();
		connection = ConnectionToDB.getInstance();
		preparedStatement = connection.prepareStatement(GET_INFORMATION_STATEMENT);
		ResultSet setOfBooks = preparedStatement.executeQuery();
		while (setOfBooks.next()) {
			BookModel newBook = (BookModel) getClass(model.BookModel.class, setOfBooks);
			listOfBooks.add(newBook);
		}
		return listOfBooks;
	}

}
