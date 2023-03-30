package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivityMainBinding;
import com.example.cookim.databinding.ActivityPresentationBinding;

public class MainActivity extends Activity {


    private ActivityMainBinding binding;

    private final String URL = "http://91.107.198.64:7070/";
    private final String URL2 = "http://192.168.127.102:7070/";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        displayLogInPage();


    }

    /**
     * Display the login Page
     */
    private void displayLogInPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
