package com.example.cookim.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityLoginBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;

import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    View.OnClickListener listener;
    Switch swLogOption;

    private final String URL = "http://91.107.198.64:7070/Cookim/";
    private final String URL2 = "http://192.168.127.102:7070/Cookim/";

    private final String URL3 = "http://192.168.127.94:7070/Cookim/";
    ExecutorService executor;
    Handler handler;

    Model model;


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
    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
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
            if (!TextUtils.isEmpty(binding.etUsername.getText()) && !TextUtils.isEmpty(binding.etPass.getText())) {
                String name = binding.etUsername.getText().toString();
                String password = binding.etPass.getText().toString();
                LoginModel userData = new LoginModel(name, password);
                binding.errormsg.setVisibility(View.INVISIBLE);
                validation(userData);
            } else {

                binding.errormsg.setText("Debes rellenar todos los campos");
                binding.errormsg.setVisibility(View.VISIBLE);
            }


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
        String url = URL3 + "login";
        String username = loginModel.getUserName();
        String password = loginModel.getPassword();
        String parametros = username + ":" + password;

        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //Background work here
                    //System.out.println("ENTRA");
//                    UserModel userModel = readResponse(url, parametros);
                    //String token = readResponse(url, parametros);
                    DataResult result = model.login(parametros);/*readResponse(url, parametros);*/
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
     * saves the token received by the server in a file only accessible from the application.
     *
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


