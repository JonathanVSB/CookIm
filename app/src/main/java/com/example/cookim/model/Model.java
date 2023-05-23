package com.example.cookim.model;

import android.content.Context;

import com.example.cookim.dao.BBDDIngredients;
import com.example.cookim.dao.CommentDao;
import com.example.cookim.dao.IngredientDao;
import com.example.cookim.dao.NetworkUtils;
import com.example.cookim.dao.Path;
import com.example.cookim.dao.RecipeDao;
import com.example.cookim.dao.UserDao;
import com.example.cookim.exceptions.OpResult;
import com.example.cookim.exceptions.PersistException;
import com.example.cookim.model.recipe.Comment;
import com.example.cookim.model.recipe.Ingredient;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Model {

    private UserDao userDao;
    private RecipeDao recipeDao;
    private CommentDao commentDao;
    private Path path;
    private IngredientDao ingredientDao;
    private NetworkUtils networkUtils;
    Context context;
//    private Context context ;


    public Model(Context appContext) {

        path = new Path();
        userDao = new UserDao();
        this.context = appContext;
        recipeDao = new RecipeDao();
        commentDao = new CommentDao();
        networkUtils = new NetworkUtils();
        ingredientDao = new IngredientDao();


//        this.context = appContext;


    }

    /**
     * validates the user credentials
     *
     * @param parametros
     * @param context
     * @return
     */
    public DataResult login(String parametros, Context context) {
        DataResult result = new DataResult();

        //check the connection
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            // try the petition
            result = userDao.readResponse(path.LOGIN, parametros);
        } catch (Exception e) {
            //
            //Log.e("Login error", "Error during login request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during login request");
        }

        return result;
    }

    /**
     * returns the data of the user by his token
     *
     * @param token
     * @return
     */
    public UserModel myProfile(String token) throws PersistException {
        UserModel userModel = null;

        if (!networkUtils.isNetworkAvailable(context)) {
            //No connection
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            userModel = userDao.readUserResponse(path.MYPROFILE, token);
        } catch (Exception e) {
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        return userModel;
    }

    /**
     * ask for all recipes to database
     *
     * @return
     */
    public List<Recipe> loadRecipes(String token, Context context) throws PersistException {
        List<Recipe> recipes = new ArrayList<>();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            //No connection
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            recipes = recipeDao.loadMyRecipes(path.HOMEPAGE, token);
        } catch (Exception e) {
            //

        }

        return recipes;
    }

    /**
     * Sends to server petition to update likes of the recipe
     *
     * @param parametros
     * @return
     */
    public DataResult likeRecipe(String parametros, Context context) {
        DataResult result = new DataResult();

        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = recipeDao.readResponse(path.LIKE, parametros);
        } catch (Exception e) {
            //
            // Log.e("Like recipe error", "Error during like recipe request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during like recipe request");
        }

        return result;
    }


    /**
     * Sends to server petition to update the recipes saved
     *
     * @param parametros
     * @return
     */
    public DataResult saveRecipe(String parametros, Context context) {
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = recipeDao.readResponse(path.SAVE, parametros);
        } catch (Exception e) {
            //
            // Log.e("Save recipe error", "Error during save recipe request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during save recipe request");
        }

        return result;
    }


    /**
     * Sends petition to server to end session
     *
     * @param token
     * @return
     */
    public DataResult logout(String token, Context context) {
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = userDao.readResponse(path.LOGOUT, token);
        } catch (Exception e) {
            //
            // Log.e("Logout error", "Error during logout request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during logout request");
        }

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
    public DataResult signIn(String username, String password, String full_name, String email, String phone, long id_rol, File file, Context context) {
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = userDao.validationNewUser(username, password, full_name, email, phone, id_rol, file, path.SIGN);
        } catch (Exception e) {
            //
            // Log.e("Sign-in error", "Error during sign-in request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during sign-in request");
        }

        return result;
    }

    public DataResult editData(String token, UserModel user, File file, Context context) {
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = userDao.editUserData(path.EDITDATA, token, user.getUsername(), user.getFull_name(), user.getEmail(), user.getPhone(), user.getPath_img(), file);
        } catch (Exception e) {
            //
            // Log.e("Edit data error", "Error during edit data request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during edit data request");
        }

        return result;
    }


    /**
     * returns the details of the recipe found by his id
     *
     * @param id
     * @param token
     * @return
     */
    public Recipe loadRecipeSteps(int id, String token, Context context) throws PersistException {
        Recipe recipe = new Recipe();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            // No hay conexión, retornar un objeto Recipe vacío
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            recipe = recipeDao.loadRecipeSteps(path.STEPS, id, token);
        } catch (Exception e) {

        }

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
    public List<Recipe> userRecipes(String token, long id, Context context) throws PersistException {
        String param = token + ":" + String.valueOf(id);
        List<Recipe> recipes = new ArrayList<>();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            // No hay conexión, retornar una lista vacía
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            recipes = recipeDao.loadMyRecipes(path.OTHERPROFILES, param);
        } catch (Exception e) {

        }

        return recipes;
    }


    /**
     * Request to server for the new ingredients available in database
     *
     * @param token
     * @param id
     * @return
     */
    public List<Ingredient> getNewIngredients(String token, int id, Context context) throws PersistException {
        List<Ingredient> ingredients = new ArrayList<>();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            // No hay conexión, retornar una lista vacía
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            ingredients = ingredientDao.getAll(path.INGREDIENTS, token, id);
        } catch (Exception e) {

        }

        return ingredients;
    }


    /**
     * removes recipe from database
     *
     * @param token
     * @param id
     * @return
     */
    public DataResult removeRecipe(String token, int id, Context context) {
        String param = token + ":" + String.valueOf(id);
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = recipeDao.readResponse(path.REMOVERECIPE, param);
        } catch (Exception e) {
            //
            // Log.e("Remove recipe error", "Error during remove recipe request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during remove recipe request");
        }

        return result;
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
    public DataResult autologin(String token, Context context) {
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = userDao.validateToken(path.AUTOLOGIN, token);
        } catch (Exception e) {
            //
            // Log.e("Autologin error", "Error during autologin request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during autologin request");
        }

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
    public DataResult createRecipe(Recipe recipe, String token, File file, Context context) {
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = recipeDao.addRecipe(path.ADDRECIPE, token, recipe, file);
        } catch (Exception e) {
            //
            // Log.e("Create recipe error", "Error during create recipe request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during create recipe request");
        }

        return result;
    }


    /**
     * Sends petition to recover the data of the profile of users
     *
     * @param token
     * @param id
     * @return
     */
    public UserModel userProfile(String token, long id, Context context) throws PersistException {
        UserModel userModel = new UserModel();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            // No hay conexión, retornar un objeto UserModel vacío
            return userModel;
        }

        try {
            userModel = userDao.readOtherUserResponse(path.OTHERPROFILES, token, id);
        } catch (Exception e) {
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        return userModel;
    }


    /**
     * Request all comments of the recipe
     *
     * @param token
     * @param id
     * @return
     */
    public List<Comment> getCommentsOfRecipe(String token, long id, Context context) throws PersistException {
        List<Comment> result = new ArrayList<>();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            // No hay conexión, retornar una lista vacía
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            result = commentDao.getAllComments(path.COMMENTS, token, id);
        } catch (Exception e) {
            //
            // Log.e("Get comments error", "Error getting comments of recipe: " + e.toString());
        }

        return result;
    }


    /**
     * Read an internal file and retrieve the token stored there
     *
     * @return the token or null
     */
    public String readFile(Context cont, String filename) {
        // Gets an instance of the application context
        Context context = cont;

        // Open the file in write mode and create the FileOutputStream object
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput(filename + ".txt");


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
    public DataResult createNewComment(String token, Comment comment, Context context) {
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = commentDao.addNewComment(path.NEWCOMMENT, token, comment);
        } catch (Exception e) {
            //
            // Log.e("Create new comment error", "Error during create new comment request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during create new comment request");
        }

        return result;
    }


    /**
     * Send new follow to user
     *
     * @param token
     * @param userId
     * @param num
     * @return
     */
    public DataResult followUser(String token, int userId, int num, Context context) {
        String param = token + ":" + String.valueOf(num) + ":" + String.valueOf(userId);
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = userDao.readResponse(path.FOLLOW, param);
        } catch (Exception e) {
            //
            // Log.e("Follow user error", "Error during follow user request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during follow user request");
        }

        return result;
    }


    /**
     * Gets list of the favoritesrecipes saved by the use
     *
     * @param token
     * @return
     */
    public List<Recipe> getFavorites(String token, Context context) throws PersistException {
        List<Recipe> recipes = new ArrayList<>();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            // No hay conexión, retornar una lista vacía
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            recipes = recipeDao.loadMyRecipes(path.FAVORITES, token);
        } catch (Exception e) {
            //
            // Log.e("Get favorites error", "Error getting favorites: " + e.toString());
        }

        return recipes;
    }


    /**
     * ask server to change the password of the user
     *
     * @param token
     * @param pass
     * @param newpass
     * @return
     */
    public DataResult changePass(String token, String pass, String newpass, Context context) {
        String param = token + ":" + pass + ":" + newpass;
        DataResult result = new DataResult();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            result.setResult("0000");
            result.setData("No connection");
            return result;
        }

        try {
            result = userDao.readResponse(path.CHANGEPASS, param);
        } catch (Exception e) {
            //
            // Log.e("Change password error", "Error during change password request: " + e.toString());
            result.setResult("0001");
            result.setData("Error during change password request");
        }

        return result;
    }


    /**
     * search the recipe by the searchQuery
     *
     * @param token
     * @param searchQuery
     * @return
     */
    public List<Recipe> searchRecipe(String token, String searchQuery, Context context) throws PersistException {
        String param = token + ":" + searchQuery;
        List<Recipe> recipes = new ArrayList<>();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            // No hay conexión, retornar una lista vacía
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            recipes = recipeDao.loadMyRecipes(path.SEARCH, param);
        } catch (Exception e) {
            //
            // Log.e("Search recipe error", "Error searching recipe: " + e.toString());
        }

        return recipes;
    }

    /**
     * search the recipe by the searchQuery
     *
     * @param token
     * @param searchQuery
     * @return
     */
    public List<Recipe> searchRecipeByCategory(String token, String searchQuery, Context context) throws PersistException {
        String param = token + ":" + searchQuery;
        List<Recipe> recipes = new ArrayList<>();

        // check network conn
        if (!networkUtils.isNetworkAvailable(context)) {
            // No hay conexión, retornar una lista vacía
            throw new PersistException(OpResult.DB_NOCONN.getCode());
        }

        try {
            recipes = recipeDao.loadMyRecipes(path.SEARCHCATEGORY, param);
        } catch (Exception e) {
            //
            // Log.e("Search recipe error", "Error searching recipe: " + e.toString());
        }

        return recipes;
    }

}
