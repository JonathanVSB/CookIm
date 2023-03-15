package com.example.cookim.recipe;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    int id;
    int user_id;
    String name;
    String description;
    String path;
    float rating;
    int like_number;
    List<Step> recipe_steps;
    List<Comment> comments;
    List<Ingredient> ingredients;




    //CONSTRUCTORS
    public Recipe(int user_id, String name, String description, List<Step> recipe_steps, List<Comment> comments) {
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.recipe_steps = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public Recipe(int user_id, String name, String description, String path, List<Step> recipe_steps, List<Comment> comments, List<Ingredient> ingredients) {
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.path = path;
        this.recipe_steps = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.ingredients = new ArrayList<>();
    }
}
