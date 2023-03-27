package com.example.cookim.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivityLoginBinding;
import com.example.cookim.databinding.ItemRecipeContentBinding;
import com.example.cookim.model.user.LoginModel;

public class RecipePage extends Activity {

    private ItemRecipeContentBinding binding;
    View.OnClickListener listener;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_recipe_content);
        binding = ItemRecipeContentBinding.inflate(getLayoutInflater());


        binding.btLike.setOnClickListener(listener);
        initElements();

    }

    private void initElements() {
        binding.btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageActions(v);
            }
        });


    }
    private void pageActions(View v) {
        if (v.getId() == binding.btLike.getId()) {
            binding.btLike.setImageResource(R.drawable.selectedheart);


        }
    }
}
