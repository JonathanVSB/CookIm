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

    public Recipe loadRecipeSteps(int id)
    {
        Recipe recipe = recipeDao.loadRecipeSteps(path.STEPS,id);
        return recipe;

    }


    public String downloadImg(String pathImg) {
//        Drawable downloadedImg = null;
//
//        // Crea un objeto File que apunta al archivo especificado por la ruta de usuario
//        File proFile = new File("user.jpg");
//
//        // Verifica si el archivo ya existe en el directorio de archivos. Si no existe, continúa descargando la imagen.
//        if (!proFile.exists()) {
//            // Descarga la imagen usando la ruta de la imagen especificada
//            try {
//                URL url = new URL(path.RELATIVEPATH + pathImg);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap downloadedBitmap = BitmapFactory.decodeStream(input);
//
//                // Guarda la imagen descargada en el archivo correspondiente
//                FileOutputStream outputStream = new FileOutputStream(proFile);
//                downloadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                outputStream.flush();
//                outputStream.close();
//
//                // Crea un objeto Drawable a partir del objeto Bitmap descargado
//                downloadedImg = new BitmapDrawable(context.getResources(), downloadedBitmap);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            // Si el archivo ya existe, carga la imagen desde el archivo
//            Bitmap bitmap = BitmapFactory.decodeFile(proFile.getAbsolutePath());
//
//            // Crea un objeto Drawable a partir del objeto Bitmap cargado desde el archivo
//            downloadedImg = new BitmapDrawable(context.getResources(), bitmap);
//        }
//
//        // Devuelve el objeto Drawable descargado o cargado desde el archivo
//        return downloadedImg;

        String profileUrl = path.RELATIVEPATH + pathImg;
        return profileUrl;
    }




}
