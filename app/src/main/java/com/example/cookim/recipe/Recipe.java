package com.example.cookim.recipe;

import java.util.List;

public class Recipe {

    int id;
    int user_id;
    String name;
    String description;
    String path;
    float rating;
    int like_number;
    List<Step> recipe_steps;
}
