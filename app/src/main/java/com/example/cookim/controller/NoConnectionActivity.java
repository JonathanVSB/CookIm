package com.example.cookim.controller;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.cookim.databinding.ActivityNoConnectionBinding;

public class NoConnectionActivity extends Activity {


    private ActivityNoConnectionBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoConnectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
