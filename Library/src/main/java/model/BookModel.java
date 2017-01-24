package model;

public class BookModel {
	private String idBooks;
	private String nameOfBook;
	private String author;

	public String getIdBooks() {
		return idBooks;
	}

	public void setIdBooks(String idBooks) {
		this.idBooks = idBooks;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((idBooks == null) ? 0 : idBooks.hashCode());
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
		if (idBooks == null) {
			if (other.idBooks != null)
				return false;
		} else if (!idBooks.equals(other.idBooks))
			return false;
		if (nameOfBook == null) {
			if (other.nameOfBook != null)
				return false;
		} else if (!nameOfBook.equals(other.nameOfBook))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return idBooks + ". Author - " + author + ", Name - " + nameOfBook;
	}
}
