package com.example.cookim.model.user;

import com.example.cookim.model.recipe.Recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModel implements Serializable {

    int id;
    String username;
    String pass;
    String full_name;
    String description;
    String email;
    String tel;
    String path;
    List<UserModel> followers;
    List<UserModel> followed;
    List<Recipe> favorites_recipes;
    int rol;
    String token;

    //CONSTRUCTORS
    public UserModel(int id, String username, String pass, String full_name, String email, String tel, List<UserModel> followers, List<UserModel> followed, List<Recipe> favorites_recipes, int rol, String token) {
        this.id = id;
        this.username = username;
        this.pass = pass;
        this.full_name = full_name;
        this.email = email;
        this.tel = tel;
        this.followers = new ArrayList<>();
        this.followed = new ArrayList<>();
        this.favorites_recipes = new ArrayList<>();

        this.rol = Rol.USER;
        this.token = token;
    }

    //FULL CONSTRUCTOR
    public UserModel(int id, String username, String pass, String full_name, String description, String email, String tel, String path, List<UserModel> followers, List<UserModel> followed, List<Recipe> favorites_recipes, int rol, String token) {
        this.id = id;
        this.username = username;
        this.pass = pass;
        this.full_name = full_name;
        this.description = description;
        this.email = email;
        this.tel = tel;
        this.path = path;
        this.followers = new ArrayList<>();
        this.followed = new ArrayList<>();
        this.favorites_recipes = new ArrayList<>();
        this.rol = rol;
        this.token = token;
    }

    public UserModel(String username, String pass) {
        this.username = username;
        this.pass = pass;
    }

    public UserModel() {

    }

    //GETTERS AND SETTERS


    public String getPath() {
        return path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}

