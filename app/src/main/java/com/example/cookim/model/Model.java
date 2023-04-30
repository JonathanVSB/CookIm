package com.example.cookim.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.cookim.dao.NukeSSLCerts;
import com.example.cookim.dao.Path;
import com.example.cookim.dao.RecipeDao;
import com.example.cookim.dao.UserDao;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    /**
     * returns the data of the user by his token
     * @param token
     * @return
     */
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


    public DataResult logout(String token){
        DataResult result = userDao.readResponse(path.LOGOUT, token);
        return result;

    }

    public DataResult signIn(String username, String password, String full_name, String email, String phone, long id_rol, File file){
        DataResult result = userDao.validationNewUser(username,
                password,
                full_name,
                email,
                phone,
                id_rol,
                file, path.SIGN);

        return result;

    }

    /**
     * returns the details of the recipe found by his id
     * @param id
     * @param token
     * @return
     */
    public Recipe loadRecipeSteps(int id, String token)
    {
        Recipe recipe = recipeDao.loadRecipeSteps(path.STEPS,id, token);

        return recipe;

    }

    /**
     * creates a path to select where search the images neededs
     * @param pathImg
     * @return
     */
    public String downloadImg(String pathImg) {


        String profileUrl = path.RELATIVEPATH + pathImg;
        return profileUrl;
    }

    /**
     * returns the recipes of the user identified by his token
     * @param token
     * @return
     */
    public List<Recipe> findUserRecipes(String token){
        List<Recipe> recipes = recipeDao.loadMyRecipes(path.MYRECIPES, token);
        return recipes;

    }


}
