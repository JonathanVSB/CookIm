package com.example.cookim.controller.Home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.controller.Add.AddRecipeActivity;
import com.example.cookim.controller.CommentActivity;
import com.example.cookim.controller.Controller;
import com.example.cookim.controller.EditPasswordActivity;
import com.example.cookim.controller.EditProfileActivity;
import com.example.cookim.controller.FavoritesActivity;
import com.example.cookim.controller.LoginActivity;
import com.example.cookim.controller.MyProfileActivity;
import com.example.cookim.controller.NoConnectionActivity;
import com.example.cookim.controller.RecipeStepsActivity;
import com.example.cookim.controller.SearchRecipeActivity;
import com.example.cookim.dao.BBDDIngredients;
import com.example.cookim.databinding.ActivityHomeBinding;
import com.example.cookim.databinding.ComponentNavHeaderBinding;
import com.example.cookim.exceptions.PersistException;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Ingredient;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

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
    Controller controller;
    String token;
    List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (shouldAskPermissions()) {
            askPermissions();
        }

        setContentView(R.layout.activity_home);


        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.ingredients = new BBDDIngredients(this, "Ingredientes", null, 1);


        handler = new Handler(Looper.getMainLooper());
        model = new Model(this);
        controller = new Controller();


        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        token = model.readFile(getApplicationContext(), "token");


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
     * Validació de la versió
     * https://developer.android.com/reference/android/os/Build.VERSION_CODES.html
     *
     * @return true
     */
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1); // > 22
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this, permissions, 1);
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

                    if (maxId != -1) {
                        List<Ingredient> ingredientList = model.getNewIngredients(token, maxId, getApplicationContext());

                        if (ingredientList != null) {

                            if (ingredientList.size() == 0) {


                            } else if (ingredientList.size() != 0 && ingredientList.get(ingredientList.size() - 1).getId() != maxId) {

                                insert(ingredientList);
                            }


                        }

                    }


                } catch (Exception e) {

                    System.out.println("Ha petado ago de la base interna");
                    System.out.println(e.toString());
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
                this.instrSQL = "INSERT INTO Ingrediente (PK_Id, Nombre) VALUES ('" + id + "','" + nombre + "')";
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
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.profile:
                    closeNavMenu();
                    displayMyProfile();
                    break;
                case R.id.favorites:
                    closeNavMenu();
                    controller.displayActivityWithId(this, FavoritesActivity.class, user.getId());

                    break;
                case R.id.edit_profile:
                    closeNavMenu();
                    controller.displayEditProfile(this, EditProfileActivity.class, user);
                    break;

                case R.id.change_password:
                    closeNavMenu();
                    controller.displayActivity(this, EditPasswordActivity.class);
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
                DataResult result = model.logout(token, getApplicationContext());
                if (result != null) {
                    controller.displayLogInPage(getApplicationContext(), LoginActivity.class);

                    //displayLogInPage();
                } else if (result.getResult().equals("0000")) {
                    controller.displayActivity(getApplicationContext(), NoConnectionActivity.class);
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
                    try {
                        user = model.myProfile(token);

                        if (user != null) {

                            recipes = model.loadRecipes(token, getApplicationContext());
                            List<RecipeAdapter> adapters = new ArrayList<>();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    displayRecipes(recipes, user.getId_rol());
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

                                            //uses
                                            loadHeader(user);


                                        } else {
//                                    // Si no hay un usuario, establece la imagen predeterminada en el ImageView "profileImage"
                                            binding.profileImage.setImageResource(R.drawable.guest_profile);
                                        }

                                    }

                                });


                            }
                        } else {
                            controller.displayLogInPage(getApplicationContext(), LoginActivity.class);


                        }

                    } catch (PersistException e) {
                        controller.displayErrorView(getApplicationContext(), e.getCode());
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
    private void displayRecipes(List<Recipe> recipes, long rol) {
        RecipeAdapter adapter = new RecipeAdapter(recipes, token, rol, this);
        adapter.setHomeListener(this);
        binding.recommendationsRv.setAdapter(adapter);
        binding.recommendationsRv.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
    }

    /**
     * Display the login Page
     */
    private void displayLogInPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(int id, int action) {
        Intent intent = null;
        switch (action) {
            case 1:
                controller.displayRecipeDetails(this, RecipeStepsActivity.class, id, user.getId());
                break;

            case 2:
                controller.displayMyProfile(this, MyProfileActivity.class, user.getId(), id);
                break;

            case 3:
                controller.displayCommentPage(this, CommentActivity.class, id);
                break;

            case 4:
                removeRecipe(token, id);
                loadHomePage(token);
                break;

        }


    }

    /**
     * Display my profile page
     */
    private void displayMyProfile() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        int myId = (int) user.getId();
        intent.putExtra("userID", user.getId());
        intent.putExtra("MyUserID", myId);
        startActivity(intent);
    }

    private void bottomNavigationViewClick() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    loadHomePage(token);
                    return true;
                case R.id.addrecipe:
                    controller.displayActivity(this, AddRecipeActivity.class);
                    //displayAddRecipe();
                    // TODO: Implement favorites screen
                    return true;
                case R.id.searchrecipe:
                    // TODO: Implement settings screen
                    controller.displayActivityWithId(this, SearchRecipeActivity.class, user.getId());
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

    private void removeRecipe(String token, int id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataResult result = model.removeRecipe(token, id, getApplicationContext());

                if (result != null) {
                    if (result.getResult().equals("1")) {

                    } else {

//                        controller.displayErrorMessage(getApplicationContext(), "La receta no ha podido ser borrada");
//
                    }
                } else if (result.getResult().equals("0000")) {
                    controller.displayActivity(getApplicationContext(), NoConnectionActivity.class);
                } else {
//                    controller.displayErrorMessage(getApplicationContext(), "La conexión ha fallado");
                }
            }
        });
    }


}