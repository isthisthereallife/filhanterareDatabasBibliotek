package com.company;

public class User {


    private String name;
    private String adress;
    private String mail;
    private String tel;
    private String activeLoans;
    private String uniqueId;

    public User(String name, String adress, String mail, String tel, String activeLoans, String uniqueId){
        this.name = name;
        this.adress = adress;
        this.mail = mail;
        this.tel = tel;
        this.activeLoans = activeLoans;
        this.uniqueId = uniqueId;
    }

    public User() {

    }

    public String getActiveLoans(){
        return this.activeLoans;
    }
    public void setActiveLoans(String loaned){
        this.activeLoans=loaned;
    }
}


