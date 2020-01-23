package com.company;

import java.util.List;

public class Card extends User {
    String cardNr = "";
    String cardHolder = "";
    String activeLoans = "";

    //ny användare, nytt kort
    public Card(String userId) {
        this.cardHolder = userId;
        this.cardNr = makeNewId();
        this.activeLoans = "";
    }

    public Card(List<String> cardInfoFromDisk) {
        for (String c : cardInfoFromDisk) {
            String trim = c.substring(c.indexOf(":") + 1).trim();
            if (c.contains("cardHolder:")) {
                this.cardHolder = trim;
            } else if (c.contains("cardNr:")) {
                this.cardNr = trim;
            } else if (c.contains("activeLoans:")) {
                this.activeLoans = trim;
            }
        }
    }

    public Card() {

    }

    public String activeLoansInfo() {
        String result = "";

        /* splitta strängen vid mellanslag,
        sök i disk efter isbn
        */
        if (!this.activeLoans.equals("")) {
            String isbnString = this.activeLoans.substring(this.activeLoans.indexOf(":") + 1);
            isbnString = isbnString.trim();
            String[] isbnStringList = isbnString.split(" ");
            for (String s : isbnStringList) {
                //skicka strängen tilll readFromDisk, hitta filen som har denna sträng i sig
                result = result.concat(searchInFile(s, "database/books"));
            }
        }
        return result;
    }

    public String getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(String loaned) {
        this.activeLoans = loaned;
    }

    public String getCardNr() {
        return this.cardNr;
    }

    @Override
    public String toString() {
        return "cardHolder: " + this.cardHolder + "\ncardNr: " + this.cardNr + "\nactiveLoans: " + this.activeLoans;
    }

}
