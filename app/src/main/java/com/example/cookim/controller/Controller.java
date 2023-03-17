package com.example.cookim.controller;

import android.content.Intent;
import android.util.Log;

import com.example.cookim.HomePage;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;

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

}
