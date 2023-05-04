package com.example.cookim.controller.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.controller.AddRecipeActivity;
import com.example.cookim.controller.LoginActivity;
import com.example.cookim.controller.MyProfileActivity;
import com.example.cookim.controller.RecipeStepsActivity;
import com.example.cookim.dao.BBDDIngredients;
import com.example.cookim.databinding.ActivityHomeBinding;
import com.example.cookim.databinding.ComponentNavHeaderBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Ingredient;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeActivity extends Activity implements HomeListener {

    private ActivityHomeBinding binding;
    Executor executor = Executors.newSingleThreadExecutor();
    Handler handler;

    BBDDIngredients ingredients;
    SQLiteDatabase dbIngredients;
    String instrSQL;
    UserModel user;
    Model model;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.ingredients = new BBDDIngredients(this.getApplicationContext(), "Ingredientes", null, 1);


        handler = new Handler(Looper.getMainLooper());
        model = new Model();


        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        token = readToken();

        if (token.isEmpty()) {
            displayLogInPage();

        } else {

            loadIngredients(this.ingredients);
            loadHomePage(token);
            bottomNavigationViewClick();

            initlistener();


        }


    }

    /**
     * Load all ingredients Saved in database
     */

    private void loadIngredients(BBDDIngredients ingredients) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    int maxId = model.getMaxIdIngredient(ingredients);

                    List<Ingredient> ingredientList = model.getNewIngredients(token, maxId);

                    if (ingredientList != null) {

                        if (ingredientList.size() == 0) {


                        }else if (ingredientList.size() != 0 && ingredientList.get(ingredientList.size() - 1).getId() != maxId) {

                            insert(ingredientList);
                        }


                    }

                } catch (Exception e) {

                    System.out.println("Ha petado ago de la base interna");
                }


            }
        });
    }

    /**
     * inserts ingredients in local database
     */
    private void insert(List<Ingredient> list) {
        this.dbIngredients = this.ingredients.getWritableDatabase();

        if (this.dbIngredients != null) {
            System.out.println("Database ingredients ready");

            for (Ingredient ing : list) {
                String nombre = ing.getName();
                String id = String.valueOf(ing.getId());
                this.instrSQL = "INSERT INTO Ingrediente (PK_Id, Nombre) VALUES ('" + id + "," + nombre + "')";
                this.dbIngredients.execSQL(this.instrSQL);
            }

            this.dbIngredients.close();
            System.out.println("ingredientes guardados");

        } else {

            System.out.println("ERROR: saving ingredients");

        }

    }

    /**
     * Fulfill the data of the navigation view with the data of the user
     *
     * @param userModel
     */
    private void loadHeader(UserModel userModel) {
        View headerData = binding.profileNavMenu.getHeaderView(0);
        ComponentNavHeaderBinding header = ComponentNavHeaderBinding.bind(headerData);

        header.tvusername.setText(userModel.getUsername());
        header.tvname.setText(userModel.getFull_name());
        String img = model.downloadImg(userModel.getPath_img());
        Glide.with(HomeActivity.this)
                .load(img)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Manejar el fallo de carga de la imagen aquí
                        header.optionProfile.setImageResource(R.drawable.guest_profile);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // La imagen se ha cargado correctamente
                        return false;
                    }
                })
                .into(header.optionProfile);
    }


    private void initlistener() {
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigatioViewClick();

            }
        });

    }

    private void navigatioViewClick() {
        binding.homeActivityContent.openDrawer(GravityCompat.START);
        binding.profileNavMenu.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    closeNavMenu();
                    displayMyProfile();
                    break;
                case R.id.favorites:
                    closeNavMenu();

                    break;
                case R.id.settings:
                    closeNavMenu();
                    break;
                case R.id.logout:
                    closeNavMenu();
                    logout();
                    break;
            }
            return true;
        });
    }


    private void logout() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataResult result = model.logout(token);
                if (result != null) {
                    displayLogInPage();
                }
            }
        });
    }

    private void closeNavMenu() {
        binding.homeActivityContent.closeDrawer(GravityCompat.START);
    }

    private void loadHomePage(String a) {

        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    user = model.myProfile(token); /*readUserResponse((url + data1), token);*/
                    List<Recipe> recipes = model.loadRecipes();
                    List<RecipeAdapter> adapters = new ArrayList<>();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            displayRecipes(recipes);
                        }
                    });

                    if (user != null) {
                        //binding.tvUsername.setText(user.getUsername());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute
                                if (user != null) { // si hay un usuario

                                    String img = model.downloadImg(user.getPath_img());
                                    Glide.with(HomeActivity.this)
                                            .load(img)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    // Manejar el fallo de carga de la imagen aquí
                                                    binding.profileImage.setImageResource(R.drawable.guest_profile);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    // La imagen se ha cargado correctamente
                                                    return false;
                                                }
                                            })
                                            .into(binding.profileImage);

                                    loadHeader(user);


                                } else {
//                                    // Si no hay un usuario, establece la imagen predeterminada en el ImageView "profileImage"
                                    binding.profileImage.setImageResource(R.drawable.guest_profile);
                                }

                            }

                        });


                    }
                }
            });
        } catch (Exception e) {
            System.out.println("ERROR" + e.toString());
        }

    }

    /**
     * Gives the list of recipes to the RecipeAdapter
     *
     * @param recipes
     */
    private void displayRecipes(List<Recipe> recipes) {
        RecipeAdapter adapter = new RecipeAdapter(recipes);
        adapter.setHomeListener(this);
        binding.recommendationsRv.setAdapter(adapter);
        binding.recommendationsRv.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
    }

    /**
     * Read an internal file read the token stored there
     *
     * @return the token or null
     */
    private String readToken() {
        // Gets an instance of the application context
        Context context = getApplicationContext();

        // Open the file in write mode and create the FileOutputStream object
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput("token.txt");

            //Reads the token data from file
            StringBuilder stringBuilder = new StringBuilder();
            int c;
            while ((c = inputStream.read()) != -1) {
                stringBuilder.append((char) c);
            }
            String token = stringBuilder.toString();

            //Close the FileInputStream Object
            inputStream.close();

            // returns token
            return token;
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error al leer la respuesta: " + e.toString());
        }

        // if file is empty, returns null
        return null;
    }

    /**
     * Display the login Page
     */
    private void displayLogInPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(int id) {
        Intent intent = new Intent(this, RecipeStepsActivity.class);
        intent.putExtra("recipe_id", id);
        startActivity(intent);
    }

    /**
     * Display my profile page
     */
    private void displayMyProfile() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }

    private void bottomNavigationViewClick() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    loadHomePage(token);
                    return true;
                case R.id.addrecipe:
                    displayAddRecipe();
                    // TODO: Implement favorites screen
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
     * Display the add recipe page
     */
    private void displayAddRecipe() {
        Intent intent = new Intent(this, AddRecipeActivity.class);

        startActivity(intent);

    }


}