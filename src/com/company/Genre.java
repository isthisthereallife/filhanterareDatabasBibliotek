package com.company;

import java.util.List;

public class Genre extends Book {
    private String name;
    private String id;

    public Genre(String name) {
        this.name = name;
        this.id = makeNewId();
    }

    public Genre(List<String> readFromFile) {
        for (String s : readFromFile) {
            s = s.toLowerCase();
            String trim = s.substring(s.indexOf(":") + 1).trim();
            if (s.contains("genre:")) {
                this.name = trim;
            } else if (s.contains("id:")) {
                this.id = trim;
            }
        }
    }


    public String getName() {
        return this.name;
    }

    public void setId() {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "genre: " + this.name + "\nid: " + this.id;
    }
}
