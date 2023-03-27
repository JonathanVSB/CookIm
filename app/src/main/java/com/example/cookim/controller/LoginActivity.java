package com.example.cookim.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookim.databinding.ActivityLoginBinding;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    View.OnClickListener listener;
    Switch swLogOption;

    private final String URL = "http://91.107.198.64:7070/";
    private final String URL2 = "http://192.168.127.101:7070/";
    ExecutorService executor;
    Handler handler;

    Model model;
    boolean validate = false;
    Controller controller = new Controller();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    private void showHomePage(String token) {
        Intent intent = new Intent(this, HomePage.class);

        intent.putExtra("token", token);

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
                    String token = readResponse(url, parametros);
                    if (!token.equals("null")/*userModel != null*/) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute

                                showHomePage(token);

                            }
                        });
                    } else {
                        //Toast.makeText(this, "credential incorrect", Toast.LENGTH_LONG).show();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.errormsg.setVisibility(View.VISIBLE);
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
     * Reads Http File and writes in internal Storage
     *
     * @param urlString  : Url to read File
     * @param parameters : Parameters for the HTTP POST request
     */
    private String readResponse(String urlString, String parameters) {
        String token = "null";
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

                token = parseToken(inputStream);

                //
                inputStream.close();

            }

        } catch (Exception e) {
            Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

//        return user;
        return token;
    }

    /**
     * Parse the Json response into UserModel Object
     *
     * @param inputStream
     * @return user
     */
    private UserModel parseUser(InputStream inputStream) {
        UserModel user = null;

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
            String jsonString = stringBuilder.toString();

            // Debugging statement
            System.out.println("Respuesta JSON modificada: " + jsonString);

            // Checks if the modified string starts and ends with "{" and "}"
            if (jsonString.trim().startsWith("{") && jsonString.trim().endsWith("}")) {

                Gson gson = new Gson();

                user = gson.fromJson(jsonString, UserModel.class);
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
        return user;
    }

    private String parseToken(InputStream inputStream) {

        String jsonString = null;

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
            jsonString = stringBuilder.toString();

            // Debugging statement
            System.out.println("Respuesta JSON modificada: " + jsonString);

            // Converts the StringBuilder object to a string and modifies it

            // Closes the BufferedReader
            bufferedReader.close();

            jsonString = jsonString.replace("\"", "");

            // Debugging statement
            System.out.println("Respuesta JSON: " + jsonString);

        } catch (IOException e) {
            //Debugging statement
            System.out.println("Error al leer la respuesta: " + e.toString());
        }

        // Returns the JSON string or null if there was an error
        return jsonString;
    }
}


