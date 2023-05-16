package com.example.cookim.controller.Add;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.cookim.R;
import com.example.cookim.controller.CommentActivity;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddRecipeActivity extends AppCompatActivity {

    private ActivityAddRecipeBinding binding;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Intent data;
    private File file;
    private View currentView;
    ExecutorService executor;


    Handler handler;
    List<Ingredient> ingredients;
    List<Step> steps;
    Model model;
    Controller controller;
    SQLiteDatabase dbIngredients;
    BBDDIngredients dataBase;
    String token;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handler = new Handler(Looper.getMainLooper());
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
        model = new Model();
        controller = new Controller();
        token = model.readToken(getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
        this.dataBase = new BBDDIngredients(this.getApplicationContext(), "Ingredientes", null, 1);


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

        binding.addingredientPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestionateProcess(v);
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
            finish();

        } else if (v.getId() == binding.stepPic.getId()) {
            currentView = v;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);

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


            if (file != null && steps.size() != 0 && ingredients.size() != 0) {
                //Recipe recipe = new Recipe(file, binding.etname.getText().toString(),
                //binding.etdescription.getText().toString(), steps, ingredients)
                executor.execute(new Runnable() {
                    @Override
                    public void run() {

                        Recipe recipe = createRecipe(file, binding.etname.getText().toString(), binding.etdescription.getText().toString()
                                , steps, ingredients);

                        if (recipe != null) {

                            DataResult res = model.createRecipe(recipe, token, file);

                            if (res.getResult().equals("1")) {
                                finish();
                            } else {

                                controller.displayErrorMessage(getApplicationContext(),"Algo ha salido mal. La receta no ha sido creada");

                            }
                        } else {
                            controller.displayErrorMessage(getApplicationContext(),"La receta contiene datos no válidos");

                        }


                    }
                });


            } else {

                controller.displayErrorMessage(getApplicationContext(),"La receta debe tener foto de portada, ingredientes, y pasos");

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
        } else if (v.getId() == binding.addbutton.getId()) {
            //TODO
            //add New step empty item

            ItemNewStepContentBinding stepContentBinding = ItemNewStepContentBinding.inflate(getLayoutInflater());

            TableRow row = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.setMargins(0, 0, 0, 20); // Replace -50 with the number of pixels you want to move to the left
            row.setLayoutParams(params);

            stepContentBinding.stepnum.setText(String.valueOf(binding.tlsteps.getChildCount() + 1));
            stepContentBinding.etElavoration.setText("");
            stepContentBinding.stepPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentView = view; // Agrega esta línea para guardar la referencia al 'View' actual
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


            // Add row to table
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
     * Create a new Recipe with data of the view or return null
     *
     * @param file
     * @param toString
     * @param toString1
     * @param steps
     * @param ingredients
     * @return
     */
    private Recipe createRecipe(File file, String toString, String toString1, List<Step> steps, List<Ingredient> ingredients) {
        Recipe recipe = null;

        // Check if all parameters are not null and not empty
        if (file != null &&
                toString != null && !toString.trim().isEmpty() &&
                toString1 != null && !toString1.trim().isEmpty() &&
                steps != null && !steps.isEmpty() &&
                ingredients != null && !ingredients.isEmpty()) {

            // Create the Recipe object
            recipe = new Recipe(file, toString, toString1, steps, ingredients);
        }

        return recipe;
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
        this.data = data;

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                file = loadImage(data, currentView); // Agrega 'currentView' como argumento
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
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage(data, currentView); // Agrega 'currentView' como argumento
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
    private File loadImage(Intent data, View view) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

        // Establecer la imagen en función del 'View' que se le pase
        if (view == binding.stepPic) {
            binding.stepPic.setImageBitmap(bitmap);
        } else {
            ImageView stepPic = (ImageView) view;
            stepPic.setImageBitmap(bitmap);
        }

        return new File(picturePath);
    }

}
