package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityMyProfileBinding;
import com.example.cookim.databinding.ItemMyRecipeContentBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyProfileActivity extends Activity {


    Model model;
    Controller controller;
    String token;

    Executor executor;
    private ActivityMyProfileBinding binding;
    Handler handler;
    UserModel user;
    boolean followed;
    int id;
    long myId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigationViewClick();
        handler = new Handler(Looper.getMainLooper());
        model = new Model();
        controller = new Controller();
        token = model.readToken(getApplicationContext());
        executor = Executors.newSingleThreadExecutor();


        Intent intent = getIntent();

        myId = intent.getLongExtra("MyUserID", -1);
        id = intent.getIntExtra("userID", -1);


        //If theres token saved in file
        if (!token.isEmpty()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //find user by his token

                    if (myId == id) {
                        binding.btfollow.setVisibility(View.GONE);

                        user = model.myProfile(token);

                    } else {
                        user = model.userProfile(token, id);

                    }
                    //if user is not null
                    if (user != null) {

                        if (user.getFollow()) {
                            binding.btfollow.setText("Dejar de seguir");
                            int colorLightGray = Color.parseColor("#D3D3D3");
                            binding.btfollow.getBackground().setColorFilter(colorLightGray, PorterDuff.Mode.SRC_ATOP);


                        }
                        //search all recipes of the user
                        List<Recipe> recipes = model.userRecipes(token, user.getId());


                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadMyPage(user, recipes);
                            }
                        });


                    } else {
                        //if the server can't validate the token
                        System.out.println("usuario nulo");
                        displayLogInPage();

                    }
                }

            });
        } else {
            //if there's no token saved
            System.out.println("not token found");
            displayLogInPage();

        }


    }


    /**
     * load the data of the user in the view
     *
     * @param user
     * @param recipes
     */
    private void loadMyPage(UserModel user, List<Recipe> recipes) {
        binding.tvUsername.setText(user.getUsername());
        binding.tvName.setText(user.getFull_name());
        binding.tvDescription.setText(user.getDescription());
        binding.tvposts.setText(String.valueOf(recipes.size()));

        binding.btfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataResult res = null;
                if (user.getFollow()) {

                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            DataResult res = model.followUser(token, id, 0);
                            if (res.getResult().equals("1")) {
                                Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_background);
                                binding.btfollow.setBackground(gradientDrawable);
                                binding.btfollow.setText("Seguir");
                                user.setFollow(false);

                            }

                        }
                    });

                } else {

                    executor.execute(new Runnable() {
                        @Override
                        public void run() {

                            DataResult res = model.followUser(token, id, 1);
                            if (res.getResult().equals("1")) {
                                int colorLightGray = Color.parseColor("#D3D3D3");
                                binding.btfollow.getBackground().setColorFilter(colorLightGray, PorterDuff.Mode.SRC_ATOP);
                                binding.btfollow.setText("Dejar de seguir");
                                user.setFollow(true);

                            }
                        }
                    });

                }
            }
        });

        String img = model.downloadImg(user.getPath_img());
        Glide.with(MyProfileActivity.this)
                .load(img)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Manejar el fallo de carga de la imagen aquí
                        binding.userimg.setImageResource(R.drawable.tostadas_de_pollo_con_lechuga);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // La imagen se ha cargado correctamente
                        return false;
                    }
                })
                .into(binding.userimg);
        //if the user has recipes in data base
        if (recipes.size() > 0) {

            for (int i = 0; i < recipes.size(); i++) {
                Recipe recipe = recipes.get(i);
                ItemMyRecipeContentBinding recipeBinding = ItemMyRecipeContentBinding.inflate(getLayoutInflater());

                if (myId != id){
                    recipeBinding.ivoptions.setVisibility(View.GONE);
                }
                recipeBinding.nameRecipe.setText(recipe.getName());
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
                    public void onClick(View v) {
                        displayDetails(recipe.getId());
                    }
                });

                // Create a new row for the table
                TableRow row = new TableRow(MyProfileActivity.this);

                TableRow.LayoutParams params = new TableRow.LayoutParams();
                params.setMargins(100, 0, 0, 40); // Replace -50 with the number of pixels you want to move to the left
                row.setLayoutParams(params);

                row.addView(recipeBinding.getRoot());

                binding.tlRecipes.addView(row);


            }

        }
    }

    /**
     * Display the login Page
     */
    private void displayLogInPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * display the details of the recipe selected
     *
     * @param id
     */
    public void displayDetails(int id) {
        Intent intent = new Intent(this, RecipeStepsActivity.class);
        intent.putExtra("recipe_id", id);
        startActivity(intent);
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

    /**
     * Displays the home page of the app and senda the user object to next activity
     */
    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
