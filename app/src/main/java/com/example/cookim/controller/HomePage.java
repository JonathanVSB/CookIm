package com.example.cookim.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivityHomeBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomePage extends Activity {

    private ActivityHomeBinding binding;
    List<Recipe> recipes;
    private final String URL = "http://91.107.198.64:7070/Cookim/";
    private final String URL2 = "http://192.168.127.102:7070/Cookim/";
    private final String URL3 = "http://192.168.127.94:7070/Cookim/";

    Executor executor = Executors.newSingleThreadExecutor();
    Handler handler;

    UserModel user;
    Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        handler = new Handler(Looper.getMainLooper());
        model = new Model();

        recipes = new ArrayList<>();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String token = readToken();

        loadHomePage(token);

        binding.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        UserModel result = model.logout(token);
                        if (result!=null) {
                            displayLogInPage();
                        }
                    }
                });
            }
        });
    }

    private void loadHomePage(String a) {
        String data1 = "my-profile";
        String data2 = "home-page";
        String url = URL3;
        String token = a;

        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    user = model.myProfile(token); /*readUserResponse((url + data1), token);*/
                    List<Recipe> recipes = model.loadRecipes();
                    List<RecipeAdapter> adapters = new ArrayList<>();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            displayRecipes(recipes);
                        }
                    });

                    if (user != null) {
                        binding.tvUsername.setText(user.getUsername());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute
                                if (user != null) {
//                                    executor.execute(() -> {
//                                        try {
//                                            File proFile = new File(getFilesDir(), "user3.jpg");
//                                            if (!proFile.exists()) {
//                                                URL beeUrl = new URL("http://91.107.198.64:7070" + result.getResult2());
//                                                Bitmap beeBitmap = BitmapFactory.decodeStream(beeUrl.openStream());
//                                                FileOutputStream beeOut = new FileOutputStream(proFile);
//                                                beeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, beeOut);
//                                                beeOut.flush();
//                                                beeOut.close();
//                                            }
//                                            runOnUiThread(() -> binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(proFile.getAbsolutePath())));
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            System.out.println("PETA EN ESTA LINEA: " + e.toString());
//                                            binding.profileImage.setImageResource(R.drawable.guest_profile);
//                                        }
//                                    });

//                                        String profileUrl = "http://91.107.198.64:7070" + result.getResult2();
//                                        Glide.with(HomePage.this)
//                                                .load(profileUrl)
//                                                .into(binding.profileImage);
                                } else {
                                    binding.profileImage.setImageResource(R.drawable.guest_profile);
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("ERROR" + e.toString());
        }

    }

    /**
     * Gives the list of recipes to the RecipeAdapter
     *
     * @param recipes
     */
    private void displayRecipes(List<Recipe> recipes) {
        RecipeAdapter adapter = new RecipeAdapter(recipes);
        binding.recommendationsRv.setAdapter(adapter);
        binding.recommendationsRv.setLayoutManager(new LinearLayoutManager(HomePage.this));
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