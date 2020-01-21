package com.company;

import javax.print.DocFlavor;
import javax.xml.xpath.XPath;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Library {
    Scanner scan = new Scanner(System.in);
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Author> authors = new ArrayList<>();
    private ArrayList<Genre> genres = new ArrayList<>();
    private Base base = new Base();
    private User activeUser;
    boolean running = true;


    public Library() throws IOException {
        load();
        identification();
    }

    private void load() throws IOException {
        loadBooks();
        loadUsers();
        loadAuthors();
        loadGenres();
        updateBooks();
        //createGenres();
        /*convertAuthorStringsToIds(books,authors);
        convertGenreStringsToIds(books, genres);
        createAuthors();*/
        loadBorrowedBooks();
    }

    public void createAuthors() {
        for (Author aut : authors) {
            aut.writeToFile("database/authors/" + aut.getAuthorId(), aut.toString());
        }
    }

    public void createGenres() {
        for (Genre genre : genres) {
            new Book().writeToFile("database/genres/" + genre.getId(), genre.toString());
        }
    }

    public void updateBooks() {
        for (Book aBook : books) {
            aBook.editFile("database/books/" + aBook.getId() + ".txt", "genre:", "genre: " + aBook.getGenre());

        }
    }

    //ändra i listorna. båda. lägg till nya objekt i genrelist för alla genrer som inte hittas där
    //skapa ett id och skriv över genren i booklist för alla böcker
    public void convertGenreStringsToIds(List<Book> booklist, List<Genre> genrelist) {
        for (Book aBook : booklist) {
            boolean foundGenre = false;
            String genre = aBook.getGenre();
            for (Genre aGenre : genrelist) {
                if (aGenre.getName().equalsIgnoreCase(genre)) {
                    //skriv över bokens genre till genrens id
                    aBook.setGenre(aGenre.getId());
                    foundGenre = true;
                }
            }
            if (!foundGenre) {
                genrelist.add(new Genre(aBook.getGenre()));
                aBook.setGenre(genrelist.get(genrelist.size() - 1).getId());
            }
        }
    }

    public void convertAuthorStringsToIds(List<Book> booklist, List<Author> authorlist) {
        for (Book aBook : booklist) {
            boolean foundAuthor = false;
            String author = aBook.getAuthorId();
            for (Author anAuthor : authorlist) {
                if (anAuthor.getFirstName().concat(" " + anAuthor.getLastName()).equalsIgnoreCase(author)) {
                    anAuthor.addToBibliography(aBook);
                    aBook.setAuthorId(anAuthor.getAuthorId());
                    foundAuthor = true;
                }
            }
            if (author.contains(" ") && !foundAuthor) {
                ArrayList<String> name = new ArrayList<>(List.of(author.split(" ")));
                var fname = name.remove(0);
                for (int i = 1; i < name.size(); i++) {
                    name.set(0, name.get(0).concat(" " + name.get(i)));
                }
                authorlist.add(new Author(fname, name.get(0), aBook));
                aBook.setAuthorId(authorlist.get(authorlist.size() - 1).getAuthorId());
            } else if (!foundAuthor) {
                authorlist.add(new Author(author, "", aBook));
                aBook.setAuthorId(authorlist.get(authorlist.size() - 1).getAuthorId());
            }
        }
    }

    private void loadBooks() throws IOException {
        File folderPath = new File("database/books/");
        String isbn = "";
        for (File file : base.readFromFolder(folderPath)) {
            BufferedReader brTest = new BufferedReader(new FileReader(file));
            String firstLine = brTest.readLine();
            final Path path = file.toPath();
            String fileName = String.valueOf(path);
            books.add(new Book(base.readFromFile(path), fileName));
            String isbnFromFile = firstLine.substring(firstLine.indexOf(":") + 1).trim();
            if (isbn.contains(isbnFromFile)) {
                books.remove(books.size() - 1);
                for (Book book : books) {
                    if (book.getIsbn().equals(isbnFromFile)) {
                        book.setQuantity(book.getQuantity() + 1);
                        book.setTotalQuantity(book.getTotalQuantity() + 1);
                    }
                }
            }
            isbn = isbn.concat(" " + isbnFromFile);
        }
    }

    private void loadUsers() {
        File folderPath = new File("database/users/");
        for (File file : base.readFromFolder(folderPath)) {
            final Path path = file.toPath();
            users.add(new User(base.readFromFile(path)));
        }
    }

    private void loadAuthors() {
        File folderPath = new File("database/authors/");
        for (File file : base.readFromFolder(folderPath)) {
            final Path path = file.toPath();
            authors.add(new Author(base.readFromFile(path), books));
        }
    }

    private void loadGenres() {
        File folderPath = new File("database/genres/");
        for (File file : base.readFromFolder(folderPath)) {
            final Path path = file.toPath();
            genres.add(new Genre(base.readFromFile(path)));
        }
    }

    private void loadBorrowedBooks() {
        String loans = "";
        for (User user : users) {
            if (user.getActiveLoans() != null || !user.getActiveLoans().trim().isEmpty())
                loans = loans.trim().concat(" " + user.getActiveLoans());
        }
        for (Book book : books) {
            if (loans.contains(book.getIsbn()))
                book.setQuantity(book.getQuantity() - 1);
        }
    }

    public void identification() {

        do {
            System.out.println("================================\nWelcome to the library!\nWho would you like to login as?\n1: Admin\n2: User\n================================");
            String choice = scan.nextLine();
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
        } while (running);
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
                    adminChoiceMenu();
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
    private void adminChoiceMenu(){
        System.out.println("1: New book");
        System.out.println("2: Show list of existing authors");
        String adminChoice = scan.nextLine();
        switch (adminChoice){
            case "1":
                addBook();
                break;
            case "2":
                int counter = 1;
                for (Author author : authors){
                    System.out.println(counter + " " + author.getFirstName() + " " + author.getLastName());
                }
        }
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
                    System.out.println("Sort by author?\n1. Yes\n2. No");
                    number = scan.nextLine();
                    if (number.equals("1")) {
                        System.out.println("Authors in library: ");
                        for (Author auth : authors) {
                            System.out.println(auth.getFirstName() + " " + auth.getLastName());
                            for (Book aBook : auth.getBibliography()) {
                                System.out.println(aBook.getTitle());
                            }
                            System.out.println();
                        }
                    } else {
                        books.sort(Comparator.comparing(Book::getTitle));
                        for (Book book : books) {
                            if (book.getQuantity() > 0)
                                System.out.println(book.listToString(authors));
                        }
                    }
                    System.out.println();
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
                    listBooks();
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

    private void listBooks() {
        books.sort(Comparator.comparing(Book::getTitle));
        for (Book book : books)
            System.out.println(book.listToString(authors));

        System.out.println(" ");
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
                            System.out.println("Do you want it?\n1. Yes\n2. No");
                            if (scan.nextLine().equals("1")) {
                                borrowBook(book);
                                System.out.println("You have borrowed "+book.toString());
                            }
                            else System.out.println("Never mind, then.");
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
        int newQuantity = bookToBorrow.getQuantity() - 1;
        bookToBorrow.setQuantity(newQuantity);
        String userFileName = "database/users/" + activeUser.getId() + ".txt";
        String bookFileName = "database/books/" + bookToBorrow.getId() + ".txt";
        String bookLineToEdit = "available";
        String userLineToEdit = "activeLoans";
        String bookNewLine = "Status: unavailable";
        bookToBorrow.editFile(bookFileName, bookLineToEdit, bookNewLine);
        activeUser.setActiveLoans(activeUser.getActiveLoans().concat(" " + bookToBorrow.getIsbn()));
        String userNewLine = "activeLoans: " + activeUser.getActiveLoans();
        activeUser.editFile(userFileName, userLineToEdit, userNewLine);
    }

    private void returnBook(Book bookToReturn) {
        int newQuantity = bookToReturn.getQuantity() - 1;
        bookToReturn.setQuantity(newQuantity);
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
            if (name.length() < 1 || name.isBlank()) {
                System.out.println("Your name must be at least one character!");
                inputOk = false;
            }
        } while (!inputOk);

        do {
            System.out.println("Your address:");
            address = scan.nextLine();
            if (address.matches("[0-9]+")) {
                System.out.println("Your adress can not only contain numbers!");
                inputOk = false;
            } else if (address.length() < 1 || address.isBlank()) {
                System.out.println("Your adress must be at least one character!");
                inputOk = false;
            } else if (!address.matches(".*\\d.*")) {
                System.out.println("There must be at least one number in your adress!");
                inputOk = false;
            } else {
                inputOk = true;
            }
        } while (!inputOk);

        do {
            System.out.println("Your zipcode: ");
            String zipCode = scan.nextLine();
            inputOk = checkIfStringOfNumbers(zipCode);
            if (zipCode.length() < 5 || zipCode.isBlank()) {
                System.out.println("Your zipCode must be at least 5 digits!");
                inputOk = false;
            } else {
                address += " " + zipCode;
            }
        } while (!inputOk);
        do {
            System.out.println("Enter your city: ");
            String city = scan.nextLine();
            inputOk = checkIfStringOfLetters(city);
            if (city.length() < 1 || city.isBlank()) {
                System.out.println("Your city must be at least one letter!");
                inputOk = false;
            } else {
                address += " " + city;
            }
        } while (!inputOk);
        do {
            System.out.println("Your mail: ");
            mail = scan.nextLine();
            if (isValid(mail)) {
                inputOk = true;
            } else {
                System.out.println("Your email adress must contain an @ character and a ., example: hello@hi.com");
                inputOk = false;
            }
        } while (!inputOk);
        do {
            System.out.println("Your telephone number:");
            tel = scan.nextLine();
            inputOk = checkIfStringOfNumbers(tel);
            if (tel.length() < 5 || tel.isBlank()) {
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
        stringToCheck = stringToCheck.replace(" ", "");
        Character[] charList = new Character[stringToCheck.length()];
        for (int i = 0; i < stringToCheck.length(); i++) {
            charList[i] = stringToCheck.charAt(i);
        }
        for (Character c : charList) {
            if (!Character.isDigit(c)) {
                System.out.println("Please enter only numbers.");
                return true;
            }
        }
        return false;
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

            for (Book book : books){
                if (book.getIsbn().equals(stringToCheck)){
                    book.setQuantity(book.getQuantity()+1);
                    return true;
                }
            }

        return false;
    }

    private boolean isValid(String mail) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
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
        boolean isDuplicate;
        String isbn;
        String title;
        String author;
        String year;
        String genre;
        do {
            System.out.println("ISBN: ");
            isbn = scan.nextLine();
            inputOk = checkIfStringOfNumbers(isbn);
            if (isbn.length() < 13 || isbn.length() > 13){
                System.out.println("The ISBN number must be 13 digits!");
            }
            if (!inputOk){
                isDuplicate = checkForDuplicates(isbn);
                if (isDuplicate){
                    System.out.println("That book already exists! Another copy has been added.");
                   return;
                }
            }
        } while (inputOk || isbn.length() < 13 || isbn.length() > 13);
        do {
            System.out.println("Title: ");
            title = scan.nextLine();
            if (title.length() < 1 || title.isBlank()) {
                System.out.println("The book's title must contain at least one character!");
            }
            else{
                inputOk = true;
            }

            } while (!inputOk);

        do {
            System.out.println("Author: ");
            author = scan.nextLine();
            inputOk = checkIfStringOfLetters(author);
            if (author.length() < 1 || author.isBlank()) {
                System.out.println("The authors name must contain at least one character!");
                inputOk = false;
            }
        } while (!inputOk);

        boolean yearCheck;
        do {
            System.out.println("Year: ");
            year = scan.nextLine();
            inputOk = checkIfStringOfNumbers(year);
            if (year.length() < 4 || year.length() > 4 || year.isBlank()){
                System.out.println("The publishing date for the book must be 4 digits!");
                yearCheck = false;
            }
            else{
                yearCheck = true;
            }
        } while (!yearCheck);

        int genreCounter = 1;
        System.out.println("All existing genres in the library, pick one or add a new one");
        for (Genre genreObj : genres){
            System.out.println(genreCounter + " " + genreObj.getName());
            genreCounter++;
        }
        System.out.println(genres.size() + 1 + " " + "Add new genre");
        int genreChoice = scan.nextInt();
        for (int i = 0; i < books.size(); i++){
            if (genreChoice - 1 == i){
                genre = books.get(i).getGenre();
            }
            
        }

            genre = scan.nextLine();
            inputOk = checkIfStringOfLetters(genre);

        books.add(new Book(isbn, title, author, year, genre));
        books.get(books.size() - 1).writeToFile(("database/books/" + isbn), (books.get(books.size() - 1).toString()));
        System.out.println("Book added to the library!");
        adminMenu();
    }

    public void deleteBook(Book aBook) {

        Path path = Paths.get("database/books/" + aBook.getIsbn() + ".txt");
        books.removeIf(book -> book.getIsbn().equals(aBook.getIsbn()));

        aBook.deleteFiles(path);
        System.out.println(aBook.getTitle() + " is now deleted.");
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
                        System.out.println("Current author: " + bookToEdit.getAuthorId());
                        System.out.println("New author:");
                        input = scan.nextLine();
                        inputOk = checkIfStringOfLetters(input);
                    } while (!inputOk);
                    bookToEdit.editFile("database/books/" + bookToEdit.getIsbn() + ".txt", "author", "author: " + input);
                    bookToEdit.setAuthorId(input);
                    System.out.println("The author is now changed to " + bookToEdit.getAuthorId());
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
