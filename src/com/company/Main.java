package com.company;

public class Main {

    public static void main(String[] args) {

        Book bok = new Book("123", "hej", "hehe", (byte) 2, "2323", "efeef");


        System.out.println(bok.toString());
        bok.setTitle("test");

        System.out.println(bok.toString());

    }
}
