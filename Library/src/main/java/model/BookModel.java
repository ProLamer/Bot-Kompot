package model;

public class BookModel {
	private String nameOfBook;
	private String author;
	private int idBooks;
	public String getNameOfBook() {
		return nameOfBook;
	}
	public void setNameOfBook(String nameOfBook) {
		this.nameOfBook = nameOfBook;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getIdBooks() {
		return idBooks;
	}
	public void setIdBooks(int idBooks) {
		this.idBooks = idBooks;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + idBooks;
		result = prime * result + ((nameOfBook == null) ? 0 : nameOfBook.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookModel other = (BookModel) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (idBooks != other.idBooks)
			return false;
		if (nameOfBook == null) {
			if (other.nameOfBook != null)
				return false;
		} else if (!nameOfBook.equals(other.nameOfBook))
			return false;
		return true;
	}
	
	
}
