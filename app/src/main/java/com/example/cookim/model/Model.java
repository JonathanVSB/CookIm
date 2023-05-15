package com.example.cookim.model;

import android.content.Context;

import com.example.cookim.dao.BBDDIngredients;
import com.example.cookim.dao.CommentDao;
import com.example.cookim.dao.IngredientDao;
import com.example.cookim.dao.Path;
import com.example.cookim.dao.RecipeDao;
import com.example.cookim.dao.UserDao;
import com.example.cookim.model.recipe.Comment;
import com.example.cookim.model.recipe.Ingredient;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Model {

    private UserDao userDao;
    private RecipeDao recipeDao;
    private CommentDao commentDao;
    private Path path;
    private IngredientDao ingredientDao;


    public Model() {
        path = new Path();
        userDao = new UserDao();
        recipeDao = new RecipeDao();
        commentDao = new CommentDao();
        ingredientDao = new IngredientDao();


    }

    public DataResult login(String parametros) {
        DataResult result = userDao.readResponse(path.LOGIN, parametros);
        return result;

    }

    /**
     * returns the data of the user by his token
     *
     * @param token
     * @return
     */
    public UserModel myProfile(String token) {
        UserModel userModel = userDao.readUserResponse(path.MYPROFILE, token);
        return userModel;
    }

    /**
     * ask for all recipes to database
     *
     * @return
     */
    public List<Recipe> loadRecipes() {
        List<Recipe> recipes = recipeDao.loadRecipes(path.HOMEPAGE);
        return recipes;

    }

    /**
     * Sends to server petition to update likes of the recipe
     *
     * @param parametros
     * @return
     */
    public DataResult likeRecipe(String parametros) {
        DataResult result = recipeDao.readResponse(path.LIKE, parametros);
        return result;


    }

    /**
     * Sends to server petition to update the recipes saved
     *
     * @param parametros
     * @return
     */
    public DataResult saveRecipe(String parametros) {
        DataResult result = recipeDao.readResponse(path.SAVE, parametros);
        return result;
    }

    /**
     * Sends petition to server to end session
     *
     * @param token
     * @return
     */
    public DataResult logout(String token) {
        DataResult result = userDao.readResponse(path.LOGOUT, token);
        return result;

    }

    /**
     * Send petition to server to create a new user using the next params
     *
     * @param username
     * @param password
     * @param full_name
     * @param email
     * @param phone
     * @param id_rol
     * @param file
     * @return
     */
    public DataResult signIn(String username, String password, String full_name, String email, String phone, long id_rol, File file) {
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
     *
     * @param id
     * @param token
     * @return
     */
    public Recipe loadRecipeSteps(int id, String token) {
        Recipe recipe = recipeDao.loadRecipeSteps(path.STEPS, id, token);

        return recipe;

    }

    /**
     * creates a path to select where search the images neededs
     *
     * @param pathImg
     * @return
     */
    public String downloadImg(String pathImg) {


        String profileUrl = path.RELATIVEPATH + pathImg;

        return profileUrl;
    }

    /**
     * returns the recipes of the user identified by his token
     *
     * @param token
     * @return
     */
    public List<Recipe> userRecipes(String token, long id) {
        String param = token + ":" + String.valueOf(id);
        List<Recipe> recipes = recipeDao.loadMyRecipes(path.OTHERPROFILES, token);
        return recipes;

    }

    /**
     * Request to server for the new ingredients available in database
     *
     * @param token
     * @param id
     * @return
     */
    public List<Ingredient> getNewIngredients(String token, int id) {

        List<Ingredient> ingredients = ingredientDao.getAll(path.INGREDIENTS, token, id);
        return ingredients;

    }

    /**
     * Ask to local database the max Id of the ingredients table
     *
     * @param ingredients
     * @return
     */
    public Integer getMaxIdIngredient(BBDDIngredients ingredients) {

        int id = ingredientDao.getMaxIngredientId(ingredients);
        return id;
    }

    /**
     * Validates if the token saved still
     *
     * @param token
     * @return
     */
    public DataResult autologin(String token) {

        DataResult result = userDao.validateToken(path.AUTOLOGIN, token);
        return result;
    }

    /**
     * sends petition to create a new recipe
     *
     * @param recipe
     * @param token
     * @param file
     * @return
     */
    public DataResult createRecipe(Recipe recipe, String token, File file) {

        DataResult result = recipeDao.addRecipe(path.ADDRECIPE, token, recipe, file);
        return result;
    }

    /**
     * Sends petition to recover the data of the profile of users
     *
     * @param token
     * @param id
     * @return
     */
    public UserModel userProfile(String token, long id) {
        UserModel userModel = userDao.readOtherUserResponse(path.OTHERPROFILES, token, id);
        return userModel;
    }

    /**
     * Request all comments of the recipe
     *
     * @param token
     * @param id
     * @return
     */
    public List<Comment> getCommentsOfRecipe(String token, long id) {
        List<Comment> result = commentDao.getAllComments(path.COMMENTS, token, id);
        return result;


    }

    /**
     * Read an internal file and retrieve the token stored there
     *
     * @return the token or null
     */
    public String readToken(Context cont) {
        // Gets an instance of the application context
        Context context = cont;

        // Open the file in write mode and create the FileOutputStream object
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput("token.txt");


            //Reads the token data from file
            StringBuilder stringBuilder = new StringBuilder();
            int c;
            while ((c = inputStream.read()) != -1) {
                stringBuilder.append((char) c);
            }
            String token = stringBuilder.toString();


            //Close the FileInputStream Object
            inputStream.close();

            // returns token
            return token;
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error al leer la respuesta: " + e.toString());

        }


        // if file is empty, returns null
        return null;
    }

    /**
     * Sends new petition to server to post a new comment
     *
     * @param token
     * @param comment
     * @return
     */
    public DataResult createNewComment(String token, Comment comment) {
        DataResult result = commentDao.addNewComment(path.NEWCOMMENT, token, comment);
        return result;
    }

    public DataResult followUser(String token, int userId, int num) {
        String param = token + ":" + String.valueOf(num) + ":" + String.valueOf(userId);
        DataResult result = userDao.readResponse(path.FOLLOW, param);
        return result;

    }

    public List<Recipe> getFavorites(String token) {
        List<Recipe> recipes = recipeDao.loadMyRecipes(path.FAVORITES, token);
        return recipes;

    }
}
