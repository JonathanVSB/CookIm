package com.example.cookim.controller;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.cookim.HomePage;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {


    private List<UserModel> userList = new ArrayList<>();

    Model model;

    public Controller() {

        userList.add(new UserModel("user1", "user1"));
        userList.add(new UserModel("alumne", "alumne"));
    }

    private void HomePage() {

    }

    public boolean validateUser(LoginModel loginModel) {
        boolean result = false;

        //Search matches in list of users
        for (UserModel usr : userList) {
            //if find matches result is true
            if (usr.getUsername().equals(loginModel.getUserName()) && usr.getPass().equals(loginModel.getPassword())) {
                result = true;
            }

        }

        return result;
    }

    public boolean validateUserServer(LoginModel loginModel) {
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
                if (userList.size()>0){
                    validation = true;
                }
            }
        } catch (ProtocolException ex) {
           // ex.getMessage();
            //Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        } catch (MalformedURLException ex) {
            //ex.getMessage();
            //Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
           // ex.getMessage();
            //Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();

        }


        return validation;

    }
}
