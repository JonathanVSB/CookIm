package com.example.cookim.model.recipe;

public class Category {

    int id;
    String name;
    String desciption;
    String icon_path;


    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
