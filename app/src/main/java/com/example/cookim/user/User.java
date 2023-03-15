package com.example.cookim.user;

import android.media.Image;
import android.media.session.MediaSession;

import com.example.cookim.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

public class User {

    int id;
    String username;
    String pass;
    String full_name;
    String description;
    String email;
    String tel;
    String path;
    List<User> followers;
    List<User> followed;
    List<Recipe> favorites_recipes;
    int rol;
    String token;

    //CONSTRUCTORS
    public User(int id, String username, String pass, String full_name, String email, String tel, List<User> followers, List<User> followed, List<Recipe> favorites_recipes, int rol, String token) {
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
    public User(int id, String username, String pass, String full_name, String description, String email, String tel, String path, List<User> followers, List<User> followed, List<Recipe> favorites_recipes, int rol, String token) {
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

}

