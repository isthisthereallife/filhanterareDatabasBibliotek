package com.company;

import javax.xml.xpath.XPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Library{
    Scanner scan = new Scanner(System.in);
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Book> books = new ArrayList<>();
    private Base base = new Base();
    private User activeUser;

    public Library(){
    load();
    identification();
    //userLoginMenu();
    }

    private void load(){
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

    public void identification(){
        System.out.println("Who would you like to login as?");
        System.out.println("1: Admin");
        System.out.println("2: User");
        int choice = 0;
        choice = scan.nextInt();

        switch (choice){
            case 1:
                adminMenu();
                break;
            case 2:
                userLoginMenu();
                break;
        }
    }

    private void adminMenu() {
        System.out.println("What do you want to do?");
        System.out.println("1: Add book to library");
        System.out.println("2: Remove book from library");
        System.out.println("3: Go back to start menu");
        System.out.println("4: Quit");
        //System.out.println("3: Edit book in library"); "Existerar ej än!"
        int choice = 0;
        choice = scan.nextInt();

        switch (choice){
            case 1:
                addBook();
                break;
            case 2:
                deleteBook();
                break;
            case 3:
                identification();
                break;
            case 4:
                break;

        }
    }
    private void userLoginMenu() {
        System.out.println("1: Login\n2: Register");
        int choice = 0;
        choice = scan.nextInt();

        switch (choice){
            case 1:
                loginMenu();
                break;
            case 2:
                addUser();
                break;
        }
    }
    public void loginMenu(){
        boolean loggedIn = false;
        System.out.println("What is your login id? ");
        String loginId = scan.nextLine();
        for (User user : users){

            if (user.getId().equalsIgnoreCase(loginId)){
                System.out.println("Login successful!");
                activeUser = user;
                loggedIn = true;
                userMenu();
                break;
            }
        }
        if (!loggedIn)
            System.out.println("Login failed.");
            userLoginMenu();
    }

    private void userMenu() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome "+activeUser.getName()+ "\nPlease choose operation:");
        System.out.println();

        boolean running = true;
        String number = "0";
        do {
            System.out.println("1: View available books");
            System.out.println("2: Borrow a book");
            System.out.println("3: Return a book");
            System.out.println("4: View all books in the library");
            if (!this.activeUser.activeLoansInfo().equals("")){
                System.out.println("5: View active loans");
            }
            System.out.println("0: Exit");

            try {
                number = scan.nextLine();
            }catch(InputMismatchException e){
                userMenu();
            }
            switch (number) {
                case "1": {
                    //söker efter böcker med "AVAILABLE" som text
                    System.out.println(activeUser.searchInFile("Available","database/books"));
                    running = rerunPrompt();
                    break;
                }
                case "2": {
                    //TODO låna en bok
                    System.out.println("Search for a book: ");
                    String search = new Scanner(System.in).nextLine();
                    String match = activeUser.searchInFile(search,"database/books").toLowerCase();


                    //TODO refaktorera
                    String[] test = match.split("isbn : ");
                    String testet = test[1];
                    test  = testet.split("\\n");
                    String isbn = test[0];

                    //TODO kolla om det är flera
                    if (isbn.contains(" ")){
                        System.out.println("Your search returned several books: ");
                    }

                    //sök igenom books, leta efter
                    for(Book book : books){
                        if (book.getIsbn().equals(isbn)){

                            System.out.println("Is this the book? \n1. Yes, give!\n2. No, go back");
                            String choice = scan.nextLine();
                            if (choice.equals("1")){
                                book.setStatus("Unavailable");
                                book.writeToFile(book.getIsbn(),book.toString());
                                activeUser.setActiveLoans(activeUser.getActiveLoans().concat(isbn));
                                activeUser.writeToFile(activeUser.getId(),activeUser.toString());
                                //TODO add alex kod för att uppdatera arrayerna och filerna
                            }

                        }
                    }

                    running = rerunPrompt();
                    break;
                }
                case "3": {
                    //TODO lämna tillbaka en bok
                    rerunPrompt();
                    break;
                }
                case "4": {
                    //söker efter böcker med en tom sträng
                    System.out.println(activeUser.searchInFile("","database/books"));

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




    private boolean rerunPrompt(){
        int choice = 0;
        do {
            System.out.println("\n1. Back to main menu");
            System.out.println("2. Quit");
            try {
                choice = new java.util.Scanner(System.in).nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter 1 or 2.");
            }
            if(choice == 1) return true;
            if(choice == 2) return false;
        }while (true);
    }


    public void addUser() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Your name:");
        String name = scan.nextLine();
        System.out.println("Your address:");
        String address = scan.nextLine();
        System.out.println("Your mail: ");
        String mail = scan.nextLine();
        System.out.println("Telephone number:");
        String tel = scan.nextLine();

        users.add(new User(name, address, mail, tel));
        activeUser = users.get(users.size()-1);
        String uniqueId = users.get(users.size()-1).getId();
        users.get(users.size()-1).writeToFile(("database/users/" + uniqueId),(users.get(users.size()-1).toString()));
        System.out.printf("Registration completed!\nYour unique id is: %s\n", uniqueId);
        userMenu();
    }

    public void addBook(){
        scan.nextLine();
        System.out.println("ISBN: ");
        String isbn = scan.nextLine();


        System.out.println("Title: ");
        String title = scan.nextLine();

        System.out.println("Author: ");
        String author = scan.nextLine();

        System.out.println("Year: ");
        String year = scan.nextLine();

        System.out.println("Genre: ");
        String genre = scan.nextLine();

        books.add(new Book(isbn, title, author, genre, year));
        books.get(books.size()-1).writeToFile(("database/books/" + isbn),(books.get(books.size()-1).toString()));
        System.out.println("Book added to the library!");
        adminMenu();


    }

    public void deleteBook() {
        System.out.println("Choose a book to delete? Type ISBN to delete file.");
        long bok = scan.nextLong();
        Path path = Paths.get("database/books/" + bok + ".txt");

        if (!Files.exists(path)){
            System.out.println("The book does not exists! Try again!");
            deleteBook();
        }else {
            try {
                Files.delete(path);
                System.out.println(bok + " is now deleted.");
            }catch (Exception e){
                e.printStackTrace();
            }
        } adminMenu();
    }

}
