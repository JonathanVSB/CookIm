package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecipeStepsActivity extends Activity {

    Model model;
    Executor executor = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        model = new Model();


        Intent intent = getIntent();
        int id = intent.getIntExtra("recipe_id", -1);
        System.out.println(id);
        executor.execute(new Runnable() {
            @Override
            public void run() {

                Recipe recipe = model.loadRecipeSteps(id);
                System.out.println("Funciona");
            }
        });


    }
}
