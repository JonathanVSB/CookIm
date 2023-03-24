package com.example.cookim.model;

public class DataResult {

    String txt;
    String path;


    //Constructor

    public DataResult(String txt, String path) {
        this.txt = txt;
        this.path = path;
    }


    //GETTERS AND SETTERS


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
