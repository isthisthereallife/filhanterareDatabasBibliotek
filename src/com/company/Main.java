package com.company;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        Library library = new Library();
        library.deleteBook();
        User u = new User("hej", "gata", "mailen", "070");
        u.setActiveLoans("39612786387");
        System.out.println(u.toString());
        System.out.println(u.activeLoansInfo());

        Book b = new Book("titi", "lalal", "dadda", "2839", "e89o");
        System.out.println(b.toString());
        new Library();
    }
}
