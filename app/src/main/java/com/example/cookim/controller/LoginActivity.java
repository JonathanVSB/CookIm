package com.example.cookim.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookim.controller.HomePage;
import com.example.cookim.controller.Controller;
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
        initElements();


    }

    /**
     * Displays the home page of the app and senda the user object to next activity
     */
    private void showHomePage(UserModel user) {
        Intent intent = new Intent(this, HomePage.class);

        intent.putExtra("userModel", user);

        startActivity(intent);
    }


    /**
     * initialize all elements
     */
    private void initElements() {
        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction(v);
            }
        });

    }

    /**
     * Gets the parameters introduced by the user to log in the app
     * to prepare a validation of them.
     *
     * @param v
     */
    private void loginAction(View v) {
        if (v.getId() == binding.btLogin.getId()) {
            String name = binding.etUsername.getText().toString();
            String password = binding.etPass.getText().toString();
            LoginModel userData = new LoginModel(name, password);

            validation(userData);

        }
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
        String url = "http://91.107.198.64:7070/login";
        String username = loginModel.getUserName();
        String password = loginModel.getPassword();
        String parametros = "username=" + username + "&password=" + password;

        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //Background work here
                    //System.out.println("ENTRA");
                    UserModel userModel = readResponse(url, parametros);
                    if (userModel != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute

                                showHomePage(userModel);
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
    private UserModel readResponse(String urlString, String parameters) {
        boolean respuesta = false;
        UserModel user = null;
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
                user = parseUser(inputStream);

                //
                inputStream.close();

            }

        } catch (Exception e) {
            Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

        return user;
    }

    /**
     * Parse the Json response into UserModel Object
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
            jsonString = jsonString.replaceAll("\\\\u003d", ":");
            jsonString = jsonString.replaceAll("^\"|\"$", "");
            jsonString = jsonString.replaceAll("User\\{", "{");


            jsonString = jsonString.replaceAll(":(\\s*[^,\\s]+)(,|\\})", ":\"$1\"$2");

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

}


