package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityAddCategoryBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Category;
import com.example.cookim.model.recipe.Recipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AddCategoryActivity extends Activity {

    private ActivityAddCategoryBinding binding;
    ExecutorService executor;
    Handler handler;
    String token;
    Model model;
    Controller controller;
    List<Category> categoryList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
        model = new Model(this);
        controller = new Controller();
        token = model.readFile(getApplicationContext(), "token");

        Intent intent = getIntent();
        Recipe recipe = intent.getParcelableExtra("recipe");

//        DataResult res = model.createRecipe(recipe, token, portraitFile,getApplicationContext());
//
//        if (res.getResult().equals("1")) {
//            controller.displayActivity(getApplicationContext(), HomeActivity.class);
//        } else {
//
//            controller.displayErrorMessage(getApplicationContext(),"Algo ha salido mal. La receta no ha sido creada");
//
//        }


    }
}
