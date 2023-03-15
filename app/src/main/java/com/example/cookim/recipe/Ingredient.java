package com.example.cookim.recipe;

public class Ingredient {
    int id;
    int id_ingredient;
    int id_recipe;
    String type;

    public Ingredient(int id_ingredient, int id_recipe) {
        this.id_ingredient = id_ingredient;
        this.id_recipe = id_recipe;
    }
}
