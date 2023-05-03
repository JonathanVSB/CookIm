package com.example.cookim.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityPresentationBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PresentationActivity extends Activity {


    private ActivityPresentationBinding binding;

    String token;
    Model model;

    ExecutorService executor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executor = Executors.newSingleThreadExecutor();
        model = new Model();
        token = readToken();


        if (token == null) {
            displayLogInPage();

        } else if (token != null) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //validates the token
                    DataResult result = model.autologin(token);

                    //if resuls is not null
                    if (result != null) {

                        //if result.getResult()==1 validation is correct
                        if (result.getResult().equals("1")) {
                            showHomePage();
                        }
                        //Token is not validated
                        else {
                            displayLogInPage();

                        }
                    } else {
                        displayLogInPage();

                    }

                }
            });


        }

    }

    /**
     * Display the login Page
     */
    private void displayLogInPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
     * display home page view
     */
    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
