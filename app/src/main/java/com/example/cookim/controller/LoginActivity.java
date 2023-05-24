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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    View.OnClickListener listener;
    Switch swLogOption;

    ExecutorService executor;
    Handler handler;

    Model model;
    Controller controller;

    /**
     * Called when the activity is starting. Performs initialization of the activity, such as
     * inflating the layout, setting up UI elements, and initializing the model and controller.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle). Note: Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        model  = new Model(this);
        controller = new Controller();

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
     * Performs the actions associated with the login page elements.
     *
     * @param v The View object that represents the clicked element.
     */
    private void loginPageActions(View v) {
        if (v.getId() == binding.btLogin.getId()) {
            if (!TextUtils.isEmpty(binding.etUsername.getText()) && !TextUtils.isEmpty(binding.etPass.getText())) {
                String name = binding.etUsername.getText().toString();
                String pass = binding.etPass.getText().toString();
                String password = getSHA256(pass);
                System.out.println("Pass: "+password);
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
     * Performs validation for the login model.
     *
     * @param loginModel The login model containing the username and password.
     */
    private void validation(LoginModel loginModel) {
        String username = loginModel.getUserName();
        String password = loginModel.getPassword();
        String parametros = username + ":" + password;

        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {

                    DataResult result = model.login(parametros, getApplicationContext());
                    if (result.getResult().equals("1")) {
                        String token = result.getData().toString();
                        saveToken(result.getData().toString());

                        controller.displayActivity(getApplicationContext(), HomeActivity.class);
                        finish();
                    } else if (result.getResult().equals("0000")) {
                        controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
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
     * Saves the token to a file.
     *
     * @param token The token to be saved.
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

    /**
     * Generates the SHA-256 hash of the input string.
     *
     * @param input The input string to be hashed.
     * @return The SHA-256 hash of the input string.
     */
    public static String getSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error al generar hash SHA-256: " + e.getMessage());
            return null;
        }
    }

}


