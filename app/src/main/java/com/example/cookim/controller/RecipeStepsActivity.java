package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.databinding.ActivityStepsBinding;
import com.example.cookim.databinding.ItemStepContentBinding;
import com.example.cookim.exceptions.PersistException;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.recipe.Step;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeStepsActivity extends Activity {

    Model model;
    Controller controller;
    ExecutorService executor;
    Handler handler;
    long myId;
    String token;
    Recipe recipe;

    private ActivityStepsBinding binding;
    //    TextView tv;
    private ItemStepContentBinding bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
        model  = new Model(this);
        controller = new Controller();
        binding = ActivityStepsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        token = model.readFile(getApplicationContext(), "token");
        bind = ItemStepContentBinding.inflate(getLayoutInflater());


        Intent intent = getIntent();
        int id = intent.getIntExtra("recipe_id", -1);

        myId = intent.getLongExtra("MyUserID", -1);
        System.out.println(id);
        executor.execute(new Runnable() {
            @Override
            public void run() {


                try {
                    recipe = model.loadRecipeSteps(id, token, getApplicationContext());
                    System.out.println("Funciona");

                    if (recipe != null) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadPage(recipe);
                                setContentView(binding.getRoot());
                            }
                        });


                    } else {
                        displayLogInPage();

                    }

                } catch (PersistException e) {
                    controller.displayErrorView(getApplicationContext(), e.getCode());

                }

            }

        });


    }

    /**
     * Load the data of the steps page using the data of the recipe
     *
     * @param recipe
     */
    private void loadPage(Recipe recipe) {
        if (recipe != null) {
            binding.tvnameRecipe.setText(recipe.getName());
            binding.tvDescription.setText(recipe.getDescription());
            binding.tvLikes.setText(String.valueOf(recipe.getLikes()));
            binding.btSave.setImageResource(recipe.isSaved() ? R.drawable.oven2 : R.drawable.oven);
            binding.btLike.setImageResource(recipe.isLiked() ? R.drawable.selectedheart : R.drawable.nonselectedheart);

            if (recipe != null && recipe.getSteps() != null) {
                binding.tvnumSteps.setText(String.valueOf(recipe.getSteps().size()));
            } else {
                binding.tvnumSteps.setText("0");
            }

            //if the recipe has frontpage picture
            if (!recipe.getPath_img().isEmpty()) {
                String img = model.downloadImg(recipe.getPath_img());
                Glide.with(RecipeStepsActivity.this)
                        .load(img)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Manejar el fallo de carga de la imagen aquí
                                binding.frontpage.setImageResource(R.drawable.tostadas_de_pollo_con_lechuga);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // La imagen se ha cargado correctamente
                                return false;
                            }
                        })
                        .into(binding.frontpage);
            }

            //If the cooker of the recipe has profile picture


            if (recipe.getPath() != null) {
                String img2 = model.downloadImg(recipe.getPath());
                Glide.with(RecipeStepsActivity.this)
                        .load(img2)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Manejar el fallo de carga de la imagen aquí
                                binding.userimg.setImageResource(R.drawable.guest_profile);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // La imagen se ha cargado correctamente
                                return false;
                            }
                        })
                        .into(binding.userimg);
                System.out.println("imagen de usuario cargada");

            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.userimg.setImageResource(R.drawable.guest_profile);
                    }
                });
            }


        }

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            String ingrediente = recipe.getIngredients().get(i).getName();

            // Crea una nueva fila para la tabla
            TableRow row = new TableRow(RecipeStepsActivity.this);

            // Crea un TextView para el ingrediente y lo agrega a la fila
            TextView tvIngrediente = new TextView(RecipeStepsActivity.this);
            tvIngrediente.setText(ingrediente);

            tvIngrediente.setPadding(80, 10, 0, 10);
            tvIngrediente.setTextSize(20);
            tvIngrediente.setTextColor(Color.BLACK);



            row.addView(tvIngrediente);


            binding.tlIngredients.addView(row);
//

        }
        System.out.println("cargada con los datos");

        //carga de items de los pasos
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            Step step = recipe.getSteps().get(i);

            // Inflar el layout item_step_content
            ItemStepContentBinding stepBinding = ItemStepContentBinding.inflate(getLayoutInflater());

            stepBinding.stepnum.setText(String.valueOf(step.getStep_number()));
            stepBinding.tvDescription.setText(step.getDescription());
            if (step.getPath() != null) {
                String img = model.downloadImg(step.getPath());
                Glide.with(this)
                        .load(img)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Handle image loading failure here
                                stepBinding.stepPic.setImageResource(R.drawable.tostadas_de_pollo_con_lechuga);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // The image has been loaded successfully
                                return false;
                            }
                        })
                        .into(stepBinding.stepPic);
            }

            // Create a new row for the table
            TableRow row = new TableRow(RecipeStepsActivity.this);

            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.setMargins(100, 0, 0, 0); // Replace -50 with the number of pixels you want to move to the left
            row.setLayoutParams(params);

            row.addView(stepBinding.getRoot());

            // Add the row to the table in the main thread
            binding.tlsteps.addView(row);
        }

        binding.btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                controller.displayCommentPage(getApplicationContext(), CommentActivity.class, recipe.getId());


            }
        });

        binding.userimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.displayMyProfile(getApplicationContext(), MyProfileActivity.class, myId, recipe.getId_user());
            }
        });

        binding.btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pressLike = !recipe.isLiked();
                recipe.setLiked(pressLike);

                recipe.setLikes(pressLike ? recipe.getLikes() + 1 : recipe.getLikes() - 1);
                binding.btLike.setImageResource(recipe.isLiked() ? R.drawable.selectedheart : R.drawable.nonselectedheart);
                binding.tvLikes.setText(String.valueOf(recipe.getLikes()));

                //send 1 if user likes the recipe, 0 if unlikes
                int likeValue = pressLike ? 1 : 0;

                DataResult result = sendLike(likeValue, String.valueOf(recipe.getId()));
                if (result != null && result.getResult() != null && result.getResult().equals("1")) {
                    try {
                        binding.tvLikes.setText(String.valueOf(recipe.getLikes()));
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }else if (result.getResult().equals("0000")) {
                    controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
                } else {

//                            pressLike = false;
                }
            }
        });

        binding.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pressSave = !recipe.isSaved();
                recipe.setSaved(pressSave);

                int saveValue = recipe.isSaved() ? 1 : 0;
                DataResult result = saveRecipe(saveValue, String.valueOf(recipe.getId()));

                if (result != null && result.getResult() != null && result.getResult().equals("1")) {
                    try {
                       binding.btSave.setImageResource(recipe.isSaved() ? R.drawable.oven2 : R.drawable.oven);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }else if (result.getResult().equals("0000")) {
                    controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
                } else {
                    recipe.setSaved(!recipe.isSaved());
                }
            }


        });
    }

    /**
     * Display the login Page
     */
    private void displayLogInPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Likes the recipe
     * @param num
     * @param id
     * @return
     */
    private DataResult sendLike(int num, String id) {
        String numero = String.valueOf(num);
        String parametros = token + ":" + numero + ":" + id;
        DataResult result = null;

        try {

            result = executor.submit(() -> {
                return model.likeRecipe(parametros, getApplicationContext());
            }).get();
        } catch (Exception e) {
            System.out.println("Error al enviar like: " + e.getMessage());
        }

        return result;
    }

    /**
     * saves the Recipe
     * @param num
     * @param id
     * @return
     */
    private DataResult saveRecipe(int num, String id) {
        String numero = String.valueOf(num);
        String parametros = token + ":" + numero + ":" + id;
        DataResult result = null;

        try {

            result = executor.submit(() -> {
                return model.saveRecipe(parametros, getApplicationContext());
            }).get();
        } catch (Exception e) {
            System.out.println("Error al enviar like: " + e.getMessage());
        }

        return result;
    }



}






