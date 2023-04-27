package com.example.cookim.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.io.FileInputStream;
import java.util.List;

public class MyProfileActivity extends Activity {


    Model model;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        model= new Model();
        String token = readToken();

        //If theres token saved in file
        if (!token.isEmpty()){

            //find user by his token
            UserModel user = model.myProfile(token);

            //if user is not null
            if (user!= null){

                //search all recipes of the user
                List<Recipe> recipes = model.findUserRecipes(token);

            }else{

                System.out.println("usuario nulo");
            }
        }else{

            displayLogInPage();

        }

    }

    /**
     * Read an internal file read the token stored there
     *
     * @return the token or null
     */
    private String readToken() {
        // Gets an instance of the application context
        Context context = getApplicationContext();

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
     * Display the login Page
     */
    private void displayLogInPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
