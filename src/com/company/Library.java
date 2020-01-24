package com.company;

import javax.print.DocFlavor;
import javax.xml.xpath.XPath;
import java.io.*;
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
    private ArrayList<Card> cards = new ArrayList<>();
    private Base base = new Base();
    private Menu menu;
    private User activeUser;
    private Card activeCard;


    public Library() {
        load();
        menu = new Menu(this);
        menu.identification();
    }

    private void load() {
        loadBooks();
        loadUsers();
        loadAuthors();
        loadGenres();
        loadCards();
        //loadBorrowedBooks();
    }

    public void loadCards() {
        File folderPath = new File("database/cards/");
        for (File file : base.readFromFolder(folderPath)) {
            final Path path = file.toPath();
            cards.add(new Card(base.readFromFile(path)));
        }
    }

    public Card getActiveCard() {
        return this.activeCard;
    }

    public void setActiveCard(){
        this.activeCard = null;
    }

    public void setActiveCard(Card card) {
        this.activeCard = card;
    }

    public void setActiveCard(String cardNumber) {
        if (!cardNumber.isBlank()) {
            for (Card c : cards) {
                if (c.getCardNr().equals(cardNumber)) {
                    this.activeCard = c;
                    break;
                }
            }
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    private void loadBooks() {
        File folderPath = new File("database/books/");
        String isbn = "";
        for (File file : base.readFromFolder(folderPath)) {
            try (BufferedReader brTest = new BufferedReader(new FileReader(file))) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            if (user.getActiveLoans() != null || !user.getActiveLoans().trim().isEmpty()) {
                loans = loans.trim().concat(" " + user.getActiveLoans());
            }
        }
        for (Book book : books) {
            if (loans.contains(book.getIsbn())) {
                book.setQuantity(book.getQuantity() - 1);
            }
        }
    }

    void listBooks() {
        books.sort(Comparator.comparing(Book::getTitle));
        for (Book book : books)

            System.out.println(book.listToString(authors));
        System.out.println(" ");
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
                            System.out.println("Do you want it?\n1. Yes\n2. No");
                            if (scan.nextLine().equals("1")) {
                                borrowBook(book);
                            } else System.out.println("Never mind, then.");
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

        if (bookToBorrow.getQuantity() < 1) {
            System.out.println("Sorry, someone else has already borrowed that book!");
        } else {
            for (Card c : cards) {
                if (c.getCardNr().equals(activeUser.getCardNr())) {
                    activeCard = c;
                    break;
                }
            }
            bookToBorrow.setQuantity(bookToBorrow.getQuantity() - 1);
            String cardFileName = "database/cards/" + activeCard.getCardNr() + ".txt";
            String bookFileName = "database/books/" + bookToBorrow.getId() + ".txt";
            String cardLineToEdit = "activeLoans";
            String bookLineToEdit = "available";
            String bookNewLine = "Status: unavailable";
            bookToBorrow.editFile(bookFileName, bookLineToEdit, bookNewLine);
            activeCard.setActiveLoans(activeCard.getActiveLoans().concat(" " + bookToBorrow.getIsbn()));
            String cardNewLine = "activeLoans: " + activeCard.getActiveLoans();
            activeCard.editFile(cardFileName, cardLineToEdit, cardNewLine);
            System.out.println("You have borrowed: " + bookToBorrow.getTitle());
        }
    }

    private void returnBook(Book bookToReturn) {
        for (Card c : cards) {
            if (c.getCardNr().equals(activeUser.getCardNr())) {
                activeCard = c;
                break;
            }
        }
        bookToReturn.setQuantity(bookToReturn.getQuantity() + 1);
        String cardFileName = "database/cards/" + activeUser.getCardNr() + ".txt";
        String bookFileName = "database/books/" + bookToReturn.getId() + ".txt";
        String cardLineToEdit = "activeLoans";
        String bookLineToEdit = "available";
        String bookNewLine = "Status: available";
        bookToReturn.editFile(bookFileName, bookLineToEdit, bookNewLine);
        activeCard.setActiveLoans(activeCard.getActiveLoans().replace(bookToReturn.getIsbn(), ""));

        String cardNewLine = "activeLoans: " + activeUser.getActiveLoans();
        activeUser.editFile(cardFileName, cardLineToEdit, cardNewLine);
        System.out.println("You have returned: " + bookToReturn.getTitle());
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
                System.out.println("Your zipcode must be at least 5 digits!");
                inputOk = false;
            } else {
                inputOk = true;
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
            } else {
                inputOk = true;
            }
        } while (!inputOk);

        users.add(new User(name, address, mail, tel));
        activeUser = users.get(users.size() - 1);
        String uniqueId = activeUser.getId();
        cards.add(new Card(uniqueId));
        activeUser.setCardNr(cards.get(cards.size()-1).getCardNr());
        cards.get(cards.size() - 1).writeToFile("database/cards/" + cards.get(cards.size() - 1).getCardNr(), cards.get(cards.size() - 1).toString());
        activeUser.writeToFile(("database/users/" + uniqueId), activeUser.toString());
        setActiveCard(cards.get(cards.size() - 1).getCardNr());
        System.out.println("Registration complete!\nYour Log-in id is: " + uniqueId + " (SAVE THIS!)\nYour card number is " + activeUser.getCardNr());
        menu.userMenu();
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
        for (Book book : books) {
            if (book.getIsbn().equals(stringToCheck)) {
                book.setQuantity(book.getQuantity() + 1);
                book.setTotalQuantity(book.getTotalQuantity() + 1);
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
        boolean isNewAuthor = false;
        boolean isNewGenre = false;
        String fileName;
        String authorChoice;
        String isbn;
        String title;
        String authorFname = null;
        String authorLname = null;
        String authorId = null;
        String newAuthorId = null;
        String year;
        String genre = null;
        String genreId = null;
        String authorsBooks = "";
        Book bookObj = new Book();
        do {
            System.out.println("ISBN: ");
            isbn = scan.nextLine();
            inputOk = checkIfStringOfNumbers(isbn);
            if (isbn.length() < 13 || isbn.length() > 13) {
                System.out.println("The ISBN number must be 13 digits!");
            }
            if (!inputOk) {
                isDuplicate = checkForDuplicates(isbn);
                if (isDuplicate) {
                    System.out.println("That book already exists! Another copy has been added.");
                    for (Book book : books) {
                        if (book.getIsbn().equals(isbn)) {
                            fileName = book.idGenerator();
                            book.writeToFile(("database/books/" + fileName), (book.toString()));
                        }
                    }
                    return;
                }
            }
        } while (inputOk || isbn.length() < 13 || isbn.length() > 13);
        do {
            System.out.println("Title: ");
            title = scan.nextLine();
            if (title.length() < 1 || title.isBlank()) {
                System.out.println("The book's title must contain at least one character!");
            } else {
                inputOk = true;
            }

        } while (!inputOk);

        do {
            int counter = 1;
            for (Author authorObj : authors) {
                System.out.println("[" + counter + "]" + " " + authorObj.getFirstName() + " " + authorObj.getLastName());
                counter++;
            }
            System.out.println("[" + counter + "] Add new author");
            System.out.println("Choose one of the authors above or add a new:");
            authorChoice = scan.nextLine();
            int choice = Integer.parseInt(authorChoice);
            if (choice < counter) {
                for (int i = 0; i < authors.size(); i++) {
                    if (choice - 1 == i) {
                        authorId = authors.get(i).getAuthorId();
                        inputOk = true;
                    }
                }
            } else if (choice == counter) {
                isNewAuthor = true;
                System.out.println("Type in Author's first name:");
                authorFname = scan.nextLine();
                inputOk = checkIfStringOfLetters(authorFname);
                if (authorFname.length() < 1 || authorFname.isBlank()) {
                    System.out.println("The authors name must contain at least one character!");
                    inputOk = false;
                }
                System.out.println("Type in Author's last name:");
                authorLname = scan.nextLine();
                inputOk = checkIfStringOfLetters(authorLname);
                if (authorLname.length() < 1 || authorLname.isBlank()) {
                    System.out.println("The authors name must contain at least one character!");
                    inputOk = false;
                }
            }
        } while (!inputOk);
        do {
            Author authorObj = new Author("New", "Author");
            newAuthorId = authorObj.getAuthorId();
            File folderPath = new File("database/authors/");
            isDuplicate = base.checkForDuplicateFileNames(folderPath, newAuthorId);
        } while (isDuplicate);
        if (isNewAuthor) {
            authorId = newAuthorId;
        }

        boolean yearCheck;

        do {
            System.out.println("Year: ");
            year = scan.nextLine();
            inputOk = checkIfStringOfNumbers(year);
            if (year.length() < 4 || year.length() > 4 || year.isBlank()) {
                System.out.println("The publishing date for the book must be 4 digits!");
                yearCheck = false;
            } else {
                yearCheck = true;
            }
        } while (!yearCheck);

        do {
            int genreCounter = 1;
            for (Genre genreObj : genres) {
                System.out.println("[" + genreCounter + "]" + " " + genreObj.getName());
                genreCounter++;
            }
            int newGenre = genres.size() + 1;
            System.out.println("[" + newGenre + "]" + " " + "Add new genre");
            System.out.println("All existing genres in the library, pick one or add a new one");
            String genreChoice = scan.nextLine();
            int choice = Integer.parseInt(genreChoice);
            if (choice < genreCounter) {
                for (int i = 0; i < books.size(); i++) {
                    if (choice - 1 == i) {
                        genreId = books.get(i).getGenre();
                        inputOk = true;
                    }
                }
            } else if (choice == genreCounter) {
                System.out.println("Type in the new genre:");
                genre = scan.nextLine();
                inputOk = checkIfStringOfLetters(genre);
                isNewGenre = true;
                if (genre.length() < 1 || genre.isBlank()) {
                    System.out.println("The genre must contain at least one character!");
                    inputOk = false;
                } else {
                    genres.add(new Genre(genre));
                    Genre genreObj = genres.get(genres.size() - 1);
                    base.writeToFile("database/genres/" + genreObj.getId(), genreObj.toString());
                }
            }
        } while (!inputOk);
        if(isNewGenre){
            genreId = genres.get(genres.size() -1).getId();
        }

        do {
            fileName = bookObj.idGenerator();
            File folderPath = new File("database/books/");
            isDuplicate = base.checkForDuplicateFileNames(folderPath, fileName);
        } while (isDuplicate);


        books.add(new Book(fileName, isbn, title, authorId, year, genreId));
        books.get(books.size() - 1).writeToFile(("database/books/" + fileName), (books.get(books.size() - 1).toString()));
        if (isNewAuthor) {
            authors.add(new Author(authorFname, authorLname, authorId, books.get(books.size() - 1)));
            Author authorObj = authors.get(authors.size() - 1);
            authorObj.writeToFile(("database/authors/" + authorObj.getAuthorId()), (authorObj.toString()));
        } else {
            for (Author author : authors) {
                if (author.getAuthorId().equals(authorId)) {
                    author.addToBibliography(books.get(books.size() - 1));
                    for (Book book : author.getBibliography()) {
                        authorsBooks = authorsBooks.concat(book.getIsbn() + " ");
                    }
                    author.editFile("database/authors/" + authorId + ".txt", "bibliography", "bibliography: " + 2);
                }
            }
        }
        System.out.println("Book added to the library!");
        menu.adminMenu();
    }

    public void deleteBook(Book aBook) {
        String authorsBooks = "";
        Path path = Paths.get("database/books/" + aBook.getId() + ".txt");
        books.removeIf(book -> book.getIsbn().equals(aBook.getIsbn()));
        for (Author author : authors) {
            for(Book book : author.getBibliography()) {
                if (book.getIsbn().equals(aBook.getIsbn())) {
                    author.removeFromBibliography(book);
                    for (Book theBook : author.getBibliography()) {
                        authorsBooks = authorsBooks.concat(theBook.getIsbn() + " ");
                    }
                    author.editFile("database/authors/" + author.getAuthorId() + ".txt", "bibliography", "bibliography: " + authorsBooks);
                }
            }
            }

        aBook.deleteFiles(path);
        System.out.println(aBook.getTitle() + " is now deleted.");
        menu.adminMenu();
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
                    bookToEdit.editFile("database/books/" + bookToEdit.getId() + ".txt", "title", "title: " + input);
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
                    bookToEdit.editFile("database/books/" + bookToEdit.getId() + ".txt", "author", "author: " + input);
                    bookToEdit.setAuthorId(input);
                    System.out.println("The author is now changed to " + bookToEdit.getAuthorId());
                    break;
                case "3":
                    do {
                        System.out.println("Current year: " + bookToEdit.getYear());
                        System.out.println("New year:");
                        input = scan.nextLine();
                        inputOk = checkIfStringOfNumbers(input);
                    } while (inputOk);
                    bookToEdit.editFile("database/books/" + bookToEdit.getId() + ".txt", "year", "year: " + input);
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
                    bookToEdit.editFile("database/books/" + bookToEdit.getId() + ".txt", "genre", "genre: " + input);
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
        menu.adminMenu();
    }

    /*
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
    */

}
