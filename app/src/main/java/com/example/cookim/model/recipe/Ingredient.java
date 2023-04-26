package com.example.cookim.model.recipe;

public class Ingredient {
    int id;
    String name;

    //CONSTRUCTOR

    public Ingredient(int id, String name) {
        this.id = id;
        this.name = name;
    }


    //GETTERS AND SETTERS


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
