package com.example.cookim.model;

import com.example.cookim.model.user.UserModel;

import java.io.IOException;
import java.io.Serializable;

public class DataResult implements Serializable {

    String result;

    Object data;
    long recipeId;



    //Constructor

    public DataResult(String result) {
        this.result = result;

    }

    public DataResult(String result, Object data, long id) {
        this.result = result;
        this.data = data;
        this.recipeId = id;
    }

    public DataResult() {


    }

    public long getRecipe_id() {
        return recipeId;
    }

    public void setRecipe_id(long recipe_id) {
        this.recipeId = recipe_id;
    }

    public DataResult(String result, Object data) {
        this.result = result;
        this.data = data;
    }

    //GETTERS AND SETTERS


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    //METHODS


}

