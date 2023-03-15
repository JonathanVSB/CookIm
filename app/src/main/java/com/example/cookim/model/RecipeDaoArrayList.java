package com.example.cookim.model;

import com.example.cookim.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeDaoArrayList implements RecipeDaoInterface {

    private List<Recipe> dataSource;

    public RecipeDaoArrayList() {
        dataSource = new ArrayList<>();
    }


}
