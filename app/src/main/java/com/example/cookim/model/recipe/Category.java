package com.example.cookim.model.recipe;

public class Category {

    long id;
    String name;


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
