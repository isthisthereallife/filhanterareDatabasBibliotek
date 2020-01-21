package com.company;

import javax.print.DocFlavor;
import javax.xml.xpath.XPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;
import java.util.regex.Pattern;

public class Library {
    Scanner scan = new Scanner(System.in);
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Book> books = new ArrayList<>();
    private Base base = new Base();
    private Menu menu;
    private User activeUser;
    boolean running = true;


    public Library() {
        load();
        menu = new Menu( this);
        menu.identification();
    }

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    private void load() {
        loadBooks();
        loadUsers();
    }

    private void loadBooks() {
        File folderPath = new File("database/books/");
        for (File file : base.readFromFolder(folderPath)) {
            final Path path = file.toPath();
            books.add(new Book(base.readFromFile(path)));
        }
    }

    private void loadUsers() {
        File folderPath = new File("database/users/");
        for (File file : base.readFromFolder(folderPath)) {
            final Path path = file.toPath();
            users.add(new User(base.readFromFile(path)));
        }
    }

    private void userMenu() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome " + activeUser.getName() + "\nPlease choose operation:");
        System.out.println();
        String number = "0";
        do {
            System.out.println("1: View available books");
            System.out.println("2: Borrow a book");
            System.out.println("3: Return a book");
            System.out.println("4: View all books in the library");
            if (!this.activeUser.activeLoansInfo().equals("")) {
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
                    running = rerunPrompt();
                    break;
                }
                case "2": {
                    String result = searchForBook("database/books");
                    searchResultChoiceMenu("borrow", countOccurrences("isbn:", result), result);
                    running = rerunPrompt();
                    break;
                }
                case "3": {
                    String result = searchForBook("database/books");
                    searchResultChoiceMenu("return", countOccurrences("isbn:", result), result);
                    running = rerunPrompt();
                    break;
                }
                case "4": {
                    books.sort(Comparator.comparing(Book::getTitle));
                    for (Book book : books)
                        System.out.println(book.listToString());

                    System.out.println(" ");
                    running = rerunPrompt();
                    break;
                }
                case "5": {
                    System.out.println(activeUser.activeLoansInfo());
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


    String searchForBook(String whereToSearch) {
        System.out.println("Enter search: ");
        String search = scan.nextLine();
        return activeUser.searchInFile(search, whereToSearch).toLowerCase();

    }

    int countOccurrences(String searchFor, String searchIn) {
        return (searchIn.toLowerCase().split(Pattern.quote(searchFor.toLowerCase()), -1).length) - 1;
    }

    public void searchResultChoiceMenu(String operation, int nrOfSearchMatches, String resultOfSearch) {
        if (nrOfSearchMatches < 1) {
            System.out.println("Your search came up empty.");
        } else if (nrOfSearchMatches > 1) {
            System.out.println("Your search was too general. Found " + nrOfSearchMatches + " books.");
        } else {
            String isbn = resultOfSearch.substring(resultOfSearch.indexOf("isbn:") + 5, resultOfSearch.indexOf("\n")).trim();

            for (Book book : books) {
                if (book.getIsbn().equals(isbn)) {
                    System.out.println(book.toString());
                    System.out.println("\nIs this the book? \n1. Yes!\n2. No, go back.");
                    String choice = scan.nextLine();
                    if (choice.equals("1")) {
                        if (operation.equals("borrow")) {
                            borrowBook(book);
                        } else if (operation.equals("return")) {
                            returnBook(book);
                        } else if (operation.equals("edit")) {
                            editBook(book);
                        } else if (operation.equals("delete")) {
                            deleteBook(book);
                        }
                    }
                }
            }
        }
    }

    private void borrowBook(Book bookToBorrow) {
        bookToBorrow.setStatus("Unavailable");
        String userFileName = "database/users/" + activeUser.getId() + ".txt";
        String bookFileName = "database/books/" + bookToBorrow.getIsbn() + ".txt";
        String bookLineToEdit = "available";
        String userLineToEdit = "activeLoans";
        String bookNewLine = "Status: unavailable";
        bookToBorrow.editFile(bookFileName, bookLineToEdit, bookNewLine);
        activeUser.setActiveLoans(activeUser.getActiveLoans().concat(" " + bookToBorrow.getIsbn()));
        String userNewLine = "activeLoans: " + activeUser.getActiveLoans();
        activeUser.editFile(userFileName, userLineToEdit, userNewLine);
    }

    private void returnBook(Book bookToReturn) {
        bookToReturn.setStatus("Available");
        String userFileName = "database/users/" + activeUser.getId() + ".txt";
        String bookFileName = "database/books/" + bookToReturn.getIsbn() + ".txt";
        String bookLineToEdit = "available";
        String userLineToEdit = "activeLoans";
        String bookNewLine = "Status: available";
        bookToReturn.editFile(bookFileName, bookLineToEdit, bookNewLine);
        //ta bort en rad från user
        activeUser.setActiveLoans(activeUser.getActiveLoans().replace(bookToReturn.getIsbn(), ""));
        //concat(" " + bookToReturn.getIsbn()));

        String userNewLine = "activeLoans: " + activeUser.getActiveLoans();
        activeUser.editFile(userFileName, userLineToEdit, userNewLine);
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


    public void addUser() {
        String name = " ";
        String address = " ";
        String mail = " ";
        String tel = " ";

        boolean inputOk = false;
        do {
            System.out.println("Your name:");
            name = scan.nextLine();
            inputOk = checkIfStringOfLetters(name);
        } while (!inputOk);

        System.out.println("Your address:");
        address = scan.nextLine();
        System.out.println("Your mail: ");
        mail = scan.nextLine();
        do {
            System.out.println("Your telephone number:");
            inputOk = checkIfStringOfNumbers(scan.nextLine());
        } while (!inputOk);

        users.add(new User(name, address, mail, tel));
        activeUser = users.get(users.size() - 1);
        String uniqueId = users.get(users.size() - 1).getId();
        users.get(users.size() - 1).writeToFile(("database/users/" + uniqueId), (users.get(users.size() - 1).toString()));
        System.out.printf("Registration complete!\nYour unique id is: %s\n", uniqueId);
        userMenu();
    }

    private boolean checkIfStringOfNumbers(String stringToCheck) {
        stringToCheck = stringToCheck.replace(" ","");
        Character[] charList = new Character[stringToCheck.length()];
        for (int i = 0; i < stringToCheck.length(); i++) {
            charList[i] = stringToCheck.charAt(i);
        }
        for (Character c : charList) {
            if (!Character.isDigit(c)) {
                System.out.println("Please enter only numbers.");
                return false;
            }
        }
        return true;
    }

    private boolean checkIfStringOfLetters(String stringToCheck) {
        stringToCheck = stringToCheck.replace(" ", "");
        stringToCheck = stringToCheck.replace(".", "");
        stringToCheck = stringToCheck.replace("-", "");
        Character[] charList = new Character[stringToCheck.length()];
        for (int i = 0; i < stringToCheck.length(); i++) {
            charList[i] = stringToCheck.charAt(i);
        }
        for (Character c : charList) {
            if (!Character.isLetter(c)) {
                System.out.println("Please enter only letters.");
                return false;
            }
        }
        return true;
    }

    private boolean checkForDuplicates(String stringToCheck) {
        Path path = Paths.get("database/books/" + stringToCheck + ".txt");
        if (Files.exists(path)) {
            System.out.println("Book already in library.");
            return false;
        } else return true;
    }

    public void addBook() {
        boolean inputOk = false;
        String isbn;
        String title;
        String author;
        String year;
        String genre;
        do {
            System.out.println("ISBN: ");
            isbn = scan.nextLine();
            inputOk = checkIfStringOfNumbers(isbn);
            if (inputOk) {
                inputOk = checkForDuplicates(isbn);

            }
        } while (!inputOk);

        System.out.println("Title: ");
        title = scan.nextLine();

        do {
            System.out.println("Author: ");
            author = scan.nextLine();
            inputOk = checkIfStringOfLetters(author);
        } while (!inputOk);
        do {
            System.out.println("Year: ");
            year = scan.nextLine();
            inputOk = checkIfStringOfNumbers(year);
        } while (!inputOk);

        do {
            System.out.println("Genre: ");
            genre = scan.nextLine();
            inputOk = checkIfStringOfLetters(genre);
        } while (!inputOk);

        books.add(new Book(isbn, title, author, year, genre));
        books.get(books.size() - 1).writeToFile(("database/books/" + isbn), (books.get(books.size() - 1).toString()));
        System.out.println("Book added to the library!");
        //adminMenu();


    }

    public void deleteBook(Book bok) {

        Path path = Paths.get("database/books/" + bok.getIsbn() + ".txt");
        books.removeIf(book -> book.getIsbn().equals(bok.getIsbn()));

        bok.deleteFiles(path);
        System.out.println(bok.getTitle() + " is now deleted.");
        //adminMenu();
    }

    public void editBook(Book bookToEdit) {
        boolean runningEditBook = true;
        boolean inputOk = false;
        do {
            String input = "";
            System.out.println("===========\nEdit what?\n1. Title\n2. Author\n3. Year\n4. Genre\n0. Cancel\n===========\n");
            String edit = scan.nextLine();
            switch (edit) {
                case "1":
                    System.out.println("Current title: " + bookToEdit.getTitle());
                    System.out.println("New title:");
                    input = scan.nextLine();
                    bookToEdit.editFile("database/books/" + bookToEdit.getIsbn() + ".txt", "title", "title: " + input);
                    bookToEdit.setTitle(input);
                    System.out.println("The title is now changed to " + bookToEdit.getTitle());
                    break;
                case "2":
                    do {
                        System.out.println("Current author: " + bookToEdit.getAuthor());
                        System.out.println("New author:");
                        input = scan.nextLine();
                        inputOk = checkIfStringOfLetters(input);
                    } while (!inputOk);
                    bookToEdit.editFile("database/books/" + bookToEdit.getIsbn() + ".txt", "author", "author: " + input);
                    bookToEdit.setAuthor(input);
                    System.out.println("The author is now changed to " + bookToEdit.getAuthor());
                    break;
                case "3":
                    do {
                        System.out.println("Current year: " + bookToEdit.getYear());
                        System.out.println("New year:");
                        input = scan.nextLine();
                        inputOk = checkIfStringOfNumbers(input);
                    } while (!inputOk);
                    bookToEdit.editFile("database/books/" + bookToEdit.getIsbn() + ".txt", "year", "year: " + input);
                    bookToEdit.setYear(input);
                    System.out.println("The year is now changed to " + bookToEdit.getYear());
                    break;
                case "4":
                    do {
                        System.out.println("Current genre: " + bookToEdit.getGenre());
                        System.out.println("Edit genre:");
                        input = scan.nextLine();
                        inputOk = checkIfStringOfLetters(input);
                    } while (!inputOk);
                    bookToEdit.editFile("database/books/" + bookToEdit.getIsbn() + ".txt", "genre", "genre: " + input);
                    bookToEdit.setGenre(input);
                    System.out.println("The genre is now changed to " + bookToEdit.getGenre());
                    break;
                case "0":
                    runningEditBook = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again");
                    break;
            }
        } while (runningEditBook);
        //adminMenu();
    }

}
