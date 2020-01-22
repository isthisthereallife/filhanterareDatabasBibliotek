package com.company;

import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    Scanner scan = new Scanner(System.in);
    Library library;
    boolean running = true;

    public Menu(Library library) {
        this.library = library;
    }

    public void identification() {

        System.out.println("================================");
        System.out.println("Who would you like to login as?");
        System.out.println("1: Admin");
        System.out.println("2: User");
        System.out.println("3: Quit");
        System.out.println("================================");

        String choice = scan.nextLine();

        switch (choice) {
            case "1":
                adminMenu();
                running = false;
            case "2":
                userLoginMenu();
                running = false;
            case "3":
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Try again!");
                identification();
                break;
        }
    }

    void adminMenu() {
        library.setActiveUser(new User("admin", "admin", "admin", "admin"));
        do {
            System.out.println("=============================");
            System.out.println("What do you want to do?");
            System.out.println("1: Add book to library");
            System.out.println("2: Remove book from library");
            System.out.println("3: Edit book from library");
            System.out.println("4: Go back to login");
            // System.out.println("5: Quit");
            System.out.println("=============================");

            String choice;
            String result;

            choice = scan.nextLine();

            switch (choice) {
                case "1":
                    library.addBook();
                    running = rerunPrompt();
                    break;
                case "2":
                    result = library.searchForBook("database/books");
                    library.searchResultChoiceMenu("delete", library.countOccurrences("isbn:", result), result);
                    running = rerunPrompt();
                    break;
                case "3":
                    result = library.searchForBook("database/books");
                    library.searchResultChoiceMenu("edit", library.countOccurrences("isbn:", result), result);
                    running = rerunPrompt();
                    break;
                case "4":
                    identification();
                    break;
                default:
                    System.out.println("Invalid choice. Try again!");
                    break;
            }
        } while (running);
    }

    void userLoginMenu() {

        System.out.println("=========================");
        System.out.println("1: Login\n2: Register\n3: Go back to main menu");
        System.out.println("=========================");

        String choice;
        choice = scan.nextLine();

        switch (choice) {
            case "1":
                loginMenu();
                break;
            case "2":
                library.addUser();
                break;
            case "3":
                identification();
            default:
                System.out.println("Invalid choice. Try again!");
                userLoginMenu();
        }
    }

    public void loginMenu() {
        System.out.println("What is your login id? ");
        library.setActiveUser(null);
        String loginId = scan.nextLine();
        for (User user : library.getUsers()) {

            if (user.getId().equalsIgnoreCase(loginId)) {
                System.out.println("Login successful!");
                library.setActiveUser(user);
                userMenu();
                break;
            }
        }
        if (library.getActiveUser() == null) {
            System.out.println("Login failed.");
            userLoginMenu();
        }
    }

    void userMenu() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Welcome " + library.getActiveUser().getName() + "\nPlease choose operation:");
        System.out.println();
        String number = "0";
        do {
            System.out.println("1: View available books");
            System.out.println("2: Borrow a book");
            System.out.println("3: Return a book");
            System.out.println("4: View all books in the library");
            if (!this.library.getActiveUser().activeLoansInfo().equals("")) {
                System.out.println("5: View active loans");
            }
            System.out.println("0: Exit");

            try {
                number = scan.nextLine();
            } catch (InputMismatchException e) {
                userMenu();
            }
            switch (number) {
                case "1": {
                    library.getBooks().sort(Comparator.comparing(Book::getTitle));
                    for (Book book : library.getBooks()) {
                        if (book.getQuantity() > 0) ;
                        System.out.println(book.listToString(library.getAuthors()));
                    }
                    System.out.println();
                    running = rerunPrompt();
                    break;
                }
                case "2": {
                    String result = library.searchForBook("database/books");
                    library.searchResultChoiceMenu("borrow", library.countOccurrences("isbn:", result), result);
                    running = rerunPrompt();
                    break;
                }
                case "3": {
                    String result = library.searchForBook("database/books");
                    library.searchResultChoiceMenu("return", library.countOccurrences("isbn:", result), result);
                    running = rerunPrompt();
                    break;
                }
                case "4": {
                    library.getBooks().sort(Comparator.comparing(Book::getTitle));
                    for (Book book : library.getBooks())
                        System.out.println(book.listToString(library.getAuthors()));

                    System.out.println(" ");
                    running = rerunPrompt();
                    break;
                }
                case "5": {
                    System.out.println(library.getActiveUser().activeLoansInfo());
                    running = rerunPrompt();
                    break;
                }
                case "0":
                    running = false;
                    break;
                default:
            }
        } while (running);
    }

    boolean rerunPrompt() {
        int choice = 0;
        do {
            System.out.println("\n1. Back to main menu");
            System.out.println("2. Quit");
            try {
                choice = new java.util.Scanner(System.in).nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter 1 or 2.");
            }
            if (choice == 1) return true;
            if (choice == 2) return false;
        } while (true);
    }
}
