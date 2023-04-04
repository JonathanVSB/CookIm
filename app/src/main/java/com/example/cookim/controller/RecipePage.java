package com.example.cookim.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.cookim.R;
import com.example.cookim.databinding.ItemRecipeContentBinding;
import com.example.cookim.model.recipe.Recipe;

public class RecipePage extends Activity {

    private ItemRecipeContentBinding binding;
    View.OnClickListener listener;

    Recipe recipe;
    Boolean press;
    public RecipePage(Recipe recipe) {
        this.recipe = recipe;
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_recipe_content);
        binding = ItemRecipeContentBinding.inflate(getLayoutInflater());
        binding.btLike.setOnClickListener(listener);
        initElements();

        binding.nameRecipe.setText(recipe.getName());
        binding.tvLikes.setText(recipe.getLikes());
        press = false;


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

        if (v.getId() == binding.btLike.getId() && !press) {
            binding.btLike.setImageResource(R.drawable.selectedheart);
            int likes = Integer.parseInt(binding.tvLikes.getText().toString());
            likes++;
            binding.tvLikes.setText(Integer.toString(likes));
            press = true;


        }else if (v.getId() == binding.btLike.getId() && press) {
            binding.btLike.setImageResource(R.drawable.nonselectedheart);
            int likes = Integer.parseInt(binding.tvLikes.getText().toString());
            likes--;
            binding.tvLikes.setText(Integer.toString(likes));
            press = false;

        }

    }
}
