package com.example.cookim.dao;

import com.example.cookim.model.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeDaoArrayList implements RecipeDaoInterface {

    private List<Recipe> dataSource;

    public RecipeDaoArrayList() {

        dataSource = new ArrayList<>();
    }


    @Override
    public int add(Recipe recipe) {
        return 0;
    }

    @Override
    public int update(Recipe recipe) {
        return 0;
    }
}
