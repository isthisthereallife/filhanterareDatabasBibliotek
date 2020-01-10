package com.company;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("test2.txt");
        Base wth = new Base();
        System.out.println(wth.readFromDisk(path));
        Library library = new Library();
        library.addBook();
    }
}
