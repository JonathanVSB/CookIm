package com.example.cookim.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivitySigninBinding;

public class SignActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);

    }
}
