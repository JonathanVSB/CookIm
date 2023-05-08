package com.example.cookim.controller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.widget.Toast;

import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivitySigninBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Intent data;
    Model model;

    private File file;

    private final String URL = "http://91.107.198.64:7070/Cookim/";
    private final String URL2 = "http://192.168.127.102:7070/Cookim/";
    private final String URL3 = "http://192.168.127.94:7070/Cookim/";
    ExecutorService executor;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model  = new Model();
        executor = Executors.newSingleThreadExecutor();
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initElements();
    }

    private void initElements() {
        binding.btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinActions(v);
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinActions(v);
            }
        });
    }

    /**
     * Control the actions of the view
     * if the user clicks sign in without the correct params, displays the error message
     * else sends the new data
     * if the user clicks the empty picture, the app allows him to chose one from his gallery
     *
     * @param v
     */
    private void signinActions(View v) {
        if (v.getId() == binding.btSignin.getId()) {
            if (areFieldsEmpty()) {
                binding.errormsg.setText("*Debes rellenar todos los campos*");
                binding.errormsg.setVisibility(View.VISIBLE);
            } else if (!isEmailValid()) {
                binding.errormsg.setText("*El correo debe ser válido*");
                binding.errormsg.setVisibility(View.VISIBLE);
            } else if (!isPhoneNumberValid()) {
                binding.errormsg.setText("*El número de teléfono debe ser válido*");
                binding.errormsg.setVisibility(View.VISIBLE);
            } else if (!toWeakPass()) {
                binding.errormsg.setText("*La contraseña es demasiado debil*");
                binding.errormsg.setVisibility(View.VISIBLE);
            } else {
                binding.errormsg.setVisibility(View.INVISIBLE);
                UserModel user = new UserModel(binding.etUsername.getText().toString(),
                        binding.etPassword.getText().toString(),
                        binding.etFullname.getText().toString(),
                        binding.etEmail.getText().toString(),
                        binding.etTel.getText().toString(), 2);

                if (user != null) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {

                            //System.out.println("ENTRA");
                            sendNewUser(user);
//
                        }
                    });


                } else {

                }


            }

        } else if (v.getId() == binding.profileImage.getId()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);

        }
    }


    /**
     * Gets the data of the view an sends new user to server
     * If the user chose
     */
    private void sendNewUser(UserModel user) {

        if (file != null) {

            //Send the new user to data base
            DataResult res = model.signIn(user.getUsername(),
                    user.getPassword(),
                    user.getFull_name(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getId_rol(), file);
            if (res != null) {
                if (res.getResult().equals("1")) {
                    //
                    String token = res.getData().toString();
                    saveToken(token);
                    showHomePage();
                    //TODO
                    //Display next page of signin
                } else {
                    binding.errormsg.setText(res.getData().toString());
                    binding.errormsg.setVisibility(View.VISIBLE);
                }
            }
            //T9odo send file

        } else {

            DataResult res = model.signIn(user.getUsername(),
                    user.getPassword(),
                    user.getFull_name(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getId_rol(), null);
            if (res != null) {
                if (res.getResult().equals("1")) {
                    //
                    String token = res.getData().toString();
                    saveToken(token);
                    showHomePage();
                    //TODO
                    //Display next page of signin
                } else {
                    binding.errormsg.setText(res.getData().toString());
                    binding.errormsg.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /**
     * Check the fields of the view to check if any of them is empty
     *
     * @return
     */
    private boolean areFieldsEmpty() {
        return binding.etUsername.getText().toString().equals("") ||
                binding.etPassword.getText().toString().equals("") ||
                binding.etFullname.getText().toString().equals("") ||
                binding.etEmail.getText().toString().equals("") ||
                binding.etTel.getText().toString().equals("");

    }

    /**
     * prevents the user to introduce very short password
     *
     * @return
     */
    private boolean toWeakPass() {
        String pass = binding.etPassword.getText().toString();
        if (pass.length() < 6) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the email has the correct structure
     * Email shall contain this characters: "@", "."
     * Also email can't be empty
     *
     * @return
     */
    private boolean isEmailValid() {
        String email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        } else if (!email.contains(".")) {
            return false;
        } else if (!email.contains("@")) {
            return false;
        }
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }
        String beforeAt = parts[0];
        String afterAt = parts[1];
        if (beforeAt.isEmpty()) {
            return false;
        } else if (afterAt.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * checks the number to prevents empty fields and wrong format
     *
     * @return
     */
    private boolean isPhoneNumberValid() {
        String phoneNumber = binding.etTel.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            return false;
        }
        return isNumeric(phoneNumber);
    }

    /**
     * Checks if the phone number have any alphabetic character
     * if the number contains any, returns false
     *
     * @param str
     * @return
     */
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
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
        binding.profileImage.setImageBitmap(bitmap);

        return new File(picturePath);
    }

    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);


        startActivity(intent);
    }

    /**
     * saves the token received by the server in a file only accessible from the application.
     *
     * @param token
     */
    private void saveToken(String token) {
        // Gets an instance of the application context
        Context context = getApplicationContext();

        // Open the file in write mode and create the FileOutputStream object
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("token.txt", Context.MODE_PRIVATE);

            // Write the token string to the file
            outputStream.write(token.getBytes());

            // Closes the FileOutputStream object to release the resources
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
