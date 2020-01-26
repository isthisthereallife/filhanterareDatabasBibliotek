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
    private BookHandler bookHandler;
    private User activeUser;
    private Card activeCard;


    public Library() {
        load();
        menu = new Menu(this);
        bookHandler = new BookHandler(this);
        menu.identification();
    }

    private void load() {
        loadBooks();
        loadUsers();
        loadAuthors();
        loadGenres();
        loadCards();
        loadBorrowedBooks();
    }

    public Card getActiveCard() {
        return this.activeCard;
    }

    public void setActiveCard() {
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

    private void loadCards() {
        File folderPath = new File("database/cards/");
        for (File file : base.readFromFolder(folderPath)) {
            final Path path = file.toPath();
            cards.add(new Card(base.readFromFile(path)));
        }
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
        for (Card card : cards) {
            if (card.getActiveLoans() != null || !card.getActiveLoans().trim().isEmpty()) {
                loans = loans.trim().concat(" " + card.getActiveLoans());
            }
        }
        for (Book book : books) {
            if (loans.contains(book.getIsbn())) {
                book.setQuantity(book.getQuantity() - 1);
            }
        }
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
                                bookHandler.borrowBook(book);
                            } else System.out.println("Never mind, then.");
                        } else if (operation.equals("return")) {
                            bookHandler.returnBook(book);
                        } else if (operation.equals("edit")) {
                            bookHandler.editBook(book);
                        } else if (operation.equals("delete")) {
                            bookHandler.deleteBook(book);
                            break;
                        }
                    }
                }
            }
        }
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

