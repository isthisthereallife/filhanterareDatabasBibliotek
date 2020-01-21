package com.company;

import java.util.List;
import java.util.Random;

public class Book extends Base {

    private String id;
    private String isbn;
    private String title;
    private String authorId;
    private String year;
    private String genre;
    private int quantity;

    public Book() {
    }

    public Book(String isbn, String title, String author, String year, String genre) {
        this.isbn = isbn;
        this.title = title;
        this.authorId = author;
        this.year = year;
        this.genre = genre;
        this.quantity = 1;
    }

    public Book(List<String> readFromFile, String fileName) {
        int i = 0;
        String[] stringsInfo = new String[7];
        String idFromFile = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lastIndexOf("."));
        for (String content : readFromFile) {
            String trim = content.substring(content.indexOf(":") + 1).trim();
            stringsInfo[i] = trim;
            i++;
        }
        this.id = idFromFile;
        this.isbn = stringsInfo[0];
        this.title = stringsInfo[1];
        this.authorId = stringsInfo[2];
        this.year = stringsInfo[3];
        this.genre = stringsInfo[4];
        this.quantity = 1;
    }

    /*userInfoFromDisk) {
        //String[] stringsInfo = userInfoFromDisk.split("\\r?\\n");

        for (String s : userInfoFromDisk) {
            s = s.toLowerCase();
            String trim = s.substring(s.indexOf(":") + 1).trim();
            if (s.contains("isbn:")) {
                this.isbn = trim;
            } else if (s.contains("title:")) {
                this.title = trim;
            } else if (s.contains("author:")) {
                this.author = trim;
            } else if (s.contains("year:")) {
                this.year = trim;
            } else if (s.contains("genre:")) {
                this.genre = trim;
            } else if (s.contains("status:")) {
                this.status = trim;
            }
        }
    }*/

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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String author) {
        this.authorId = author;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getId() {
        return this.id;
    }

    public String listToString() {

        return title + " by " + authorId + " (" + year + ") - ISBN: " + isbn;
    }

    private void idGenerator() {
        int leftLimit = 48; // ASCII vart 0 börjar
        int rightLimit = 122; // ASCII vart z slutar
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 97)) //ASCII räknar inte med tecken från 58 till 96
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        System.out.println(generatedString);
    }

    @Override
    public String toString() {
        return "isbn: " + isbn + "\ntitle: " + title + "\nauthor: " + authorId +
                "\nyear: " + year + "\ngenre: " + genre + "\nstatus: " + quantity;

    }
}
