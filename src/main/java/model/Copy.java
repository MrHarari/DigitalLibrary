package model;

import java.io.Serializable;

public class Copy implements Serializable {
    public final Book book;
    public final String copyCode;
    public final boolean loaned;

    public Copy(Book book, String copyCode, boolean loaned) {
        this.book = book;
        this.copyCode = copyCode;
        this.loaned = loaned;
    }

    @Override
    public String toString() {
        return "Copy{" +
                "isbn='" + book + '\'' +
                ", copyCode='" + copyCode + '\'' +
                ", loaned=" + loaned +
                '}';
    }
}