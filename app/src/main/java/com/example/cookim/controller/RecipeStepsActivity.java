package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityHomeBinding;
import com.example.cookim.databinding.ActivityStepsBinding;
import com.example.cookim.databinding.ItemStepContentBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.recipe.Step;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecipeStepsActivity extends Activity {

    Model model;
    Executor executor = Executors.newSingleThreadExecutor();
    Handler handler;
    private ActivityStepsBinding binding;
    //    TextView tv;
    private ItemStepContentBinding bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        handler = new Handler(Looper.getMainLooper());
        model = new Model();
        binding = ActivityStepsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bind = ItemStepContentBinding.inflate(getLayoutInflater());


        Intent intent = getIntent();
        int id = intent.getIntExtra("recipe_id", -1);
        System.out.println(id);
        executor.execute(new Runnable() {
            @Override
            public void run() {

                Recipe recipe = model.loadRecipeSteps(id);
                System.out.println("Funciona");

                if (recipe != null) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadPage(recipe);
                        }
                    });


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



            if (recipe.getPath() !=null)
            {
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

            }else
            {
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

            tvIngrediente.setPadding(80, 0, 0, 0);
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

            // Configurar el contenido de los elementos de la vista inflada
            stepBinding.stepnum.setText(String.valueOf(step.getStep_number()));
            stepBinding.tvDescription.setText(step.getDescription());
//                if (!step.getPath().isEmpty()) {
////                            String img = model.downloadImg(step.getPath());
////                            Glide.with(RecipeStepsActivity.this)
////                                    .load(img)
////                                    .listener(new RequestListener<Drawable>() {
////                                        @Override
////                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
////                                            // Manejar el fallo de carga de la imagen aquí
////                                            bind.stepPic.setImageResource(R.drawable.tostadas_de_pollo_con_lechuga);
////                                            return false;
////                                        }
////
////                                        @Override
////                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
////                                            // La imagen se ha cargado correctamente
////                                            return false;
////                                        }
////                                    })
////                                    .into(bind.stepPic);

            // Crear una nueva fila para la tabla
            TableRow row = new TableRow(RecipeStepsActivity.this);


            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.setMargins(100, 0, 0, 0); // Reemplazar -50 con la cantidad de píxeles que deseas mover hacia la izquierda
            row.setLayoutParams(params);

            row.addView(stepBinding.getRoot());

            // Agregar la fila a la tabla en el hilo principal

            binding.tlsteps.addView(row);

        }
    }
}






