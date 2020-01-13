package com.company;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        Library library = new Library();
        library.deleteBook();
    }
}
