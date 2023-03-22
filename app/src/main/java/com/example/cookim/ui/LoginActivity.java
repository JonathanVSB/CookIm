package com.example.cookim.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookim.HomePage;
import com.example.cookim.controller.Controller;
import com.example.cookim.databinding.ActivityLoginBinding;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
                validateUser(v);
            }
        });
    }

    private void validateUser(View v) {
        if (v.getId() == binding.btLogin.getId()) {
            String name = binding.etUsername.getText().toString();
            String password = binding.etPass.getText().toString();
            LoginModel userData = new LoginModel(name, password);
            //validate = validateUserServer(userData);
             validation(userData);

//            validate = Validation(userData);
//
//            if (validate) {
//                Toast.makeText(this, "LOGIN CORRECT", Toast.LENGTH_LONG).show();
//                showHomePage();
//            } else {
//                Toast.makeText(this, "credential incorrect", Toast.LENGTH_LONG).show();
//            }
        }
    }

    /**
     * Adds all necessaries elements to listener
     */
    private void addElementsToListener() {
        binding.btLogin.setOnClickListener(listener);


    }

    private boolean validateUserServer(LoginModel loginModel) {
        boolean validation = false;

        try {
            String url = "http://localhost:7070/login";
            String username = loginModel.getUserName();
            String password = loginModel.getPassword();
            String parametros = "username=" + username + "&password=" + password;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(parametros.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = con.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                UserModel[] users = new Gson().fromJson(in, UserModel[].class);
                List<UserModel> userList = Arrays.asList(users);
                for (UserModel user : userList) {
                    System.out.println(user.toString());
                }
                if (userList.size() > 0) {
                    validation = true;
                }
            }
        } catch (ProtocolException ex) {
            // ex.getMessage();
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        } catch (MalformedURLException ex) {
            //ex.getMessage();
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
            // ex.getMessage();
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();

        }


        return validation;

    }


    private void validation(LoginModel loginModel) {
        String url = "http://192.168.1.55:7070/login";
        String username = loginModel.getUserName();
        String password = loginModel.getPassword();
        String parametros = "username=" + username + "&password=" + password;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                readHttpWriteFile(url, parametros);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showHomePage();
                    }
                });
            }
        });
    }

    /**
     * Reads Http File and writes in internal Storage
     *
     * @param urlString      : Url to read File
     * @param parameters     : Parameters for the HTTP POST request
     */
    private void readHttpWriteFile(String urlString, String parameters) {
        int i = 0;
        try {
            // Petició HTTP
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

            // If the response does not enclose an entity, there is no need
            // to worry about connection release
            if (connection != null) {
                // Read Stream
                InputStream inputStream = connection.getInputStream();

                // Handle the response here (e.g., parse JSON, update UI, etc.)

                // Close the input stream
                inputStream.close();
            }

        } catch (Exception e) {
            System.out.println("Ex: " + i + e.toString());
        }
    }


}

