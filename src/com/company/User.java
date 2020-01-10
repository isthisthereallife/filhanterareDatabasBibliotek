package com.company;

import java.util.Random;

public class User {


    private String name;
    private String address;
    private String mail;
    private String tel;
    private String activeLoans;
    private String uniqueId;

    public User(String name, String address, String mail, String tel) {
        this.name = name;
        this.address = address;
        this.mail = mail;
        this.tel = tel;
        this.activeLoans = "";
        this.uniqueId = idGenerator();
    }

    private String idGenerator() {
        String result = "";
        Random randomizer = new Random();
        for (int i = 0; i < 5; i++) {
            char a = (char) (randomizer.nextInt(26) + 'a');
            int b = (int) (randomizer.nextInt(10));
            result = result.concat(String.valueOf(a));
            result = result.concat(String.valueOf(b));
        }
        return result;
    }

    public String getId(){
        return this.uniqueId;
    }
    public String getActiveLoans() {
        return this.activeLoans;
    }

    private void setActiveLoans(String loaned) {
        this.activeLoans = loaned;
    }
}


