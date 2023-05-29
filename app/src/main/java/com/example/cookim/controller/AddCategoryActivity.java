package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.controller.Add.AddRecipeActivity;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityAddCategoryBinding;
import com.example.cookim.exceptions.PersistException;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Category;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.recipe.Step;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
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
    List<Step> stepsList;
    Recipe recipe;
    long recipe_id;

    boolean bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9;

    /**
     * Called when the activity is starting or restarting.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
        model = new Model(this);
        controller = new Controller();
        categoryList = new ArrayList<>();
        token = model.readFile(getApplicationContext(), "token");

        Intent intent = getIntent();

        recipe_id = intent.getLongExtra("recipe_id", -1);

        initBoolean();
        if (recipe_id != -1) {

            loadPage();


        }

    }

    /**
     * inits the booleans of the buttons
     */
    private void initBoolean() {
        bt1 = false;
        bt2 = false;
        bt3 = false;
        bt4 = false;
        bt5 = false;
        bt6 = false;
        bt7 = false;
        bt8 = false;
        bt9 = false;

    }

    /**
     * Load the page and configure the button listeners.
     */
    private void loadPage() {


        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt1) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button1.setBackground(gradientDrawable);

                    Category cat = new Category("Fitness");
                    categoryList.add(cat);
                    bt1 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button1.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("Fitness")) {
                            categoryList.remove(i);
                        }
                    }
                    bt1 = false;
                }

            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt2) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button2.setBackground(gradientDrawable);

                    Category cat = new Category("Vegana");
                    categoryList.add(cat);
                    bt2 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button2.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("Vegana")) {
                            categoryList.remove(i);
                        }
                    }
                    bt2 = false;
                }

            }
        });
        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt3) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button3.setBackground(gradientDrawable);

                    Category cat = new Category("FastFood");
                    categoryList.add(cat);
                    bt3 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button3.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("FastFood")) {
                            categoryList.remove(i);
                        }
                    }
                    bt3 = false;
                }

            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt4) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button4.setBackground(gradientDrawable);

                    Category cat = new Category("Coctel");
                    categoryList.add(cat);
                    bt4 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button4.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("Coctel")) {
                            categoryList.remove(i);
                        }
                    }
                    bt4 = false;
                }

            }
        });

        binding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt5) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button5.setBackground(gradientDrawable);

                    Category cat = new Category("India");
                    categoryList.add(cat);
                    bt5 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button5.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("India")) {
                            categoryList.remove(i);
                        }
                    }
                    bt5 = false;
                }

            }
        });

        binding.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt6) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button6.setBackground(gradientDrawable);

                    Category cat = new Category("Italiana");
                    categoryList.add(cat);
                    bt6 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button6.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("Italiana")) {
                            categoryList.remove(i);
                        }
                    }
                    bt6 = false;
                }

            }
        });

        binding.button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt7) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button7.setBackground(gradientDrawable);

                    Category cat = new Category("Pasta");
                    categoryList.add(cat);
                    bt7 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button7.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("Pasta")) {
                            categoryList.remove(i);
                        }
                    }
                    bt7 = false;
                }

            }
        });

        binding.button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt8) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button8.setBackground(gradientDrawable);

                    Category cat = new Category("Postres");
                    categoryList.add(cat);
                    bt8 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button8.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("Postres")) {
                            categoryList.remove(i);
                        }
                    }
                    bt8 = false;
                }

            }
        });

        binding.button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bt9) {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_button_pressed);
                    binding.button9.setBackground(gradientDrawable);

                    Category cat = new Category("Desayuno");
                    categoryList.add(cat);
                    bt9 = true;
                } else {
                    Drawable gradientDrawable = getResources().getDrawable(R.drawable.bg_fulloutlined);
                    binding.button9.setBackground(gradientDrawable);

                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getName().equals("Desayuno")) {
                            categoryList.remove(i);
                        }
                    }
                    bt9 = false;
                }

            }
        });

        binding.btsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryList.size() != 0) {

                    binding.btsend.setEnabled(false);

                    executor.execute(new Runnable() {
                        @Override
                        public void run() {

                            DataResult result = model.updateCategory(token, recipe_id, categoryList);

                            if (result.getResult().equals("1")) {
                                controller.displayActivity(getApplicationContext(), HomeActivity.class);
                            } else if (result.getResult().equals("0001")) {
                                //model.removeRecipe(token,(int)recipe_id, getApplicationContext());
                                controller.displayActivity(getApplicationContext(), NoConnectionActivity.class);

                            } else {
                                model.removeRecipe(token,(int)recipe_id, getApplicationContext());
                            }

                        }
                    });

                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            controller.displayErrorMessage(AddCategoryActivity.this, "Debes seleccionar al menos una categoria");

                        }
                    });

                }
            }
        });


        binding.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.displayConfirmEnd(AddCategoryActivity.this, "Si decides terminar el proceso ahora, la receta no será creada");
            }
        });

    }
}
