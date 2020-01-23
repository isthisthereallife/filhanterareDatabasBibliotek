package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Search {


    public Search(ArrayList<Book> books, ArrayList<Genre> genres, ArrayList<Author> authors) {
        searchMenu(books, genres, authors);
    }

    private void searchMenu(ArrayList<Book> books, ArrayList<Genre> genres, ArrayList<Author> authors) {
        boolean keepSearching;
        do {
            System.out.println("Search for: \n1. Title\n2. Author\n3. Year\n4. Genre\n5. All of the above");
            String choice = enterSearchString().trim();
            String searchString = "";
            boolean foundSomething = false;
            switch (choice) {
                case "1":
                    //Title
                    System.out.println("Please enter name of book to search for: ");
                    searchString = enterSearchString().trim();
                    for (Book book : books) {
                        if (book.getTitle().toLowerCase().contains(searchString.toLowerCase())) {
                            System.out.println(book.getTitle() + " by " + getAuthorNameFromBook(book, authors) + " (" + book.getYear() + ") ISBN: " + book.getIsbn() + " - Available copies: " + book.getQuantity() + " of " + book.getTotalQuantity());
                            foundSomething = true;
                        }
                    }
                    if (!foundSomething) {
                        System.out.println("Your search for \"" + searchString + "\" came up empty :(");
                    }
                    break;
                case "2":
                    System.out.println("Please enter name of author to search for: ");
                    searchString = enterSearchString().trim();
                    for (Book book : books) {
                        if (getAuthorNameFromBook(book, authors).toLowerCase().contains(searchString.toLowerCase())) {
                            System.out.println(book.getTitle() + " by " + getAuthorNameFromBook(book, authors) + " (" + book.getYear() + ") Genre: " + getGenreFromId(genres, book.getGenre()) + " ISBN: " + book.getIsbn() + " - Available copies: " + book.getQuantity() + " of " + book.getTotalQuantity());
                            foundSomething = true;
                        }
                    }
                    if (!foundSomething) {
                        System.out.println("Your search for \"" + searchString + "\" came up empty :(");
                    }
                    break;
                case "3":
                    System.out.println("Please enter year to search for: ");
                    searchString = enterSearchString().trim();
                    for (Book book : books) {
                        if (book.getYear().toLowerCase().contains(searchString.toLowerCase())) {
                            System.out.println(book.getTitle() + " by " + getAuthorNameFromBook(book, authors) + " (" + book.getYear() + ") Genre: " + getGenreFromId(genres, book.getGenre()) + " ISBN: " + book.getIsbn() + " - Available copies: " + book.getQuantity() + " of " + book.getTotalQuantity());
                            foundSomething = true;
                        }
                    }
                    if (!foundSomething) {
                        System.out.println("Your search for \"" + searchString + "\" came up empty :(");
                    }
                    break;
                case "4":
                    System.out.println("Please enter genre to search for: ");
                    searchString = enterSearchString().trim();
                    for (Book book : books) {
                        if (getGenreFromId(genres, book.getGenre()).toLowerCase().contains(searchString.toLowerCase())) {
                            System.out.println(book.getTitle() + " by " + getAuthorNameFromBook(book, authors) + " (" + book.getYear() + ") Genre: " + getGenreFromId(genres, book.getGenre()) + " ISBN: " + book.getIsbn() + " - Available copies: " + book.getQuantity() + " of " + book.getTotalQuantity());
                            foundSomething = true;
                        }
                    }
                    if (!foundSomething) {
                        System.out.println("Your search for \"" + searchString + "\" came up empty :(");
                    }
                    break;
                case "5":
                    System.out.println("Please text to search for: ");
                    searchString = enterSearchString().trim();
                    for (Book book : books) {
                        if (book.getTitle().toLowerCase().contains(searchString.toLowerCase()) || getGenreFromId(genres, book.getGenre()).toLowerCase().contains(searchString.toLowerCase()) || book.getYear().toLowerCase().contains(searchString.toLowerCase()) || getAuthorNameFromBook(book, authors).toLowerCase().contains(searchString.toLowerCase())) {
                            System.out.println(book.getTitle() + " by " + getAuthorNameFromBook(book, authors) + " (" + book.getYear() + ") Genre: " + getGenreFromId(genres, book.getGenre()) + " ISBN: " + book.getIsbn() + " - Available copies: " + book.getQuantity() + " of " + book.getTotalQuantity());
                            foundSomething = true;
                        }
                    }
                    if (!foundSomething) {
                        System.out.println("\nYour search for \"" + searchString + "\" came up empty :(");
                    }
                    break;

                default:
                    System.out.println("Invalid input.");
            }
            System.out.println("\nSearch again?\n1. Yes\n2. No");
            if (new Scanner(System.in).nextLine().equals("1")) {
                keepSearching = true;
            } else keepSearching = false;
        } while (keepSearching);
    }


    private String getGenreFromId(ArrayList<Genre> genre, String id) {
        for (Genre aGenre : genre) {
            if (aGenre.getId().equalsIgnoreCase(id)) {
                return aGenre.getName();
            }
        }
        return "There should be a genre here...";
    }

    private String getAuthorNameFromBook(Book book, ArrayList<Author> authors) {
        for (Author author : authors) {
            if (book.getAuthorId().equalsIgnoreCase(author.getAuthorId())) {
                return author.getFirstName() + " " + author.getLastName();
            }
        }
        return "There should be a name here...";
    }

    private String enterSearchString() {
        return new Scanner(System.in).nextLine();
    }
}
