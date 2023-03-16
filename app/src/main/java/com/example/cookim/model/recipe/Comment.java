package com.example.cookim.model.recipe;

import java.util.Date;

public class Comment {
    int id;
    int id_user;
    int id_recipe;
    String text;
    Date date;

    public Comment(String text) {
        this.id = id;
        this.id_user = id_user;
        this.id_recipe = id_recipe;
        this.text = text;
        this.date = date;
    }

}
