package com.company;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("test2.txt");
        Base wth = new Base();
        System.out.println(wth.readFromDisk(path));
        Path path = Paths.get("database/books/");
        Book theBook = new Book();
        System.out.println(theBook.readFromDisk(path));

    }
}
