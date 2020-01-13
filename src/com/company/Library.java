package com.company;

import javax.xml.xpath.XPath;
import java.io.File;
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
                    //TODO printa alla böcker med status AVAILABLE
                    running = rerunPrompt();
                    break;
                }
                case "2": {
                    //TODO låna en bok
                    running = rerunPrompt();
                    break;
                }
                case "3": {
                    //TODO lämna tillbaka en bok
                    rerunPrompt();
                    break;
                }
                case "4": {
                    //TODO printa ALLA böcker
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

        String uniqueId = users.get(users.size()-1).getId();
        users.get(users.size()-1).writeToFile(("database/users/" + uniqueId),(users.get(users.size()-1).toString()));
        System.out.printf("Registration completed!\nYour unique id is: %s\n", uniqueId);
        loginMenu();
    }

    public void addBook(){
        System.out.println("What is the book's isbn number?: ");
        String isbn = scan.nextLine();

        System.out.println("What is the name of the book?: ");
        String title = scan.nextLine();

        System.out.println("What is the author's name of the book?: ");
        String author = scan.nextLine();

        System.out.println("What year was the book released?: ");
        String year = scan.nextLine();

        System.out.println("What is the genre of the book?: ");
        String genre = scan.nextLine();

        books.add(new Book(title, author, genre, year, isbn));
        books.get(books.size()-1).writeToFile(("database/books/" + isbn),(books.get(books.size()-1).toString()));
        System.out.println("Boken är nu tillagd i biblioteket!");


    }
    

}
