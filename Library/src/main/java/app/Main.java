package app;

import service.serviceImpl.BookService;

public class Main {
	public static void main(String[] args) {
		BookService service = new BookService();
		service.run();
	}
}
