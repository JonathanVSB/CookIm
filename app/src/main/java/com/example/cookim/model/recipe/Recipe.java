package com.example.cookim.model.recipe;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    public int id;
    public int user_id;
    public String name;
    public String description;
    public String path;
    public float rating;
    public int like_number;
    public List<Step> recipe_steps;
    public List<Comment> comments;
    public List<Ingredient> ingredients;




    //CONSTRUCTORS
    /*public Recipe(int user_id, String name, String description, List<Step> recipe_steps, List<Comment> comments) {
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.recipe_steps = new ArrayList<>();
        this.comments = new ArrayList<>();
    }*/

    /*public Recipe(int user_id, String name, String description, String path, List<Step> recipe_steps, List<Comment> comments, List<Ingredient> ingredients) {
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.path = path;
        this.recipe_steps = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.ingredients = new ArrayList<>();
    }*/

    //GETTERS AND SETTERS


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getLike_number() {
        return like_number;
    }

    public void setLike_number(int like_number) {
        this.like_number = like_number;
    }

    public List<Step> getRecipe_steps() {
        return recipe_steps;
    }

    public void setRecipe_steps(List<Step> recipe_steps) {
        this.recipe_steps = recipe_steps;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
