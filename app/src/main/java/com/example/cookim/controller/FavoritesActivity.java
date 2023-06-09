package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
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
import com.example.cookim.exceptions.PersistException;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FavoritesActivity extends Activity {

    private ActivityFavoritesBinding binding;
    Model model;
    Controller controller;
    ExecutorService executor;
    String token;
    Handler handler;
    List<Recipe> recipes;
    long myId;

    /**
     * Called when the activity is starting. Initializes the activity and sets up the UI elements.
     *
     * @param savedInstanceState the saved instance state bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        model = new Model(this);
        controller = new Controller();
        bottomNavigationViewClick();
        handler = new Handler(Looper.getMainLooper());
        token = model.readFile(getApplicationContext(), "token");
        executor = Executors.newSingleThreadExecutor();
        Intent intent = getIntent();

        myId = intent.getLongExtra("MyUserID", -1);

        binding.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (token != null) {
            loadData();

        } else {

            controller.displayLogInPage(getApplicationContext(), LoginActivity.class);
        }


    }

    /**
     * Loads the favorite recipes data from the model and updates the UI.
     */
    private void loadData() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    recipes = model.getFavorites(token, getApplicationContext());

                    if (recipes != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadView(recipes);
                            }
                        });
                    } else {

                    }
                } catch (PersistException e) {
                    controller.displayErrorView(FavoritesActivity.this, e.getCode());

                }


            }
        });


    }

    /**
     * displays the data in the view
     *
     * @param recipes The list of favorite recipes to be loaded.
     */
    private void loadView(List<Recipe> recipes) {


        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            ItemRecipeContentBinding recipeBinding = ItemRecipeContentBinding.inflate(getLayoutInflater());
            recipe.setSaved(true);


            recipeBinding.ivoptions.setVisibility(View.GONE);
            recipeBinding.tvPoster.setText(recipe.getUsername());
            recipeBinding.nameRecipe.setText(recipe.getName());
            recipeBinding.tvLikes.setText(String.valueOf(recipe.getLikes()));
            recipeBinding.btLike.setImageResource(recipe.isLiked() ? R.drawable.selectedheart : R.drawable.nonselectedheart);
            recipeBinding.btSave.setImageResource(recipe.isSaved() ? R.drawable.oven2 : R.drawable.oven);

            if (recipe.getPath_img() != null) {
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

            recipeBinding.imageContentCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controller.displayRecipeDetails(getApplicationContext(), RecipeStepsActivity.class, recipe.getId(), myId);
                }
            });

            recipeBinding.btComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controller.displayCommentPage(getApplicationContext(), CommentActivity.class, recipe.getId());
                }
            });

            recipeBinding.btSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean pressSave = !recipe.isSaved();
                    recipe.setSaved(pressSave);

                    int saveValue = recipe.isSaved() ? 1 : 0;
                    DataResult result = saveRecipe(saveValue, String.valueOf(recipe.getId()));

                    if (result.getResult().equals("1")) {
                        try {
                            recipeBinding.btSave.setImageResource(recipe.isSaved() ? R.drawable.oven2 : R.drawable.oven);
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    } else if (result.getResult().equals("0000")) {
                        controller.displayActivity(getApplicationContext(), NoConnectionActivity.class);
                    } else {
                        recipe.setSaved(!recipe.isSaved());
                    }
                }
            });

            recipeBinding.btLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean pressLike = !recipe.isLiked();
                    recipe.setLiked(pressLike);

                    recipe.setLikes(pressLike ? recipe.getLikes() + 1 : recipe.getLikes() - 1);
                    recipeBinding.btLike.setImageResource(recipe.isLiked() ? R.drawable.selectedheart : R.drawable.nonselectedheart);
                    recipeBinding.tvLikes.setText(String.valueOf(recipe.getLikes()));

                    //send 1 if user likes the recipe, 0 if unlikes
                    int likeValue = pressLike ? 1 : 0;

                    DataResult result = sendLike(likeValue, String.valueOf(recipe.getId()));
                    if (result.getResult().equals("1")) {
                        try {
                            recipeBinding.tvLikes.setText(String.valueOf(recipe.getLikes()));
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    } else if (result.getResult().equals("0000")) {
                        controller.displayActivity(getApplicationContext(), NoConnectionActivity.class);
                    } else {

//                            pressLike = false;
                    }
                }


            });

            TableRow row = new TableRow(FavoritesActivity.this);

            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.setMargins(0, 0, 30, 40); // Replace -50 with the number of pixels you want to move to the left
            row.setLayoutParams(params);
            row.setGravity(Gravity.CENTER);

            row.addView(recipeBinding.getRoot());

            binding.tlData.addView(row);

        }

    }

    /**
     * Saves a recipe with the specified parameters.
     *
     * @param num The number value (1 or 0) indicating whether to save or unsave the recipe.
     * @param id The ID of the recipe to be saved.
     * @return The data result of the save operation.
     */
    private DataResult saveRecipe(int num, String id) {
        String numero = String.valueOf(num);
        String parametros = token + ":" + numero + ":" + id;
        DataResult result = null;

        try {

            result = executor.submit(() -> {
                return model.saveRecipe(parametros, this);
            }).get();
        } catch (Exception e) {
            System.out.println("Error al enviar like: " + e.getMessage());
        }

        return result;
    }

    /**
     * Sends a like for a recipe with the specified parameters.
     *
     * @param num The number value (1 or 0) indicating whether to like or unlike the recipe.
     * @param id The ID of the recipe to be liked.
     * @return The data result of the like operation.
     */
    private DataResult sendLike(int num, String id) {
        String numero = String.valueOf(num);
        String parametros = token + ":" + numero + ":" + id;
        DataResult result = null;

        try {

            result = executor.submit(() -> {
                return model.likeRecipe(parametros, this);
            }).get();
        } catch (Exception e) {
            System.out.println("Error al enviar like: " + e.getMessage());
        }

        return result;
    }

    /**
     * Handles the click events for items in the bottom navigation view.
     */
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
