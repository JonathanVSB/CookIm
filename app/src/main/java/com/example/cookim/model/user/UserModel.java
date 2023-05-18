package com.example.cookim.model.user;

import android.os.Parcelable;

import com.example.cookim.model.recipe.Recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    Boolean follow;

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

    public UserModel (long id, String username, String full_name, String email, String tel, String Path){
        this.id = id;
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.phone = tel;
        this.path_img = Path;
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

    public UserModel (long id, String username, String full_name, String email, String tel){
        this.id = id;
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.phone = tel;

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

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return id == userModel.id && id_rol == userModel.id_rol && Objects.equals(username, userModel.username) && Objects.equals(password, userModel.password) && Objects.equals(full_name, userModel.full_name) && Objects.equals(email, userModel.email) && Objects.equals(phone, userModel.phone) && Objects.equals(path_img, userModel.path_img) && Objects.equals(description, userModel.description) && Objects.equals(token, userModel.token) && Objects.equals(followers, userModel.followers) && Objects.equals(followed, userModel.followed) && Objects.equals(recipe_likes, userModel.recipe_likes) && Objects.equals(favorites_recipes, userModel.favorites_recipes) && Objects.equals(follow, userModel.follow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, full_name, email, phone, path_img, description, id_rol, token, followers, followed, recipe_likes, favorites_recipes, follow);
    }
}

