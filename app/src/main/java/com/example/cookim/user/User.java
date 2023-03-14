package com.example.cookim.user;

import android.media.Image;
import android.media.session.MediaSession;

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
    int rol;
    String token;



    public User(String username, String pass, String full_name, String email) {
        this.username = username;
        this.pass = pass;
        this.full_name = full_name;
        this.email = email;
        this.rol = 3;
    }
}

