package com.example.cookim.model;

import com.example.cookim.model.recipe.Recipe;

public interface RecipeDaoInterface {

    public int add(Recipe recipe);

    public int update(Recipe recipe);


}
