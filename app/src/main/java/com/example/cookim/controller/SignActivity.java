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
import com.example.cookim.exceptions.PersistException;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Intent data;
    Model model;
    Controller controller;

    private File file;

    ExecutorService executor;
    Handler handler;

    /**
     * Method called when the activity is created.
     * It is responsible for initializing the elements of the activity, such as the model, the controller and the executor,
     * and configure the view by inflating the corresponding layout.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model  = new Model(this);
        controller = new Controller();
        executor = Executors.newSingleThreadExecutor();
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());

        initElements();
    }

    /**
     * Initializes the elements of the activity.
     * Configures the listeners for login buttons and profile image.
     */
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
     * Performs corresponding actions when login buttons or profile picture is clicked.
     * If the login button is clicked, it checks the fields and displays error messages if necessary.
     * If the profile image is clicked, it launches an activity to select an image from the gallery.
     *
     * @param v Clicked view (login button or profile picture)
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
     * Send a new user to the server and perform the corresponding actions based on the result.
     * If a profile image was selected, the image is also sent along with the user data.
     *
     * @param user UserModel object representing the new user
     */
    private void sendNewUser(UserModel user) {

        if (file != null) {

            //Send the new user to data base
            DataResult res = model.signIn(user.getUsername(),
                    user.getPassword(),
                    user.getFull_name(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getId_rol(), file, getApplicationContext());
            if (res != null) {
                if (res.getResult().equals("1")) {
                    //
                    String token = res.getData().toString();
                    saveToken(token);
                    showHomePage();
                    //TODO
                    //Display next page of signin
                }else if (res.getResult().equals("0000")) {
                    controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
                } else {
                    binding.errormsg.setText(res.getData().toString());
                    binding.errormsg.setVisibility(View.VISIBLE);
                }
            }
            //T9odo send file

        } else {
            String password = getSHA256(user.getPassword());
            DataResult res = model.signIn(user.getUsername(),
                    password,
                    user.getFull_name(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getId_rol(), null, getApplicationContext());
            if (res != null) {
                if (res.getResult().equals("1")) {
                    //
                    String token = res.getData().toString();
                    saveToken(token);
                    showHomePage();
                    //TODO
                    //Display next page of signin
                }else if (res.getResult().equals("0000")) {
                    controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
                } else {
                    binding.errormsg.setText(res.getData().toString());
                    binding.errormsg.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /**
     * Checks if the input fields are empty.
     *
     * @return true if any of the input fields is empty, false otherwise
     */
    private boolean areFieldsEmpty() {
        return binding.etUsername.getText().toString().equals("") ||
                binding.etPassword.getText().toString().equals("") ||
                binding.etFullname.getText().toString().equals("") ||
                binding.etEmail.getText().toString().equals("") ||
                binding.etTel.getText().toString().equals("");

    }

    /**
     * Checks if the password is too weak.
     *
     * @return true if the password is weak (length less than 6), false otherwise
     */
    private boolean toWeakPass() {
        String pass = binding.etPassword.getText().toString();
        if (pass.length() < 6) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the email address is valid.
     *
     * @return true if the email address is valid, false otherwise
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
     * Checks if the phone number is valid.
     *
     * @return true if the phone number is valid, false otherwise
     */
    private boolean isPhoneNumberValid() {
        String phoneNumber = binding.etTel.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            return false;
        }
        return isNumeric(phoneNumber);
    }

    /**
     * Checks if a given string is numeric.
     *
     * @param str the string to be checked
     * @return true if the string is numeric, false otherwise
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
     * Handles the result of an activity that was started for a result.
     *
     * @param requestCode the request code passed to startActivityForResult()
     * @param resultCode  the result code returned by the child activity
     * @param data        an Intent that contains the result data
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
     * Callback for the result of a permission request.
     *
     * @param requestCode  the request code passed to requestPermissions()
     * @param permissions  the requested permissions
     * @param grantResults the grant results for the corresponding permissions
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
     * Load the selected image from the intent data and display it in the profile image view.
     *
     * @param data the intent data containing the image URI
     * @return the file representing the selected image
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

    /**
     * Display the home page by starting the HomeActivity.
     */
    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    /**
     * Save the token to a file.
     *
     * @param token The token string to be saved.
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

    /**
     * Generate the SHA-256 hash of a given input string.
     *
     * @param input The input string to generate the hash from.
     * @return The SHA-256 hash of the input string.
     */
    public static String getSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error al generar hash SHA-256: " + e.getMessage());
            return null;
        }
    }


}
