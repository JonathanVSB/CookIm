package com.example.cookim.controller;

import android.content.Intent;
import android.util.Log;

import com.example.cookim.HomePage;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Controller {


    private List<UserModel> userList = new ArrayList<>();

    Model model;
    public Controller(){

        userList.add(new UserModel("user1", "user1"));
        userList.add(new UserModel("alumne", "alumne"));
    }

    private void HomePage() {

    }

    public boolean validateUser(LoginModel loginModel) {
        boolean result = false;

        //Search matches in list of users
        for (UserModel usr: userList) {
            //if find matches result is true
            if (usr.getUsername().equals(loginModel.getUserName()) && usr.getPass().equals(loginModel.getPassword())){
                result = true;
            }

        }

        return result;
    }

    public boolean validateUserServer(LoginModel loginModel){
        boolean validation = false;
            String url = "http://192.168.127.8:7070/login";
            String username = loginModel.getUserName();
            String password = loginModel.getPassword();
            String parametros = "username=" + username + "&password=" + password;

        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("Respuesta del servidor: " + response.toString());

//            User[] users = new Gson().fromJson(in, User[].class);
//            for (User user : users) {
//                System.out.println(user.toString());
//            }

            }
        }
    }



        return validation;
    }

}
