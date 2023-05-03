package com.example.cookim.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityAddRecipeBinding;

public class AddRecipeActivity extends AppCompatActivity {

    private ActivityAddRecipeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gestionateProcess();
    }

    /**
     * receive cancel and continue button.
     * if cancel button is pressed. resturns to home page
     * if accpt button is pressed, send petition to create new Recipe
     */
    private void gestionateProcess() {

        binding.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHomePage();
            }
        });
    }

    /**
     * Displays the home page of the app and senda the user object to next activity
     */
    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
