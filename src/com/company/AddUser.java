package com.company;

import java.util.Scanner;

public class AddUser {
    private Scanner scan = new Scanner(System.in);
    Library library;
    Check checker;
    private boolean inputOk = false;
    private String name = " ";
    private String address = " ";
    private String mail = " ";
    private String tel = " ";

    public AddUser(Library library) {
        this.library = library;
        this.checker = new Check(library);
    }

    private String name() {
        do {
            System.out.println("Your name:");
            name = scan.nextLine();
            inputOk = checker.checkIfStringOfLetters(name);
            if (name.length() < 1 || name.isBlank()) {
                System.out.println("Your name must be at least one character!");
                inputOk = false;
            }
        } while (!inputOk);
        return name;
    }

    private String address() {
        do {
            System.out.println("Your address:");
            address = scan.nextLine();
            if (address.matches("[0-9]+")) {
                System.out.println("Your adress can not only contain numbers!");
                inputOk = false;
            } else if (address.length() < 1 || address.isBlank()) {
                System.out.println("Your adress must be at least one character!");
                inputOk = false;
            } else if (!address.matches(".*\\d.*")) {
                System.out.println("There must be at least one number in your adress!");
                inputOk = false;
            } else {
                inputOk = true;
            }
        } while (!inputOk);
        return address;
    }

    private String zipCode() {
        String zipCode;
        do {
            System.out.println("Your zipcode: ");
            zipCode = scan.nextLine();
            inputOk = checker.checkIfStringOfNumbers(zipCode);
            if (zipCode.length() < 5 || zipCode.isBlank()) {
                System.out.println("Your zipcode must be at least 5 digits!");
                inputOk = false;
            } else {
                inputOk = true;
                address += " " + zipCode;
            }
        } while (!inputOk);
        return zipCode;
    }

    private String city() {
        String city;
        do {
            System.out.println("Enter your city: ");
            city = scan.nextLine();
            inputOk = checker.checkIfStringOfLetters(city);
            if (city.length() < 1 || city.isBlank()) {
                System.out.println("Your city must be at least one letter!");
                inputOk = false;
            } else {
                address += " " + city;
            }
        } while (!inputOk);
        return city;
    }

    private String mail() {
        do {
            System.out.println("Your mail: ");
            mail = scan.nextLine();
            if (checker.isValid(mail)) {
                inputOk = true;
            } else {
                System.out.println("Your email adress must contain an @ character and a ., example: hello@hi.com");
                inputOk = false;
            }
        } while (!inputOk);
        return mail;
    }

    private String telephone() {
        do {
            System.out.println("Your telephone number:");
            tel = scan.nextLine();
            inputOk = checker.checkIfStringOfNumbers(tel);
            if (tel.length() < 5 || tel.isBlank()) {
                System.out.println("Your telephone number must be at least 5 digits!");
                inputOk = false;
            } else {
                inputOk = true;
            }
        } while (!inputOk);
        return tel;
    }
    private void addProccess(String name, String address, String mail, String tel) {
        library.getUsers().add(new User(name, address, mail, tel));
        User newUser = library.getUsers().get(library.getUsers().size() - 1);
        library.setActiveUser(newUser);
        String uniqueId = library.getActiveUser().getId();
        library.getCards().add(new Card(uniqueId));
        library.getActiveUser().setCardNr(library.getCards().get(library.getCards().size() - 1).getCardNr());
        library.getCards().get(library.getCards().size() - 1).writeToFile("database/cards/" + library.getCards().get(library.getCards().size() - 1).getCardNr(), library.getCards().get(library.getCards().size() - 1).toString());
        library.getActiveUser().writeToFile(("database/users/" + uniqueId), library.getActiveUser().toString());
        library.setActiveCard(library.getCards().get(library.getCards().size() - 1).getCardNr());
        System.out.println("Registration complete!\nYour Log-in id is: " + uniqueId + " (SAVE THIS!)\nYour card number is " + library.getActiveUser().getCardNr());
        return;
    }

    protected void addUser() {
        name = name();
        address = address() + " " + zipCode() + " " + city();
        mail = mail();
        tel = telephone();
        addProccess(name, address, mail, tel);
    }


}
