package com.company;

public class Book {

    private String isbn;
    private String title;
    private String author;
    private byte qty;
    private String year;
    private String genre;

    public Book() {
    }

    public Book(String isbn, String title, String author, byte qty, String year, String genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.qty = qty;
        this.year = year;
        this.genre = genre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public byte getQty() {
        return qty;
    }

    public void setQty(byte qty) {
        this.qty = qty;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "ISBN: " + isbn + "\nTitle: " + title + "\nAuthor: " + author +
                "\nQty: " + qty + "\nYear: " + year + "\nCategory: " + genre;

    }
}
