package com.company;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        User h = new User("HA","gatan 3","mailen@mail.com","0802730");
        System.out.println(h.getId());
        Library library = new Library();
        library.register();
    }
}
