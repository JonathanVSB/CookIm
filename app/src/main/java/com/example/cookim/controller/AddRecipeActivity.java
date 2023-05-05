package com.example.cookim.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cookim.R;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityAddRecipeBinding;
import com.example.cookim.databinding.ItemNewStepContentBinding;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Ingredient;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.recipe.Step;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    private ActivityAddRecipeBinding binding;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Intent data;
    private File file;

    Handler handler;
    List<Ingredient> ingredients;
    List<Step> steps;
    Model model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handler = new Handler(Looper.getMainLooper());
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
        model = new Model();

        prepareElements();
    }

    /**
     * build the structure of all the elements
     */
    private void prepareElements() {
        binding.stepPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gestionateProcess(view);
            }
        });
        binding.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateProcess(view);
            }
        });
        binding.ivaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateProcess(view);
            }
        });
        binding.addingredientPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateProcess(view);
            }
        });
        binding.addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateProcess(view);
            }
        });
    }

    /**
     * receive cancel and continue button.
     * if cancel button is pressed. resturns to home page
     * if accpt button is pressed, send petition to create new Recipe
     */
    private void gestionateProcess(View v) {

        if (v.getId() == binding.ivcancel.getId()) {
            showHomePage();

        } else if (v.getId() == binding.stepPic.getId()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);

        } else if (v.getId() == binding.ivaccept.getId()) {

            if (file != null /*&& steps != null && ingredients != null*/) {
//                Recipe recipe = new Recipe(file, binding.etname.getText().toString(),
//                        binding.etdescription.getText().toString(), steps, ingredients);

                Recipe recipe = new Recipe(file, binding.etname.getText().toString(),binding.etdescription.getText().toString());
                model.createRecipe(recipe, readToken());

            } else {
                //TODO
                //Display error message

            }

        } else if (v.getId() == binding.addingredientPic.getId()) {
            //TODO
            //Display add ingredient

        } else if (v.getId() == binding.addbutton.getId()) {
            //TODO
            //add New step empty item

            ItemNewStepContentBinding stepContentBinding = ItemNewStepContentBinding.inflate(getLayoutInflater());

            TableRow row = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.setMargins(0, 0, 0, 20); // Replace -50 with the number of pixels you want to move to the left
            row.setLayoutParams(params);

            stepContentBinding.stepnum.setText(String.valueOf(binding.tlsteps.getChildCount() + 1));
            stepContentBinding.stepPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            });
            row.addView(stepContentBinding.getRoot());

            // Agregar el botón de eliminación a la fila
            ImageView btremove = new ImageView(this);
            btremove.setId(View.generateViewId());
            btremove.setImageResource(R.drawable.ic_remove);
            TableRow.LayoutParams btremoveParams = new TableRow.LayoutParams(90, 90); // Nuevo tamaño
            btremoveParams.gravity = Gravity.END | Gravity.TOP;
            btremove.setLayoutParams(btremoveParams);
            row.addView(btremove);


            // Agregar la fila a la tabla
            binding.tlsteps.addView(row);

            // Asignar el listener al botón de eliminación
            btremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Obtener la referencia a la fila que contiene el botón
                    TableRow parentRow = (TableRow) view.getParent();
                    // Eliminar la fila de la tabla
                    binding.tlsteps.removeView(parentRow);
                    refactorSteps(binding.tlsteps);


                }

            });


        }


    }

    /**
     * Refactors the step number if one row is deleted from tablelayout
     *
     * @param tlsteps
     * @param
     */
    private void refactorSteps(TableLayout tlsteps) {
        // Refactor the step number if one is deleted
        for (int i = 0; i < tlsteps.getChildCount(); i++) {
            TableRow row = (TableRow) tlsteps.getChildAt(i);
            ItemNewStepContentBinding stepContentBinding = ItemNewStepContentBinding.bind(row.getChildAt(0));

            int stepNum = i + 1;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    stepContentBinding.stepnum.setText(String.valueOf(stepNum));
                }
            });
        }
    }



    /**
     * Displays the home page of the app and senda the user object to next activity
     */
    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Search for an image in storage and sets the imageview with
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.data = data; // Agrega esta línea para guardar la referencia a 'data'

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                file = loadImage(data);
            }
        }
    }

    /**
     * @param requestCode  The request code passed in
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage(data);
            } else {
                Toast.makeText(this, "Permiso denegado para leer el almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Load the Image in the imageView
     *
     * @param data
     */
    private File loadImage(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        binding.stepPic.setImageBitmap(bitmap);

        return new File(picturePath);
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
}
