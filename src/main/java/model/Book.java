package model;

public class Book {
    public final String isbn;
    public final String bookName;
    public final String author;
    public final String category;
    public final int publicationYear;


    public Book(String isbn, String bookName, String author, String category, int publicationYear) {
        this.isbn = isbn;
        this.bookName = bookName;
        this.author = author;
        this.category = category;
        this.publicationYear = publicationYear;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", publicationYear=" + publicationYear +
                '}';
    }
}
