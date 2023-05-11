package com.example.cookim.model.recipe;

import java.io.File;

public class Step {

    File file;
    long id;
    long recipe_id;
    long step_number;
    String description;
    String path;

    public Step(long id, long recipe_id, long step_number, String description) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.step_number = step_number;
        this.description = description;
    }

    public Step(File file, long step_number, String description) {
        this.file = file;
        this.step_number = step_number;
        this.description = description;
    }


    public Step(long id, long recipe_id, long step_number, String description, String path) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.step_number = step_number;
        this.description = description;
        this.path = path;
    }

    //GETTERS AND SETTERS


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(long recipe_id) {
        this.recipe_id = recipe_id;
    }

    public long getStep_number() {
        return step_number;
    }

    public void setStep_number(long step_number) {
        this.step_number = step_number;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}


