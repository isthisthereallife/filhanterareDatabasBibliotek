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

    }

    public Card(List<String> cardInfoFromDisk) {
        for (String c : cardInfoFromDisk) {
            String trim = c.substring(c.indexOf(":") + 1).trim();
            if (c.contains("cardHolder:")) {
                this.cardHolder = trim;
            } else if (c.contains("cardNr:")) {
                this.cardNr = trim;
            }
        }
    }


    public String getCardNr() {
        return this.cardNr;
    }

    @Override
    public String toString() {
        return "cardHolder: " + this.cardHolder + "\ncardNr: " + this.cardNr+"\nactiveLoans: "+this.activeLoans;
    }

}
