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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
