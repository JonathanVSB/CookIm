package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TableRow;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.databinding.ActivitySearchRecipeBinding;
import com.example.cookim.databinding.ItemRecipeResultBinding;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchRecipeActivity extends Activity {

    Model model;
    Controller controller;
    private ActivitySearchRecipeBinding binding;
    ExecutorService executor;
    Handler handler;
    String token;
    long myId;
    List<Recipe> recipeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipe);
        binding = ActivitySearchRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = new Model();
        controller = new Controller();
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        recipeList = null;
        Intent intent = getIntent();
        myId = intent.getLongExtra("MyUserID", -1);
        token = model.readToken(getApplicationContext());

        if (token != null && !token.isEmpty()) {

            loadPage();
        } else {

            controller.displayLogInPage(this, LoginActivity.class);

        }


    }

    /**
     * creates the elements to make the view work
     */
    private void loadPage() {
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            recipeList = model.searchRecipe(token, query,getApplicationContext());

                            if (recipeList != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.tableView.removeAllViews();

                                        for (Recipe recipe : recipeList) {
                                            TableRow row = new TableRow(SearchRecipeActivity.this);
                                            ItemRecipeResultBinding resultBinding = ItemRecipeResultBinding.inflate(getLayoutInflater());

                                            resultBinding.steptitle.setText(recipe.getName());

                                            if (!recipe.getPath_img().isEmpty()) {
                                                String img = model.downloadImg(recipe.getPath_img());
                                                Glide.with(SearchRecipeActivity.this)
                                                        .load(img)
                                                        .into(resultBinding.img01);
                                            }

                                            resultBinding.recipeCard.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    controller.displayRecipeDetails(getApplicationContext(), RecipeStepsActivity.class, recipe.getId(),myId);
                                                }
                                            });

                                            row.addView(resultBinding.getRoot());
                                            binding.tableView.addView(row);
                                        }
                                    }
                                });

                            } else {
                                //TODO not matches
                            }


                        }
                    });
                } else {
                    // TODO: Textfield is empty
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


}
