package com.example.cookim.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivityMainBinding;
import com.example.cookim.databinding.ActivityPresentationBinding;
import com.example.cookim.model.DataResult;
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

public class PresentationActivity extends Activity {


    private ActivityPresentationBinding binding;

    private final String URL = "http://91.107.198.64:7070/";
    private final String URL2 = "http://192.168.127.102:7070/";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String token = readToken();


        if (readToken() == null) {
            displayLogInPage();

        } else if (readToken() != null) {
            String url = URL2 + "/autologin";
            DataResult result = validateToken(url, token);

            if (result.getResult() == "1") {
                showHomePage(token);
            } else {
                displayLogInPage();

            }

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
     * Do petition
     * @param urlString
     * @param token
     * @return
     */
    private DataResult validateToken(String urlString, String token) {
        DataResult result = null;
        try {
            // HTTPS request
            System.out.println("ENTRA  " + urlString);
            java.net.URL url = new URL(urlString);
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

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into DataResult object
                result = parseDataResult(inputStream);

                inputStream.close();
            } else {
                // Debugging statement
                System.out.println("Error al conectar con el servidor: " + connection.getResponseMessage());
            }

            connection.disconnect();

        } catch (Exception e) {
            // Debugging statement
            System.out.println("Error al conectar con el servidor: " + e.toString());
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
        String jsonString = null;
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

            // Converts the StringBuilder object to a string
            jsonString = stringBuilder.toString();

            // Debugging statement
            System.out.println("Respuesta JSON: " + jsonString);

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
        }

        // Returns the DataResult object or null if there was an error
        return result;
    }

    /**
     * display home page view
     * @param token
     */
    private void showHomePage(String token) {
        Intent intent = new Intent(this, HomePage.class);

        intent.putExtra("token", token);

        startActivity(intent);
    }
}
