package com.company;

import java.util.Comparator;
import java.util.Scanner;

public class Browse {
    private Scanner scan = new Scanner(System.in);
    Library library;
    Check checker;
    private boolean inputOk = false;

    public Browse(Library library) {
        this.library = library;
        this.checker = new Check(library);
    }

    protected void listBooks() {
        library.getBooks().sort(Comparator.comparing(Book::getTitle));
        for (Book book : library.getBooks())

            System.out.println(book.listToString(library.getAuthors()));
        System.out.println(" ");
    }

    protected void browseByAuthor() {
        do {
            int counter = 1;
            for (Author author : library.getAuthors()) {
                System.out.println("[" + counter + "]" + " " + author.getFirstName() + " " + author.getLastName());
                counter++;
            }
            int back = library.getAuthors().size() + 1;
            System.out.println("[" + back + "] Go back");
            System.out.println("Choose an author for books or go back");
            String authorChoice = scan.nextLine();
            inputOk = checker.checkIfStringOfNumbers(authorChoice);
            int choice = Integer.parseInt(authorChoice);
            if (choice < back) {
                for (int i = 0; i < library.getAuthors().size(); i++) {
                    if (choice - 1 == i) {
                        System.out.println("Books written by " + library.getAuthors().get(i).getFirstName() + " " + library.getAuthors().get(i).getLastName() + ":");
                        for (Book book : library.getAuthors().get(i).getBibliography()) {
                            System.out.println(book.getTitle() + " (" + book.getYear() + ") ISBN: " + book.getIsbn() + " - Available copies: " + book.getQuantity() + " of " + book.getTotalQuantity());
                        }
                        System.out.println("");
                        inputOk = false;
                    }
                }
            } else if (choice == counter) {
                inputOk = false;
            }
        } while (inputOk);
    }

    protected void browseByGenre() {
        do {
            int counter = 1;
            for (Genre genre : library.getGenres()) {
                System.out.println("[" + counter + "]" + " " + genre.getName() + " ");
                counter++;
            }
            int back = library.getGenres().size() + 1;
            System.out.println("[" + back + "] Go back");
            System.out.println("Choose a Genre or go back");
            String authorChoice = scan.nextLine();
            inputOk = checker.checkIfStringOfNumbers(authorChoice);
            int choice = Integer.parseInt(authorChoice);
            if (choice < back) {
                for (int i = 0; i < library.getGenres().size(); i++) {
                    if (choice - 1 == i) {
                        System.out.println("Books with genres " + library.getGenres().get(i).getName() + " " + ":");
                        for(Book book : library.getBooks()) {
                            if (book.getGenre().equals(library.getGenres().get(i).getId())){
                                System.out.println(book.listToString(library.getAuthors()));
                            }
                        }
                        System.out.println("");
                        inputOk = false;
                    }
                }
            } else if (choice == counter) {
                inputOk = false;
            }
        } while (inputOk);
    }
}
