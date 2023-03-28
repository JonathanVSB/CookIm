package com.example.cookim.controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cookim.R;
import com.example.cookim.databinding.ActivityHomeBinding;
import com.example.cookim.model.Data;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

import javax.net.ssl.HttpsURLConnection;

public class HomePage extends Activity {

    private ActivityHomeBinding binding;
    List<Recipe> recipes;
    private final String URL = "http://91.107.198.64:7070/";
    private final String URL2 = "http://192.168.127.102:7070/";

    Executor executor = Executors.newSingleThreadExecutor();
    Handler handler;

    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        handler = new Handler(Looper.getMainLooper());


        recipes = new ArrayList<>();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        user = (UserModel) getIntent().getSerializableExtra("userModel");
        String token = (String) getIntent().getSerializableExtra("token");

        loadHomePage(token);


    }

    private void loadHomePage(String a) {
        String url = URL + "perfil";
        String token = "token=" + a;


        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    DataResult result = readResponse(url, token);
                    if (result != null) {
                        binding.tvUsername.setText(result.getResult());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute
                                if (result.getResult2() != null) {
                                    executor.execute(() -> {
                                        try {
                                            File proFile = new File(getFilesDir(), "user3.jpg");
                                            if (!proFile.exists()) {
                                                URL beeUrl = new URL("http://91.107.198.64:7070" + result.getResult2());
                                                Bitmap beeBitmap = BitmapFactory.decodeStream(beeUrl.openStream());
                                                FileOutputStream beeOut = new FileOutputStream(proFile);
                                                beeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, beeOut);
                                                beeOut.flush();
                                                beeOut.close();
                                            }
                                            runOnUiThread(() -> binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(proFile.getAbsolutePath())));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println("PETA EN ESTA LINEA: " + e.toString());
                                            binding.profileImage.setImageResource(R.drawable.guest_profile);
                                        }
                                    });

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

//    private DataResult readResponse(String urlString, String token) {
//        DataResult result = null;
//        try {
//            //HTTP request
//            System.out.println("ENTRA  " + urlString);
//            URL url = new URL(urlString);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("User-Agent", "");
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//            connection.setRequestMethod("POST");
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//
//            // Write parameters to the request
//            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
//                wr.write(token.getBytes(StandardCharsets.UTF_8));
//            }
//
//            connection.connect();
//
//            if (connection != null) {
//                // read Stream
//                InputStream inputStream = connection.getInputStream();
//
//                // parse the response into UserModel object
//
//                result = parseDataResult(inputStream);
//
//                //
//                inputStream.close();
//
//            }
//
//        } catch (Exception e) {
//            Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
//            System.out.println("PETA EN ESTA LINEA: " + e.toString());
//        }
//
////        return user;
//        return result;
//    }

    private DataResult readResponse(String urlString, String token) {
        DataResult result = null;
        try {
            // HTTPS request
            System.out.println("ENTRA  " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set authorization header with token
            String authHeader = "Bearer " + token;
            connection.setRequestProperty("Authorization", authHeader);

            // Set content type to form url encoded
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Write parameters to the request body
            String requestBody = "";
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into DataResult object
                result = parseDataResult(inputStream);

                inputStream.close();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + e.toString());
        }

        return result;
    }

    /**
     * Reads the Json answer of the server and transforms it in DataResult object
     *
     * @param inputStream
     * @return
     */
    private DataResult parseDataResult(InputStream inputStream) {
        DataResult result = null;

        try {
            // Initializes a BufferedReader object to read the InputStream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Initializes a StringBuilder object to hold the JSON-formatted string
            StringBuilder stringBuilder = new StringBuilder();

            // Reads each line of the InputStream and appends it to the StringBuilder object
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                stringBuilder.append(linea);
            }

            // Closes the BufferedReader
            bufferedReader.close();
            // Converts the StringBuilder object to a string and modifies it
            //String jsonString = stringBuilder.toString();

//            jsonString = jsonString.replace("\"", "");
            // Removes the quotes around the braces
            String jsonString = stringBuilder.toString().replaceAll("\"\\{", "{").replaceAll("\\}\"", "}");


            // Debugging statement
            System.out.println("Respuesta JSON modificada: " + jsonString);

            // Checks if the modified string starts and ends with "{" and "}"
            if (jsonString.trim().startsWith("{") && jsonString.trim().endsWith("}")) {

                Gson gson = new Gson();

                result = gson.fromJson(jsonString, DataResult.class);
            } else {
                // Debugging statement
                System.out.println("La respuesta no es un objeto JSON válido");
            }
        } catch (IOException e) {
            //Debugging statement
            System.out.println("Error al leer la respuesta: " + e.toString());
        } catch (JsonSyntaxException e) {
            // Debugging statement
            System.out.println("Error al analizar la respuesta JSON: " + e.toString());
        }

        // Returns the UserModel object
        return result;
    }

    /**
     * Creates HTTP petition, to get the data of all recipes
     */
    private Recipe[] loadRecipes() {

        Data d = null;
        String petition = URL + "principal";
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