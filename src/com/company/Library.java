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
    private User activeUser;
    boolean running = true;


    public Library() {
        load();
        identification();
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
                    activeUser = new User("admin", "admin", "admin", "admin");
                    adminMenu();
                    break;
                case "2":
                    userLoginMenu();
                    break;
                default:
                    System.out.println("Invalid choice. Try again!");
                    identification();
            }
        }while(running);
    }

    private void adminMenu() {
        activeUser = new User("admin", "admin", "admin", "admin");
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
                    addBook();
                    running = rerunPrompt();
                    break;
                case "2":
                    result = searchForBook("database/books");
                    searchResultChoiceMenu("delete", countOccurrences("isbn:", result), result);
                    running = rerunPrompt();
                    break;
                case "3":
                    result = searchForBook("database/books");
                    searchResultChoiceMenu("edit", countOccurrences("isbn:", result), result);
                    running = rerunPrompt();
                    break;
                case "4":
                    identification();
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again!");

            }

        } while (running);
    }

    private void userLoginMenu() {
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
                addUser();
                break;
            default:
                System.out.println("Invalid choice. Try again!");
                userLoginMenu();
        }
    }

    public void loginMenu() {
        boolean loggedIn = false;
        System.out.println("What is your login id? ");
        String loginId = scan.nextLine();
        for (User user : users) {

            if (user.getId().equalsIgnoreCase(loginId)) {
                System.out.println("Login successful!");
                activeUser = user;
                loggedIn = true;
                userMenu();
                break;
            }
        }
        if (!loggedIn) {
            System.out.println("Login failed.");
            userLoginMenu();
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


    private String searchForBook(String whereToSearch) {
        System.out.println("Enter search: ");
        String search = scan.nextLine();
        return activeUser.searchInFile(search, whereToSearch).toLowerCase();

    }

    private int countOccurrences(String searchFor, String searchIn) {
        return (searchIn.toLowerCase().split(Pattern.quote(searchFor.toLowerCase()), -1).length) - 1;
    }

    private void searchResultChoiceMenu(String operation, int nrOfSearchMatches, String resultOfSearch) {
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

    private boolean rerunPrompt() {
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
            if (name.length() < 1 || name.isBlank()){
                System.out.println("Your name must be at least one character!");
                inputOk = false;
            }
        } while (!inputOk);

        do {
            System.out.println("Your address:");
            address = scan.nextLine();
            if (address.matches("[0-9]+")){
                System.out.println("Your adress can not only contain numbers!");
                inputOk = false;
            }
            else if (address.length() < 1 || address.isBlank()) {
                System.out.println("Your adress must be at least one character!");
                inputOk = false;
            }
            else if (!address.matches(".*\\d.*")){
                System.out.println("There must be at least one number in your adress!");
                inputOk = false;
            } else {
                inputOk = true;
            }
        } while (!inputOk);

        do {
            System.out.println("Your postcode: ");
            String postCode = scan.nextLine();
            inputOk = checkIfStringOfNumbers(postCode);
            if (postCode.length() < 5 || postCode.isBlank()){
                System.out.println("Your postcode must be at least 5 digits!");
                inputOk = false;
            }
            else{
                address += " " + postCode;
            }
        } while (!inputOk);
        do {
            System.out.println("Enter your city: ");
            String city = scan.nextLine();
            inputOk = checkIfStringOfLetters(city);
            if (city.length() < 1 || city.isBlank()){
                System.out.println("Your city must be at least one letter!");
                inputOk = false;
            }
            else{
                address += " " + city;
            }
        } while (!inputOk);
        do {
            System.out.println("Your mail: ");
            mail = scan.nextLine();
            if (isValid(mail)){
                inputOk = true;
            }
            else{
                System.out.println("Your email adress must contain an @ character and a ., example: hello@hi.com");
                inputOk = false;
            }
        } while (!inputOk);
        do {
            System.out.println("Your telephone number:");
            tel = scan.nextLine();
            inputOk = checkIfStringOfNumbers(tel);
            if (tel.length() < 5 || tel.isBlank()){
                System.out.println("Your telephone number must be at least 5 digits!");
                inputOk = false;
            }
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
    private boolean isValid(String mail)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (mail == null)
            return false;
        return pat.matcher(mail).matches();
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
        adminMenu();


    }

    public void deleteBook(Book bok) {

        Path path = Paths.get("database/books/" + bok.getIsbn() + ".txt");
        books.removeIf(book -> book.getIsbn().equals(bok.getIsbn()));

        bok.deleteFiles(path);
        System.out.println(bok.getTitle() + " is now deleted.");
        adminMenu();
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
        adminMenu();
    }

}
