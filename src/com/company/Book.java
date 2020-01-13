package com.company;

import java.util.List;

public class Book extends Base{

    private String isbn;
    private String title;
    private String author;
    private String year;
    private String genre;
    private String status;

    public Book() {
    }

    public Book(String isbn, String title, String author, String year, String genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.status = "Available";
    }
    public Book(String userInfoFromDisk) {
        String[] stringsInfo = userInfoFromDisk.split("\\r?\\n");
        for (String s : stringsInfo) {
            String trim = s.substring(s.indexOf(":") + 1).trim();
            if (s.contains("isbn :")) {
                this.isbn = trim;
            } else if (s.contains("title :")) {
                this.title = trim;
            } else if (s.contains("author :")) {
                this.author = trim;
            } else if (s.contains("year :")) {
                this.year = trim;
            } else if (s.contains("genre :")) {
                this.genre = trim;
            } else if (s.contains("status :")) {
                this.status = trim;
            }
        }
    }

    public Book(List<String> readFromFile) {
        int i = 0;
        String[] stringsInfo = new String [6];
        for(String content:readFromFile){
            stringsInfo[i] = content;
            i++;
        }
        this.isbn = stringsInfo[0].substring(7);
        this.title = stringsInfo[1].substring(8);
        this.author = stringsInfo[2].substring(9);
        this.year = stringsInfo[3].substring(7);
        this.genre = stringsInfo[4].substring(8);
        this.status = stringsInfo[5].substring(9);
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

    public void setStatus(String status){this.status = status;}

    @Override
    public String toString() {
        return "isbn: " + isbn + "\ntitle: " + title + "\nauthor: " + author +
                "\nyear: " + year + "\ncategory: " + genre + "\nstatus: "+status;

    }
}
