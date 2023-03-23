package com.example.cookim.model.user;

import com.example.cookim.model.recipe.Recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModel implements Serializable {

    long id;
    String username;
    String pass;
    String full_name;
    String email;
    String tel;
    String path;
    List<UserModel> followers;
    List<UserModel> followed;
    List<Recipe> favorites_recipes;
    String description;
    long rol;
    String token;

    //CONSTRUCTORS
    public UserModel(long id, String username, String pass, String full_name, String email, String tel, List<UserModel> followers, List<UserModel> followed, List<Recipe> favorites_recipes, long rol, String token) {
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
    public UserModel(long id, String username, String pass, String full_name, String description, String email, String tel, String path, List<UserModel> followers, List<UserModel> followed, List<Recipe> favorites_recipes, long rol, String token) {
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

    public UserModel(long id, String username, String pass, String full_name, String email, String tel, String path, String description, long rol, String token) {
        this.id = id;
        this.username = username;
        this.pass = pass;
        this.full_name = full_name;
        this.email = email;
        this.tel = tel;
        this.path = path;
        this.description = description;
        this.rol = rol;
        this.token = token;
    }

    public UserModel() {

    }

    //GETTERS AND SETTERS


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<UserModel> getFollowers() {
        return followers;
    }

    public void setFollowers(List<UserModel> followers) {
        this.followers = followers;
    }

    public List<UserModel> getFollowed() {
        return followed;
    }

    public void setFollowed(List<UserModel> followed) {
        this.followed = followed;
    }

    public List<Recipe> getFavorites_recipes() {
        return favorites_recipes;
    }

    public void setFavorites_recipes(List<Recipe> favorites_recipes) {
        this.favorites_recipes = favorites_recipes;
    }

    public long getRol() {
        return rol;
    }

    public void setRol(long rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

