package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Base {

    public Base(){

    }

    //läs in en fil från disk, returnera innehållet som sträng
    public String readFromDisk(Path path) throws IOException {

        String result = "";
        List<String> resultList = new ArrayList<>();
        resultList = Files.readAllLines(path);
        for (String item : resultList){
            result = result.concat(item) +"\n";
        }
        return result;

    }



}
