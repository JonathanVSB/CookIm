package com.example.cookim.controller.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.cookim.R;
import com.example.cookim.controller.LoginActivity;
import com.example.cookim.controller.RecipeStepsActivity;
import com.example.cookim.databinding.ActivityHomeBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeActivity extends Activity implements HomeListener {

    private ActivityHomeBinding binding;

    private final String URL = "http://91.107.198.64:7070/Cookim/";
    private final String URL2 = "http://192.168.127.102:7070/Cookim/";
    private final String URL3 = "http://192.168.127.94:7070/Cookim/";

    Executor executor = Executors.newSingleThreadExecutor();
    Handler handler;

    UserModel user;
    Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        handler = new Handler(Looper.getMainLooper());
        model = new Model();


        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String token = readToken();

        loadHomePage(token);

        binding.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    private void loadHomePage(String a) {
        String data1 = "my-profile";
        String data2 = "home-page";
        String url = URL3;
        String token = a;

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
                        binding.tvUsername.setText(user.getUsername());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute
                                if (user != null) { // si hay un usuario
                                    executor.execute(() -> { // ejecuta esto en un hilo de fondo para evitar bloquear el subproceso principal de la interfaz de usuario
                                        try {
                                            // Crea un objeto File que apunta al archivo especificado por la ruta de usuario
                                            File proFile = new File("user.jpg");

                                            // Verifica si el archivo ya existe en el directorio de archivos. Si no existe, continúa descargando la imagen.
                                            if (!proFile.exists()) {
                                                // no se descarga la imagen ya que el archivo ya existe
                                            }

                                            // Actualiza la imagen en el ImageView "profileImage" en el subproceso de interfaz de usuario con la imagen cargada desde el archivo
                                            runOnUiThread(() -> binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(proFile.getAbsolutePath())));

                                        } catch (Exception e) { // Si ocurre algún error durante la carga o la escritura del archivo, se captura y se maneja en este bloque de excepción
                                            e.printStackTrace(); // Muestra una traza de la pila de excepciones en la consola
                                            System.out.println("PETA EN ESTA LINEA: " + e.toString()); // Muestra un mensaje de error en la consola

                                            // Establece la imagen predeterminada en el ImageView "profileImage" si ocurre algún error durante la carga de la imagen del archivo
                                            binding.profileImage.setImageResource(R.drawable.guest_profile);
                                        }
                                    });

                                    // Crea una cadena que contiene la ubicación de la imagen en el servidor
                                    String profileUrl = "http://91.107.198.64" + user.getPath_img();

                                    // Descarga y muestra la imagen usando Glide
                                    Glide.with(HomeActivity.this)
                                            .load(profileUrl)
                                            .into(binding.profileImage);
                                } else {
                                    // Si no hay un usuario, establece la imagen predeterminada en el ImageView "profileImage"
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
        startActivity(intent);
    }
}