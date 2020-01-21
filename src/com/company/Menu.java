package com.company;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    Scanner scan = new Scanner(System.in);
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Book> books = new ArrayList<>();
    private Base base = new Base();
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
        System.out.println("================================");


        String choice = scan.nextLine();

        do {
            switch (choice) {
                case "1":
                    adminMenu();
                    running = false;
                case "2":
                    userLoginMenu();
                    running = false;
                default:
                    System.out.println("Invalid choice. Try again!");

            }
        } while(running);
    }
    private void adminMenu() {
        library.setActiveUser(new User("admin", "admin", "admin", "admin"));
        do {
            System.out.println("=============================");
            System.out.println("What do you want to do?");
            System.out.println("1: Add book to library");
            System.out.println("2: Remove book from library");
            System.out.println("3: Edit book from library");
            System.out.println("4: Go back to login");
            System.out.println("5: Quit");
            System.out.println("=============================");

            String choice;
            String result;

            choice = scan.nextLine();

            switch (choice) {
                case "1":
                   library.addBook();
                   running = library.rerunPrompt();
                    break;
                case "2":
                   result = library.searchForBook("database/books");
                   library.searchResultChoiceMenu("delete", library.countOccurrences("isbn:", result), result);
                   running = library.rerunPrompt();
                    break;
                case "3":
                    result = library.searchForBook("database/books");
                    library.searchResultChoiceMenu("edit", library.countOccurrences("isbn:", result), result);
                    running = library.rerunPrompt();
                    break;
                case "4":
                    identification();
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again!");
                    break;
            }

        } while (running);
    }

    void userLoginMenu() {
        System.out.println("============");
        System.out.println("1: Login\n2: Register");
        System.out.println("============");
        String choice = "0";


        choice = scan.nextLine();


        switch (choice) {
            case "1":
                loginMenu();
                break;
            case "2":
                library.addUser();
                break;
            default:
                System.out.println("Invalid choice. Try again!");
                userLoginMenu();
        }
    }
    public void loginMenu() {
        System.out.println("What is your login id? ");
        library.setActiveUser(null);
        String loginId = scan.nextLine();
        for (User user : users) {

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
    private void userMenu() {
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


                    books.sort(Comparator.comparing(Book::getTitle));
                    for (Book book : books) {
                        if (book.getStatus().equals("Available"))
                            System.out.println(book.listToString());
                    }
                    System.out.println();

                    //söker efter böcker med "AVAILABLE" som text
                    //System.out.println(activeUser.searchInFile("available", "database/books"));
                    running = library.rerunPrompt();
                    break;
                }
                case "2": {
                    String result = library.searchForBook("database/books");
                    library.searchResultChoiceMenu("borrow", library.countOccurrences("isbn:", result), result);
                    running = library.rerunPrompt();
                    break;
                }
                case "3": {
                    String result = library.searchForBook("database/books");
                    library.searchResultChoiceMenu("return", library.countOccurrences("isbn:", result), result);
                    running = library.rerunPrompt();
                    break;
                }
                case "4": {
                    books.sort(Comparator.comparing(Book::getTitle));
                    for (Book book : books)
                        System.out.println(book.listToString());

                    System.out.println(" ");
                    running = library.rerunPrompt();
                    break;
                }
                case "5": {
                    System.out.println(library.getActiveUser().activeLoansInfo());
                    running = library.rerunPrompt();
                    break;
                }
                case "0":
                    running = false;
                    break;
                default:

            }
        } while (running);
    }

}
