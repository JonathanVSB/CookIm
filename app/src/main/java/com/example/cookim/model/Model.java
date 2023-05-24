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
     * Performs a login request with the specified parameters.
     *
     * @param parametros the login parameters
     * @param context    the application context
     * @return the result of the login request
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
     * Retrieves the user's profile information.
     *
     * @param token the user's authentication token
     * @return the user's profile information
     * @throws PersistException if there is an error during the retrieval of the profile information
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
     * Loads the recipes for the user.
     *
     * @param token   the user's authentication token
     * @param context the application context
     * @return a list of recipes
     * @throws PersistException if there is an error during the loading of recipes
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
     * Likes a recipe.
     *
     * @param parametros the parameters for the request
     * @param context    the application context
     * @return the result of the like operation
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
     * Saves a recipe.
     *
     * @param parametros the parameters for the request
     * @param context    the application context
     * @return the result of the save operation
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
     * Logs out the user.
     *
     * @param token   the user token
     * @param context the application context
     * @return the result of the logout operation
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
     * Signs in a new user.
     *
     * @param username  the username
     * @param password  the password
     * @param full_name the full name
     * @param email     the email
     * @param phone     the phone number
     * @param id_rol    the role ID
     * @param file      the user's profile image file
     * @param context   the application context
     * @return the result of the sign-in operation
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

    /**
     * Edits user data.
     *
     * @param token    the user's authentication token
     * @param user     the user object containing updated data
     * @param file     the updated profile image file
     * @param context  the application context
     * @return the result of the edit data operation
     */
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
     * Loads the steps of a recipe.
     *
     * @param id       the ID of the recipe
     * @param token    the user's authentication token
     * @param context  the application context
     * @return the recipe object with loaded steps
     * @throws PersistException if there is an error during the operation
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
     * Downloads an image from the specified path.
     *
     * @param pathImg the path of the image to download
     * @return the URL of the downloaded image
     */
    public String downloadImg(String pathImg) {


        String profileUrl = path.RELATIVEPATH + pathImg;

        return profileUrl;
    }

    /**
     * Retrieves the recipes associated with a user.
     *
     * @param token   the authentication token
     * @param id      the ID of the user
     * @param context the context of the application
     * @return a list of recipes associated with the user
     * @throws PersistException if there is a problem retrieving the recipes
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
     * Retrieves new ingredients for a recipe.
     *
     * @param token   the authentication token
     * @param id      the ID of the recipe
     * @param context the context of the application
     * @return a list of new ingredients for the recipe
     * @throws PersistException if there is a problem retrieving the ingredients
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
     * Removes a recipe.
     *
     * @param token   the authentication token
     * @param id      the ID of the recipe to be removed
     * @param context the context of the application
     * @return the result of the removal operation
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
     * Gets the maximum ID of an ingredient from the database.
     *
     * @param ingredients the instance of BBDDIngredients
     * @return the maximum ID of an ingredient
     */
    public Integer getMaxIdIngredient(BBDDIngredients ingredients) {

        int id = ingredientDao.getMaxIngredientId(ingredients);
        return id;
    }

    /**
     * Performs automatic login using the provided token.
     *
     * @param token   the user token for authentication
     * @param context the context of the application
     * @return a DataResult object containing the result of the autologin request
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
     * Creates a new recipe.
     *
     * @param recipe  the recipe object to be created
     * @param token   the user token for authentication
     * @param file    the file associated with the recipe (e.g., image)
     * @param context the context of the application
     * @return a DataResult object containing the result of the create recipe request
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
     * Retrieves the user profile of a specific user.
     *
     * @param token   the user token for authentication
     * @param id      the ID of the user profile to retrieve
     * @param context the context of the application
     * @return the UserModel object representing the user profile
     * @throws PersistException if there is a persistence-related exception
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
     * Retrieves the comments of a recipe.
     *
     * @param token   the user token for authentication
     * @param id      the ID of the recipe
     * @param context the context of the application
     * @return a list of Comment objects representing the comments of the recipe
     * @throws PersistException if there is a persistence-related exception
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
     * Reads the content of a file.
     *
     * @param cont     the context of the application
     * @param filename the name of the file to read
     * @return the content of the file as a string
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
     * Creates a new comment.
     *
     * @param token   the user token
     * @param comment the comment to be created
     * @param context the context of the application
     * @return the result of the operation
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
     * Follows a user.
     *
     * @param token   the user token
     * @param userId  the ID of the user to follow
     * @param num     a number value
     * @param context the context of the application
     * @return the result of the operation
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
     * Retrieves the user's favorite recipes.
     *
     * @param token   the user token
     * @param context the context of the application
     * @return a list of favorite recipes
     * @throws PersistException if there is a persistence exception
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
     * Changes the user's password.
     *
     * @param token   the user token
     * @param pass    the current password
     * @param newpass the new password
     * @param context the context of the application
     * @return the result of the change password operation
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
     * Searches for recipes based on the given search query.
     *
     * @param token       the user token
     * @param searchQuery the search query
     * @param context     the context of the application
     * @return a list of recipes matching the search query
     * @throws PersistException if there is a problem with the database connection
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
     * Searches for recipes by category based on the given search query.
     *
     * @param token       the user token
     * @param searchQuery the search query
     * @param context     the context of the application
     * @return a list of recipes matching the search query by category
     * @throws PersistException if there is a problem with the database connection
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
