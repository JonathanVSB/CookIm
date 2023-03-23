package com.example.cookim.controller;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivityHomeBinding;
import com.example.cookim.model.Data;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomePage extends Activity {

    private ActivityHomeBinding binding;
    List<Recipe> recipes;
    final String URL_PATH = "http://192.168.127.80:7070/";

    Executor executor = Executors.newSingleThreadExecutor();

    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recipes = new ArrayList<>();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = (UserModel) getIntent().getSerializableExtra("userModel");

        if (user != null) {
            binding.tvUsername.setText(user.getUsername());
        } else{

            binding.tvUsername.setText("Guest");
            binding.profileImage.setImageResource(R.drawable.guest_profile);
        }

        //TODO
//        executor.execute(() -> {
//            try {
//                String profileUrl = user.getPath();
//                runOnUiThread(() -> Glide.with(this).load(profileUrl).into(binding.profileImage));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });

        //loadRecipes();


    }

    /**
     * Creates HTTP petition, to get the data of all recipes
     */
    private Recipe[] loadRecipes() {

        Data d = null;
        String petition = URL_PATH + "principal";
        try {
            //Generem l'objecte URL que fa servir HttpURLConnection
            URL url = new URL(petition);

            //L'objecte HttpUrlConnection ens permet manipular una connexió HTTP.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            //Connectem amb el servidor
            conn.connect();

            String response = getReponseBody(conn);
            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
            d = new Gson().fromJson(jsonObject.get("data"), Data.class);

            //Devolvemos Recipes
            return d.results;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            //hrow new RuntimeException(e);
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        }
        return d.results;
    }

    /**
     * gets the body response of HTTP request and returnsit as String
     *
     * @param con
     * @return
     * @throws IOException
     */
    private String getReponseBody(HttpURLConnection con) throws IOException {
        BufferedReader br;

        if (con.getResponseCode() >= 400) {
            //if code response superior to 400 displays error
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        } else {
            //if code response lower than 400 displays error gets response
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        return sb.toString();

    }


}