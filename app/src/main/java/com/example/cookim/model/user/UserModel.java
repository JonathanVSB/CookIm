package com.example.cookim.model.user;

import com.example.cookim.model.recipe.Recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModel implements Serializable {

    long id;
    String username;
    String password;
    String full_name;
    String email;
    String phone;
    String path_img;
    String description;
    long id_rol;

    String token;
    List<UserModel> followers;
    List<UserModel> followed;
    List<Long> recipe_likes;
    List<Recipe> favorites_recipes;

    //CONSTRUCTORS


    public UserModel(String username, String password, String full_name, String email, String phone, long id_rol) {
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.id_rol = id_rol;
    }

    public UserModel(long id, String username, String password, String full_name, String email, String phone, String path_img, long id_rol, String token) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.path_img = path_img;
        this.followers = new ArrayList<>();
        this.followed = new ArrayList<>();
        this.favorites_recipes = new ArrayList<>();
        this.id_rol = id_rol;
        this.token = token;
    }

    public UserModel(long id, String username, String full_name, String email, String tel, List<UserModel> followers, List<UserModel> followed, List<Recipe> favorites_recipes, long rol, String token) {
        this.id = id;
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.phone = tel;
        this.followers = new ArrayList<>();
        this.followed = new ArrayList<>();
        this.favorites_recipes = new ArrayList<>();
        this.id_rol = id_rol;
        this.token = token;
    }

    //FULL CONSTRUCTOR
    public UserModel(long id, String username, String pass, String full_name, String description, String email, String tel, String path, List<UserModel> followers, List<UserModel> followed, List<Recipe> favorites_recipes, long rol, String token) {
        this.id = id;
        this.username = username;
        this.password = pass;
        this.full_name = full_name;
        this.description = description;
        this.email = email;
        this.phone = tel;
        this.path_img = path_img;
        this.id_rol = id_rol;
        this.token = token;
        this.followers = new ArrayList<>();
        this.followed = new ArrayList<>();
        this.favorites_recipes = new ArrayList<>();
    }

    public UserModel(String username, String pass, String token) {
        this.username = username;
        this.password = pass;
        this.token = token;
    }

    public UserModel(long id, String username, String pass, String full_name, String email, String tel, String path_img, String description, long id_rol, String token) {
        this.id = id;
        this.username = username;
        this.password = pass;
        this.full_name = full_name;
        this.email = email;
        this.phone = tel;
        this.path_img = path_img;
        this.description = description;
        this.id_rol = id_rol;
        this.token = token;

    }

    public UserModel(long id, String username, String full_name, String email, String phone, String path_img, String description, long id_rol) {
        this.id = id;
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.path_img = path_img;
        this.description = description;
        this.id_rol = id_rol;

        this.followers = new ArrayList<>();
        this.followed = new ArrayList<>();
        this.favorites_recipes = new ArrayList<>();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPath_img(String path_img) {
        this.path_img = path_img;
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

    public long getId_rol() {
        return id_rol;
    }

    public void setId_rol(long id_rol) {
        this.id_rol = id_rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPath_img() {
        return path_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Long> getRecipe_likes() {
        return recipe_likes;
    }

    public void setRecipe_likes(List<Long> recipe_likes) {
        this.recipe_likes = recipe_likes;
    }
}

