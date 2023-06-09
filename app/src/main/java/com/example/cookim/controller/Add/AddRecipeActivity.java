package com.example.cookim.controller.Add;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.cookim.R;
import com.example.cookim.controller.AddCategoryActivity;
import com.example.cookim.controller.Controller;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.dao.BBDDIngredients;
import com.example.cookim.databinding.ActivityAddRecipeBinding;
import com.example.cookim.databinding.ItemAddIngredientBinding;
import com.example.cookim.databinding.ItemNewStepContentBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Ingredient;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.recipe.Step;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddRecipeActivity extends AppCompatActivity {

    private ActivityAddRecipeBinding binding;
    private static final int REQUEST_LOAD_IMAGE_PORTRAIT = 1;
    private static final int REQUEST_LOAD_IMAGE_OTHER = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Intent data;
    private File file;
    private File portraitFile;
    private View currentView;
    private View portraitView;
    ExecutorService executor;


    Handler handler;
    List<Ingredient> ingredients;
    List<Step> steps;
    Model model;
    Controller controller;
    SQLiteDatabase dbIngredients;
    BBDDIngredients dataBase;
    String token;

    /**
     * Called when the activity is starting or being recreated after previously being destroyed.
     * Initializes the user interface and performs other setup tasks.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        handler = new Handler(Looper.getMainLooper());
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
        model = new Model(this);
        controller = new Controller();
        token = model.readFile(getApplicationContext(), "token");
        executor = Executors.newSingleThreadExecutor();
        this.dataBase = new BBDDIngredients(this.getApplicationContext(), "Ingredientes", null, 1);


        prepareElements();
    }

    /**
     * Prepares the elements of the user interface for the activity.
     * Sets up the necessary onClickListeners for various UI elements.
     * When the elements are clicked, the method 'gestionateProcess' is called.
     */
    private void prepareElements() {
        binding.portraitPic.setOnClickListener(new View.OnClickListener() {
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

        binding.addingredientPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestionateProcess(v);
            }
        });
    }

    /**
     * Handles the different processes when clicking on various UI elements.
     *
     * @param v The View that was clicked.
     */
    private void gestionateProcess(View v) {

        if (v.getId() == binding.ivcancel.getId()) {
            finish();

        } else if (v.getId() == binding.portraitPic.getId()) {
            portraitView = v;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra("portraitView", v.getId());
            startActivityForResult(intent, REQUEST_LOAD_IMAGE_PORTRAIT);


        } else if (v.getId() == binding.ivaccept.getId()) {

            //Fulfill ingredients list
            for (int i = 0; i < binding.tlIngredients.getChildCount(); i++) {
                TableRow row = (TableRow) binding.tlIngredients.getChildAt(i);
                int columnCount = row.getChildCount();

                //get all ingredients from view
                for (int j = 0; j < columnCount; j++) {
                    View view = row.getChildAt(j);

                    //creates new ingredient
                    if (view instanceof TextView) {
                        Ingredient ingredient = new Ingredient((((TextView) view).getText().toString()));

                        //adds new ingredient to list
                        ingredients.add(ingredient);

                    }
                }

            }

            //Fulfill step list
            for (int i = 0; i < binding.tlsteps.getChildCount(); i++) {
                TableRow row = (TableRow) binding.tlsteps.getChildAt(i);

                EditText editText = row.findViewById(R.id.etElavoration);
                ImageView imageView = row.findViewById(R.id.stepPic);

                editText.requestFocus();

                // Get the description from the EditText and the temporary image file from the ImageView
                String description = editText.getText().toString();
                long stepnum = i + 1;

                // Get the image from the ImageView and save it to a temporary file
                Drawable drawable = imageView.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                File imageFile = null;
                try {
                    imageFile = File.createTempFile("stepImage", ".jpg", getCacheDir());
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Create a Step object with the values from the EditText and the temporary image file
                Step step = new Step(imageFile, stepnum, description);

                steps.add(step);
            }


            if (portraitFile != null && steps.size() != 0 && ingredients.size() != 0) {
                //Recipe recipe = new Recipe(file, binding.etname.getText().toString(),
                //binding.etdescription.getText().toString(), steps, ingredients)

                if (!binding.etname.getText().toString().isEmpty() && !binding.etdescription.getText().toString().isEmpty()) {

                    binding.ivaccept.setEnabled(false);
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {

                            Recipe recipe = createRecipe(portraitFile, binding.etname.getText().toString(), binding.etdescription.getText().toString()
                                    , steps, ingredients);

                            if (recipe != null) {

                                Gson gson = new Gson();

                                String recipeJson = gson.toJson(recipe);
                                long id =1;

                                controller.displayCategoryActivity(getApplicationContext(), AddCategoryActivity.class, id);

                            } else {
                                controller.displayErrorMessage(AddRecipeActivity.this, "La receta contiene datos no válidos");

                            }


                        }
                    });

                } else {
                    controller.displayErrorMessage(AddRecipeActivity.this, "La receta debe tener un nombre y una descripción");


                }


            } else {

                controller.displayErrorMessage(AddRecipeActivity.this, "La receta debe tener foto de portada, ingredientes, y pasos");

            }

        } else if (v.getId() == binding.addingredientPic.getId()) {
            this.dbIngredients = this.dataBase.getWritableDatabase();
            AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipeActivity.this);
            ItemAddIngredientBinding ingredientView = ItemAddIngredientBinding.inflate(getLayoutInflater());
            builder.setView(ingredientView.getRoot());

            builder.setTitle("Ingredientes")
                    .setCancelable(true)
                    .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!ingredientView.idSearch.getQuery().toString().equals("")) {
                                TableRow row = new TableRow(AddRecipeActivity.this);
                                TableRow.LayoutParams params = new TableRow.LayoutParams();
                                params.setMargins(0, 10, 0, 20);
                                row.setLayoutParams(params);

                                TextView ingredientTextView = new TextView(AddRecipeActivity.this);
                                ingredientTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                ingredientTextView.setText(ingredientView.idSearch.getQuery().toString()); // Reemplaza "ingredientName" con el nombre del ingrediente que deseas mostrar
                                ingredientTextView.setTextSize(20);
                                ingredientTextView.setTypeface(null, Typeface.BOLD);
//                            ingredientTextView.setGravity(Gravity.LEFT);
                                row.addView(ingredientTextView);

                                ImageView btremove = new ImageView(AddRecipeActivity.this);
                                btremove.setId(View.generateViewId());
                                btremove.setImageResource(R.drawable.ic_remove);
                                TableRow.LayoutParams btremoveParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT); // Establece los parámetros de layout de la imagen en MATCH_PARENT
                                btremoveParams.gravity = Gravity.END | Gravity.TOP;
                                btremove.setLayoutParams(btremoveParams);
                                row.addView(btremove);

                                binding.tlIngredients.addView(row);

                                btremove.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Obtener la referencia a la fila que contiene el botón
                                        TableRow parentRow = (TableRow) view.getParent();
                                        // Removes row from table
                                        binding.tlIngredients.removeView(parentRow);
                                    }
                                });

                            }


                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //close the dialog
                            dialog.cancel();
                        }
                    });

            // Listen for text changes in the search field.
            ingredientView.idSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false; // Don't care about this.
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (s.length() >= 3) {
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddRecipeActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
                        ingredientView.lvResults.setAdapter(adapter);
                        String query = "SELECT * FROM Ingrediente WHERE Nombre LIKE '%" + s + "%'";
                        Cursor cursor = dbIngredients.rawQuery(query, null);
                        while (cursor.moveToNext()) {
                            String item = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                            adapter.add(item);
                        }
                    }
                    return false;
                }
            });

            // Listen for clicks on the ListView items.
            ingredientView.lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selected = (String) parent.getItemAtPosition(position);
                    ingredientView.idSearch.setQuery(selected, false);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }else if (v.getId() == binding.addbutton.getId()) {
            int lastIndex = binding.tlsteps.getChildCount() - 1;

            if (lastIndex >= 0) {
                TableRow lastRow = (TableRow) binding.tlsteps.getChildAt(lastIndex);
                ItemNewStepContentBinding lastStepContentBinding = ItemNewStepContentBinding.bind(lastRow.getChildAt(0));

                if (lastStepContentBinding.etElavoration.getText().toString().isEmpty()) {
                    controller.displayErrorMessage(AddRecipeActivity.this, "El paso anterior aún está vacío");
                    return;
                }
            }

            ItemNewStepContentBinding stepContentBinding = ItemNewStepContentBinding.inflate(getLayoutInflater());
            TableRow row = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.setMargins(0, 0, 0, 20);
            row.setLayoutParams(params);

            stepContentBinding.stepnum.setText(String.valueOf(binding.tlsteps.getChildCount() + 1));
            stepContentBinding.etElavoration.setText("");
            stepContentBinding.stepPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentView = view;
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_LOAD_IMAGE_OTHER);
                }
            });
            row.addView(stepContentBinding.getRoot());

            ImageView btremove = new ImageView(this);
            btremove.setId(View.generateViewId());
            btremove.setImageResource(R.drawable.ic_remove);
            TableRow.LayoutParams btremoveParams = new TableRow.LayoutParams(90, 90);
            btremoveParams.gravity = Gravity.END | Gravity.TOP;
            btremove.setLayoutParams(btremoveParams);
            row.addView(btremove);

            binding.tlsteps.addView(row);

            btremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TableRow parentRow = (TableRow) view.getParent();
                    binding.tlsteps.removeView(parentRow);
                    refactorSteps(binding.tlsteps);
                }
            });
        }


    }

    /**
     * Creates a Recipe object with the given parameters.
     *
     * @param file        The file associated with the recipe.
     * @param name        The name of the recipe.
     * @param description The description of the recipe.
     * @param steps       The list of steps for the recipe.
     * @param ingredients The list of ingredients for the recipe.
     * @return The created Recipe object, or null if any of the parameters are null or empty.
     */
    private Recipe createRecipe(File file, String name, String description, List<Step> steps, List<Ingredient> ingredients) {
        Recipe recipe = null;

        // Check if all parameters are not null and not empty
        if (file != null &&
                name != null && !name.trim().isEmpty() &&
                description != null && !description.trim().isEmpty() &&
                steps != null && !steps.isEmpty() &&
                ingredients != null && !ingredients.isEmpty()) {

            // Create the Recipe object
            recipe = new Recipe();
            recipe.setFile(file);
            recipe.name = name;
            recipe.description = description;
            recipe.steps = steps;
            recipe.ingredients = ingredients;
        }

        return recipe;
    }


    /**
     * Refactors the step numbers in the table layout after deleting a step.
     *
     * @param tlsteps The TableLayout containing the steps.
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
     * Handles the results of activities launched for selecting images from the gallery.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity through its setResult().
     * @param data        An Intent that carries the result data.
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.data = data;

        if (resultCode == RESULT_OK && null != data) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                if (requestCode == REQUEST_LOAD_IMAGE_PORTRAIT) {
                    portraitFile = loadImage(data, portraitView); // Realiza acciones específicas para portraitView
                } else if (requestCode == REQUEST_LOAD_IMAGE_OTHER) {
                    file = loadImage(data, currentView); // Realiza acciones específicas para otherView
                }
            }
        }
    }

    /**
     * Handles the user's response to the permission request.
     *
     * @param requestCode  The code originally provided to requestPermissions().
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage(data, currentView); // Agrega 'currentView' como argumento
            } else {
                Toast.makeText(this, "Permiso denegado para leer el almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Loads the selected image from the gallery.
     *
     * @param data The intent containing the selected image data.
     * @param view The view to set the image on.
     * @return The File object representing the selected image.
     */
    private File loadImage(Intent data, View view) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

        // Set the image based on the 'View' passed to it
        if (view == binding.portraitPic) {
            binding.portraitPic.setImageBitmap(bitmap);
        } else {
            ImageView stepPic = (ImageView) view;
            stepPic.setImageBitmap(bitmap);
        }

        return new File(picturePath);
    }

    /**
     * check the
     *
     * @param recipe
     * @return
     */
    private boolean checkFields(Recipe recipe) {
        boolean correct = false;
        return correct;
    }

}
