package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Base {

    public Base(){

    }

    /**
     *
     * @param searchItem vad som ska sökas efter
     * @param pathString vars det ska sökas
     * @return stränginnehållet i filerna som matchade sökningen
     */
    public String searchInFile(String searchItem, String pathString){
        String result = "";

        File folderPath = new File(pathString);
        try {
            for (File file : folderPath.listFiles()) {
                try {
                    //läs in en fil
                    String stringToCheck = "";
                    stringToCheck = stringToCheck.concat(readFromDisk(file.toPath()));
                    if(stringToCheck.contains(searchItem)){
                        result = result.concat(stringToCheck+"\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            result = "No such file.";
        }
        return result;
    }

    /**
     * läs in en fil från disk, returnera innehållet som sträng
     * @param path
     * @return
     * @throws IOException
     */
    public String readFromDisk(Path path) throws IOException {

        String result = "";
        List<String> resultList = new ArrayList<>();
        resultList = Files.readAllLines(path);
        for (String item : resultList){
            result = result.concat(item) +"\n";
        }

        return result;
    }

    public File[] readFromFolder(File folderPath) {
        File[] fileList = folderPath.listFiles();
        return fileList;
    }

    public List<String> readFromFile(Path path) {

        List<String> contents = null;
        try {
            contents = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public void writeToFile(String fileName, String str) {
        File file = new File(fileName + ".txt");
        try {
            if(file.exists()){
                System.out.println("Filename already exists!");
            } else {
                PrintWriter writer = null;
                writer = new PrintWriter(fileName + ".txt", "UTF-8");
                writer.println(str);
                writer.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
