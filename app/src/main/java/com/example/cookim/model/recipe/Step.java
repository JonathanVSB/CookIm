package com.example.cookim.model.recipe;

public class Step {

    int id;
    int recipe_id;
    int step_number;
    String description;
    String path;

    public Step( int recipe_id, int step_number, String description, String path) {

        this.recipe_id = recipe_id;
        this.step_number = step_number;
        this.description = description;
        this.path = path;
    }

    public Step(int recipe_id, int step_number, String description) {
        this.recipe_id = recipe_id;
        this.step_number = step_number;
        this.description = description;
    }

    public Step(int id, int recipe_id, int step_number, String description, String path) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.step_number = step_number;
        this.description = description;
        this.path = path;
    }
}
