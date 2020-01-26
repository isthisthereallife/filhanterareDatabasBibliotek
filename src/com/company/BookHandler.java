package com.company;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class BookHandler {
    private Scanner scan = new Scanner(System.in);
    Library library;
    Check checker;

    public BookHandler(Library library) {
        this.library = library;
        this.checker = new Check(library);
    }

    String searchForBook(String whereToSearch) {
        System.out.println("Enter search: ");
        String search = scan.nextLine();
        return library.getActiveUser().searchInFile(search, whereToSearch).toLowerCase();
    }

    protected void borrowBook(Book bookToBorrow) {

        if (bookToBorrow.getQuantity() < 1) {
            System.out.println("Sorry, someone else has already borrowed that book!");
        } else {
            for (Card c : library.getCards()) {
                if (c.getCardNr().equals(library.getActiveUser().getCardNr())) {
                    library.setActiveCard(c);
                    break;
                }
            }
            bookToBorrow.setQuantity(bookToBorrow.getQuantity() - 1);
            String cardFileName = "database/cards/" + library.getActiveCard().getCardNr() + ".txt";
            String bookFileName = "database/books/" + bookToBorrow.getId() + ".txt";
            String cardLineToEdit = "activeLoans";
            String bookLineToEdit = "available";
            String bookNewLine = "Status: unavailable";
            bookToBorrow.editFile(bookFileName, bookLineToEdit, bookNewLine);
            library.getActiveCard().setActiveLoans(library.getActiveCard().getActiveLoans().concat(" " + bookToBorrow.getIsbn()));
            String cardNewLine = "activeLoans: " + library.getActiveCard().getActiveLoans();
            library.getActiveCard().editFile(cardFileName, cardLineToEdit, cardNewLine);
            System.out.println("You have borrowed: " + bookToBorrow.getTitle());
        }
    }

    protected void returnBook(Book bookToReturn) {
        for (Card c : library.getCards()) {
            if (c.getCardNr().equals(library.getActiveUser().getCardNr())) {
                library.setActiveCard(c);
                break;
            }
        }
        bookToReturn.setQuantity(bookToReturn.getQuantity() + 1);
        String cardFileName = "database/cards/" + library.getActiveUser().getCardNr() + ".txt";
        String bookFileName = "database/books/" + bookToReturn.getId() + ".txt";
        String cardLineToEdit = "activeLoans";
        String bookLineToEdit = "available";
        String bookNewLine = "Status: available";
        bookToReturn.editFile(bookFileName, bookLineToEdit, bookNewLine);
        library.getActiveCard().setActiveLoans(library.getActiveCard().getActiveLoans().replace(bookToReturn.getIsbn(), ""));

        String cardNewLine = "activeLoans: " + library.getActiveCard().getActiveLoans();
        library.getActiveCard().editFile(cardFileName, cardLineToEdit, cardNewLine);
        System.out.println("You have returned: " + bookToReturn.getTitle());
    }

    protected void deleteBook(Book aBook) {
        String authorsBooks = "";
        Path path = Paths.get("database/books/" + aBook.getId() + ".txt");
        library.getBooks().removeIf(book -> book.getIsbn().equals(aBook.getIsbn()));
        for (Author author : library.getAuthors()) {
            for (Book book : author.getBibliography()) {
                if (book.getIsbn().equals(aBook.getIsbn())) {
                    author.removeFromBibliography(book);
                    for (Book theBook : author.getBibliography()) {
                        authorsBooks = authorsBooks.concat(theBook.getIsbn() + " ");
                    }
                    author.editFile("database/authors/" + author.getAuthorId() + ".txt", "bibliography", "bibliography: " + authorsBooks);
                    break;
                }
            }
        }

        aBook.deleteFiles(path);
        System.out.println(aBook.getTitle() + " is now deleted.");
        return;
    }

    protected void editBook(Book bookToEdit) {
        boolean runningEditBook = true;
        boolean inputOk = false;
        do {
            String input = "";
            System.out.println("===========\nEdit what?\n1. Title\n2. Year\n0. Cancel\n===========\n");
            String edit = scan.nextLine();
            switch (edit) {
                case "1":
                    System.out.println("Current title: " + bookToEdit.getTitle());
                    System.out.println("New title:");
                    input = scan.nextLine();
                    bookToEdit.editFile("database/books/" + bookToEdit.getId() + ".txt", "title", "title: " + input);
                    bookToEdit.setTitle(input);
                    System.out.println("The title is now changed to " + bookToEdit.getTitle());
                    break;
                case "2":
                    do {
                        System.out.println("Current year: " + bookToEdit.getYear());
                        System.out.println("New year:");
                        input = scan.nextLine();
                        inputOk = checker.checkIfStringOfNumbers(input);
                    } while (inputOk);
                    bookToEdit.editFile("database/books/" + bookToEdit.getId() + ".txt", "year", "year: " + input);
                    bookToEdit.setYear(input);
                    System.out.println("The year is now changed to " + bookToEdit.getYear());
                    break;
                case "0":
                    runningEditBook = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again");
                    break;
            }
        } while (runningEditBook);
        return;
    }
}
