package com.company;

import java.io.File;
import java.util.Scanner;

public class AddBook {
    private Scanner scan = new Scanner(System.in);
    private Library library;
    private Base base = new Base();
    private boolean inputOk = false;
    private boolean isDuplicate;
    private boolean isNewAuthor = false;
    private boolean isNewGenre = false;
    private String fileName;
    private String authorChoice;
    private String isbn;
    private String title;
    private String authorFname = null;
    private String authorLname = null;
    private String authorId = null;
    private String newAuthorId = null;
    private String year;
    private String genre = null;
    private String genreId = null;
    private Book bookObj = new Book();

    public AddBook() {}

    public AddBook(Library library) {
        this.library = library;
    }


    private String isbn() {
        do {
            System.out.println("ISBN: ");
            isbn = scan.nextLine();
            inputOk = library.checkIfStringOfNumbers(isbn);
            if (isbn.length() < 13 || isbn.length() > 13) {
                System.out.println("The ISBN number must be 13 digits!");
            }
            if (!inputOk) {
                isDuplicate = library.checkForDuplicates(isbn);
                if (isDuplicate) {
                    System.out.println("That book already exists! Another copy has been added.");
                    for (Book book : library.getBooks()) {
                        if (book.getIsbn().equals(isbn)) {
                            fileName = book.idGenerator();
                            book.writeToFile(("database/books/" + fileName), (book.toString()));
                        }
                    }
                    return null;
                }
            }
        } while (inputOk || isbn.length() < 13 || isbn.length() > 13);
        return isbn;
    }

    private String title() {
        do {
            System.out.println("Title: ");
            title = scan.nextLine();
            if (title.length() < 1 || title.isBlank()) {
                System.out.println("The book's title must contain at least one character!");
            } else {
                inputOk = true;
            }
        } while (!inputOk);
        return title;
    }

    private String year() {
        boolean yearCheck;
        do {
            System.out.println("Year: ");
            year = scan.nextLine();
            inputOk = library.checkIfStringOfNumbers(year);
            if (year.length() < 4 || year.length() > 4 || year.isBlank()) {
                System.out.println("The publishing date for the book must be 4 digits!");
                yearCheck = false;
            } else {
                yearCheck = true;
            }
        } while (!yearCheck);
        return year;
    }

    private String genre() {
        do {
            int genreCounter = 1;
            for (Genre genreObj : library.getGenres()) {
                System.out.println("[" + genreCounter + "]" + " " + genreObj.getName());
                genreCounter++;
            }
            int newGenre = library.getGenres().size() + 1;
            System.out.println("[" + newGenre + "]" + " " + "Add new genre");
            System.out.println("All existing genres in the library, pick one or add a new one");
            String genreChoice = scan.nextLine();
            int choice = Integer.parseInt(genreChoice);
            if (choice < genreCounter) {
                for (int i = 0; i < library.getBooks().size(); i++) {
                    if (choice - 1 == i) {
                        genreId = library.getBooks().get(i).getGenre();
                        inputOk = true;
                    }
                }
            } else if (choice == genreCounter) {
                System.out.println("Type in the new genre:");
                genre = scan.nextLine();
                inputOk = library.checkIfStringOfLetters(genre);
                if (genre.length() < 1 || genre.isBlank()) {
                    System.out.println("The genre must contain at least one character!");
                    inputOk = false;
                } else {
                    library.getGenres().add(new Genre(genre));
                    Genre genreObj = library.getGenres().get(library.getGenres().size() - 1);
                    base.writeToFile("database/genres/" + genreObj.getId(), genreObj.toString());
                }
            }
        } while (!inputOk);
        if(isNewGenre){
            genreId = library.getGenres().get(library.getGenres().size() -1).getId();
        }
        return genreId;
    }

    private String fileName() {
        do {
            fileName = bookObj.idGenerator();
            File folderPath = new File("database/books/");
            isDuplicate = base.checkForDuplicateFileNames(folderPath, fileName);
        } while (isDuplicate);
        return fileName;
    }

    private void addProccess(String fileName, String isbn, String title, String authorId, String year, String genreId, boolean isNewAuthor, String authorFname, String authorLname) {
        library.getBooks().add(new Book(fileName, isbn, title, authorId, year, genreId));
        library.getBooks().get(library.getBooks().size() - 1).writeToFile(("database/books/" + fileName), (library.getBooks().get(library.getBooks().size() - 1).toString()));
        if (isNewAuthor) {
            library.getAuthors().add(new Author(authorFname, authorLname, authorId, library.getBooks().get(library.getBooks().size() - 1)));
            Author authorObj = library.getAuthors().get(library.getAuthors().size() - 1);
            authorObj.writeToFile(("database/authors/" + authorObj.getAuthorId()), (authorObj.toString()));
        } else {
            for (Author author : library.getAuthors()) {
                if (author.getAuthorId().equals(authorId)) {
                    String authorsBooks = "";
                    author.addToBibliography(library.getBooks().get(library.getBooks().size() - 1));
                    for (Book book : author.getBibliography()) {
                        authorsBooks = authorsBooks.concat(book.getIsbn() + " ");
                    }
                    author.editFile("database/authors/" + authorId + ".txt", "bibliography", "bibliography: " + authorsBooks);
                }
            }
        }
    }

    public void addBook() {
        isbn = isbn();
        if(isbn == null)
            return;
        title = title();
        do {
            int counter = 1;
            for (Author authorObj : library.getAuthors()) {
                System.out.println("[" + counter + "]" + " " + authorObj.getFirstName() + " " + authorObj.getLastName());
                counter++;
            }
            System.out.println("[" + counter + "] Add new author");
            System.out.println("Choose one of the authors above or add a new:");
            authorChoice = scan.nextLine();
            int choice = Integer.parseInt(authorChoice);
            if (choice < counter) {
                for (int i = 0; i < library.getAuthors().size(); i++) {
                    if (choice - 1 == i) {
                        authorId = library.getAuthors().get(i).getAuthorId();
                        inputOk = true;
                    }
                }
            } else if (choice == counter) {
                isNewAuthor = true;
                System.out.println("Type in Author's first name:");
                authorFname = scan.nextLine();
                inputOk = library.checkIfStringOfLetters(authorFname);
                if (authorFname.length() < 1 || authorFname.isBlank()) {
                    System.out.println("The authors name must contain at least one character!");
                    inputOk = false;
                }
                System.out.println("Type in Author's last name:");
                authorLname = scan.nextLine();
                inputOk = library.checkIfStringOfLetters(authorLname);
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
        year = year();
        genreId = genre();
        fileName = fileName();
        addProccess(fileName, isbn, title, authorId, year, genreId, isNewAuthor, authorFname, authorLname);
        System.out.println("Book added to the library!");
        return;
    }
}