package com.example.cookim.controller;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TableRow;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.controller.Add.AddRecipeActivity;
import com.example.cookim.databinding.ActivityFavoritesBinding;
import com.example.cookim.databinding.ItemRecipeContentBinding;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class FavoritesActivity extends Activity {

    private ActivityFavoritesBinding binding;
    Model model;
    Controller controller;
    Executor executor;
    String token;
    Handler handler;
    List<Recipe> recipes;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = new Model();
        controller = new Controller();
        bottomNavigationViewClick();
        handler = new Handler(Looper.getMainLooper());
        token = model.readToken(getApplicationContext());
        executor = Executors.newSingleThreadExecutor();

        if (token != null){
            loadData();

        }else{

            controller.displayLogInPage(getApplicationContext(), LoginActivity.class);
        }


    }

    /**
     * Ask to server, the list of recipes saved as favorites
     * and displays it
     */
    private void loadData() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                recipes = model.getFavorites(token);

                if (recipes != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadView(recipes);
                        }
                    });
                }else{

                }
            }
        });


    }

    /**
     * displays the data in the view
     * @param recipes
     */
    private void loadView(List<Recipe> recipes) {

        binding.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            ItemRecipeContentBinding recipeBinding = ItemRecipeContentBinding.inflate(getLayoutInflater());

            recipeBinding.ivoptions.setVisibility(View.GONE);
            recipeBinding.tvPoster.setText(recipe.getUsername());
            recipeBinding.nameRecipe.setText(recipe.getName());
            recipeBinding.tvLikes.setText(recipe.getLikes());
            if (recipe.getPath_img()!= null){
                String portrait = model.downloadImg(recipe.getPath_img());
                Glide.with(this)
                        .load(portrait)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Handle image loading failure here
                                recipeBinding.img01.setImageResource(R.drawable.tostadas_de_pollo_con_lechuga);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // The image has been loaded successfully
                                return false;
                            }
                        })
                        .into(recipeBinding.img01);



            }
            TableRow row = new TableRow(FavoritesActivity.this);

            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.setMargins(100, 0, 0, 40); // Replace -50 with the number of pixels you want to move to the left
            row.setLayoutParams(params);

            row.addView(recipeBinding.getRoot());

            binding.tlData.addView(row);

        }

    }

    private void bottomNavigationViewClick() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    finish();
                    return true;
                case R.id.addrecipe:
                    controller.displayActivity(this, AddRecipeActivity.class);
                    return true;
                case R.id.searchrecipe:
                    // TODO: Implement settings screen
                    return true;
                default:
                    return false;
            }
        });

    }
}
