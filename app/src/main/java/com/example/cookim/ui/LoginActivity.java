package com.example.cookim.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    View.OnClickListener listener;
    Switch swLogOption;


    Model model;
    boolean validate = false;
    Controller controller = new Controller();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

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
            validate = validateUserServer(userData);

            if (validate) {
                Toast.makeText(this, "LOGIN CORRECT", Toast.LENGTH_LONG).show();
                showHomePage();
            } else {
                Toast.makeText(this, "credential incorrect", Toast.LENGTH_LONG).show();
            }
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
}
