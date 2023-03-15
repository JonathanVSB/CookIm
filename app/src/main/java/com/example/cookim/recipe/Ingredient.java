package com.example.cookim.recipe;

public class Ingredient {
    int id;
    int id_ingredient;
    int id_recipe;
    String name;

    //CONSTRUCTOR
    public Ingredient(int id_ingredient, int id_recipe, String name) {
        this.id_ingredient = id_ingredient;
        this.id_recipe = id_recipe;
        this.name = name;
    }
}
