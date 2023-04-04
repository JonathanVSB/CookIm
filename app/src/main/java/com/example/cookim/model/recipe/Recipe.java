package com.example.cookim.model.recipe;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    public int id;
    public int user_id;
    public String name;
    public String description;
    public String path_img;
    public double rating;
    public int likes;
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

    public Recipe(int id, int user_id, String name, String description, String path_img, double rating, int likes) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.path_img = path_img;
        this.rating = rating;
        this.likes = likes;
        this.recipe_steps = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.ingredients = new ArrayList<>();
    }

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

    public String getPath_img() {
        return path_img;
    }

    public void setPath_img(String path_img) {
        this.path_img = path_img;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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
