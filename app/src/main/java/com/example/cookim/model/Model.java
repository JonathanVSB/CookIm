package com.example.cookim.model;

import android.util.Log;

import com.example.cookim.dao.Path;
import com.example.cookim.dao.RecipeDao;
import com.example.cookim.dao.UserDao;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private UserDao userDao;
    private RecipeDao recipeDao;
    private Path path;

    public Model() {
        path = new Path();
        userDao = new UserDao();
        recipeDao = new RecipeDao();
    }

    public DataResult login(String parametros) {
        DataResult result = userDao.readResponse(path.LOGIN, parametros);
        return result;

    }

    public UserModel myProfile(String token) {
        UserModel userModel = userDao.readUserResponse(path.MYPROFILE, token);
        return userModel;
    }

    public List<Recipe> loadRecipes() {
        List<Recipe> recipes = recipeDao.loadRecipes(path.HOMEPAGE);
        return recipes;

    }

    public DataResult likeRecipe(String parametros){
        DataResult result = recipeDao.readResponse(path.LIKE, parametros);
        return result;

    }


    public UserModel logout(String token){
        UserModel result = userDao.readUserResponse(path.LOGOUT, token);
        return result;

    }

}
