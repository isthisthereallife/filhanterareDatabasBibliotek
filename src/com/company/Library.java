package com.company;

import javax.xml.xpath.XPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Library{
    Scanner scan = new Scanner(System.in);
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Book> books;
    private User activeUser;

    public Library(){
    books = new ArrayList<Book>();
    initializeBookAndUserObjects();
    //identification();
    userLoginMenu();

    }
    private void initializeBookAndUserObjects(){
        //TODO initiera bok-objekten och user-objekten här
    }
    public void identification(){
        System.out.println("Who would you like to login as?");
        System.out.println("1: Admin");
        System.out.println("2: User");
        String choice = scan.nextLine();

        if (choice.equals("1")){
            adminMenu();
        }
        else if (choice.equals("2")){
            userLoginMenu();
        }
    }

    private void adminMenu() {
        System.out.println("What do you want to do?");
        System.out.println("1: Add book to library");
        System.out.println("2: Remove book from library");
        System.out.println("3: Edit book in library");
        String choice = scan.nextLine();
        if (choice.equals("1")){
            addBook();
        }
        /*else if (choice == 2){
            deleteBook();
        }*/
    }
    private void userLoginMenu() {
        System.out.println("1: Login\n2: Register");
        String choice = scan.nextLine();
        if (choice.equals("1")){
            loginMenu();
        }
        if(choice.equals("2")){
            addUser();
        }
    }
    public void loginMenu(){
        boolean loggedIn = false;
        System.out.println("What is your login id? ");
        String loginId = scan.nextLine();
        for (User user : users){

            if (user.getId().equals(loginId)){
                System.out.println("Login successful!");
                activeUser = user;
                loggedIn = true;
                userMenu();
                break;
            }
        }
        if (!loggedIn)
            System.out.println("Login failed.");
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
                    System.out.println(activeUser.searchInFile("AVAILABLE","database/books"));
                    running = rerunPrompt();
                    break;
                }
                case "2": {
                    //TODO låna en bok
                    System.out.println("Sök efter bok: ");
                    String search = new Scanner(System.in).nextLine();
                    String match = activeUser.searchInFile(search,"database/books").toLowerCase();
                    System.out.println(match);
                    //TODO kolla om det är flera
                    String[] test = match.split("isbn : ");
                    String testet = test[1];
                    test  = testet.split("\\n");
                    String isbn = test[0];

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

        books.add(new Book(title, author, genre, year, isbn));
        books.get(books.size()-1).writeToFile(("database/books/" + isbn),(books.get(books.size()-1).toString()));
        System.out.println("Book added to the library!");


    }
    

}
