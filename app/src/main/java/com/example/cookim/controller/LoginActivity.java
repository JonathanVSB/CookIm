package com.example.cookim.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookim.databinding.ActivityLoginBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    View.OnClickListener listener;
    Switch swLogOption;

    private final String URL = "http://91.107.198.64:7070/Cookim/";
    private final String URL2 = "http://192.168.127.102:7070/Cookim/";
    ExecutorService executor;
    Handler handler;

    Model model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        setContentView(binding.getRoot());
        model = new Model();

        String username = binding.etUsername.getText().toString();
        binding.tvSignin.setOnClickListener(listener);
        initElements();




    }

    /**
     * Displays the home page of the app and senda the user object to next activity
     */
    private void showHomePage() {
        Intent intent = new Intent(this, HomePage.class);



        startActivity(intent);
    }


    /**
     * initialize all elements
     */
    private void initElements() {
        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPageActions(v);
            }
        });

        binding.tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPageActions(v);
            }
        });

    }


    /**
     * Gets the parameters introduced by the user to log in the app
     * to prepare a validation of them.
     *
     * @param v
     */
    private void loginPageActions(View v) {
        if (v.getId() == binding.btLogin.getId()) {
            String name = binding.etUsername.getText().toString();
            String password = binding.etPass.getText().toString();
            LoginModel userData = new LoginModel(name, password);

            validation(userData);

        } else if (v.getId() == binding.tvSignin.getId()) {
            displaySignInPage();

        }
    }

    /**
     * Displays Sign In Page
     */
    private void displaySignInPage() {
        Intent intent = new Intent(this, SignActivity.class);
        startActivity(intent);
    }

    /**
     * Adds all necessaries elements to listener
     */
    private void addElementsToListener() {
        binding.btLogin.setOnClickListener(listener);
    }


    /**
     * Prepares the http request and gestionated the action if validation is correct
     *
     * @param loginModel
     */
    private void validation(LoginModel loginModel) {
        String url = URL2 + "login";
        String username = loginModel.getUserName();
        String password = loginModel.getPassword();
        String parametros = "username=" + username + "&password=" + password;

        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //Background work here
                    //System.out.println("ENTRA");
//                    UserModel userModel = readResponse(url, parametros);
                    //String token = readResponse(url, parametros);
                    DataResult result = readResponse(url, parametros);
                    if (result.getResult().equals("1")) {
                        String token = result.getData().toString();
                        saveToken(result.getData().toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute

                                showHomePage();

                            }
                        });
                    } else {
                        //Toast.makeText(this, "credential incorrect", Toast.LENGTH_LONG).show();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.errormsg.setText(result.getData().toString());
                                // binding.errormsg.setText("Username or password are wrong");
                                binding.errormsg.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                }
            });

        } catch (Exception e) {
            binding.errormsg.setText("Error Connecting with server");
            binding.errormsg.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Reads Http File and writes in internal Storage
     *
     * @param urlString  : Url to read File
     * @param parameters : Parameters for the HTTP POST request
     */
    private DataResult readResponse(String urlString, String parameters) {

        DataResult result = null;
        int i = 0;
        try {
            //HTTP request
            System.out.println("ENTRA  " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Write parameters to the request
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(parameters.getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into UserModel object

                result = parseResponse(inputStream);

                //
                inputStream.close();

            }

        } catch (Exception e) {
            //Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

//        return DataResult;
        return result;
    }



    /**
     * Reads the token received from server and saves it String variable
     *
     * @param inputStream
     * @return
     */
    private DataResult parseResponse(InputStream inputStream) {

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
     * saves the token received by the server in a file only accessible from the application.
     * @param token
     */
    private void saveToken(String token) {
        // Gets an instance of the application context
        Context context = getApplicationContext();

        // Open the file in write mode and create the FileOutputStream object
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("token.txt", Context.MODE_PRIVATE);

            // Write the token string to the file
            outputStream.write(token.getBytes());

            // Closes the FileOutputStream object to release the resources
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


