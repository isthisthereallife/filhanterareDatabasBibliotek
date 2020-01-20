package com.company;

import java.util.ArrayList;
import java.util.List;

public class Author extends Base {
    private String firstName;
    private String lastName;
    private String authorId;
    private ArrayList<Book> bibliography = new ArrayList<>();

    public Author(String firstName, String lastName, Book newBook) {
        this.firstName = firstName;
        this.lastName = lastName;
        setAuthorId();
        this.bibliography = new ArrayList<>();
        this.bibliography.add(newBook);

    }

    public void addToBibliography(Book newBook) {
        bibliography.add(newBook);
    }

    public ArrayList<Book> getBibliography() {
        return this.bibliography;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }


    public Author(List<String> authorInfoFromDisk, List<Book> bookList) {
        for (String s : authorInfoFromDisk) {
            String trim = s.substring(s.indexOf(":") + 1).trim();
            if (s.contains("first name:")) {
                this.firstName = trim;
            } else if (s.contains("last name:")) {
                this.lastName = trim;
            } else if (s.contains("authorId:")) {
                this.authorId = trim;
            } else if (s.contains("bibliography: ")) {
                String[] authorBookString = trim.split(" ");
                //för varje book så ska jag hitta objektet med sånt isbn
                for (String book : authorBookString) {
                    //kolla igenom bookList
                    for (Book item : bookList) {
                        if (item.getIsbn().equals(book)) {
                            //lägg in det objektet i this.bibliography
                            this.bibliography.add(item);
                            System.out.println("added: " + item.getTitle() + " to " + this.firstName + " " + this.lastName);
                        }
                    }
                }
            }
        }
    }

    public void setAuthorId() {
        if (this.authorId == null) {
            this.authorId = "";
            for (int i = 0; i < 5; i++) {
                this.authorId = this.authorId.concat(String.valueOf(new java.util.Random().nextInt(26) + 'a'));
                this.authorId = this.authorId.concat(String.valueOf(new java.util.Random().nextInt(10)));
            }
        }
    }

    public String getAuthorId() {
        return this.authorId;
    }

    public String toString() {
        String myBooks = "";
        for (Book bok : this.bibliography) {
            myBooks = myBooks.concat(bok.getIsbn() + " ");
        }
        return "first name: " + this.firstName + "\nlast name: " + this.lastName + "\nauthorId: " + this.authorId +
                "\nbibliography: " + myBooks;

    }

}
