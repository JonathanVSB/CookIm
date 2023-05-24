package com.example.cookim.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityEditProfileBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.UserModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileActivity extends Activity {

    UserModel user;
    private ActivityEditProfileBinding binding;
    private static final int REQUEST_LOAD_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    String token;
    Model model;
    Controller controller;
    File newfile;
    File currentFile;
    ExecutorService executor;
    Handler handler;
    Intent intent;
    private File file;

    /**
     * Sets up the activity for editing a user profile.
     * It initializes the necessary variables, retrieves the user information from the intent, and loads the view with the user data.
     * It also handles the saving of the profile image to a temporary file.
     * If the token is missing or empty, it redirects to the login page.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        model  = new Model(this);
        newfile = null;
        currentFile = null;
        controller = new Controller();
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        Intent intent = getIntent();

        token = model.readFile(getApplicationContext(), "token");


        if (token != null && !token.isEmpty()) {
            long id = intent.getLongExtra("id", 0);
            String username = intent.getStringExtra("username");
            String full_name = intent.getStringExtra("fullname");
            String phoneNumber = intent.getStringExtra("phone");
            String email = intent.getStringExtra("email");
            String pic = intent.getStringExtra("path_img");
            user = new UserModel(id, username, full_name, email, phoneNumber, pic);
            if (user != null) {
                loadView(user);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (binding.profileImage.getDrawable() != null) {
                                    Drawable drawable = binding.profileImage.getDrawable();
                                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                    try {
                                        currentFile = File.createTempFile("Image", ".jpg", getCacheDir());
                                        FileOutputStream fos = new FileOutputStream(currentFile);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                        fos.close();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                        });
                    }
                });

                newfile = currentFile;
            } else {
                controller.displayLogInPage(this, LoginActivity.class);
            }


        } else {
            controller.displayLogInPage(this, LoginActivity.class);
        }
    }

    /**
     * Loads the user data into the view fields.
     * Checks each field in the user model and sets the corresponding values in the view.
     * If a field is not empty, it sets the value in the view.
     * If the user has a profile image, it downloads and displays the image using Glide library.
     *
     * @param user The user model containing the user data.
     */
    private void loadView(UserModel user) {
        //complete all camps.

        //Check the username
        if (!user.getUsername().isEmpty()) {
            binding.tvUsername.setText(user.getUsername());
        }
        //Check the full name
        if (!user.getFull_name().isEmpty()) {
            binding.tvFullname.setText(user.getFull_name());
        }
        //Check the email
        if (!user.getEmail().isEmpty()) {
            binding.tvEmail.setText(user.getEmail());
        }
        //Check the pone
        if (!user.getPhone().isEmpty()) {
            binding.tvPhone.setText(user.getPhone());
        }
        //Check the image
        if (!user.getPath_img().isEmpty()) {
            String img = model.downloadImg(user.getPath_img());
            Glide.with(EditProfileActivity.this)
                    .load(img)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .into(binding.profileImage);

//            //Gets the current profile image fom the user

        }

        binding.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateActions(view);
            }
        });

        binding.ivaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateActions(view);
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateActions(view);
            }
        });
    }

    /**
     * Handles the actions performed by the user on the view.
     * Checks the ID of the clicked view and performs the corresponding action.
     *
     * @param view The view that triggered the action.
     */
    private void gestionateActions(View view) {

        if (view.getId() == binding.ivcancel.getId()) {
            controller.displayActivity(getApplicationContext(), HomeActivity.class);
        } else if (view.getId() == binding.ivaccept.getId()) {
            //TODO
            if (areFieldsEmpty()) {
                binding.errormsg.setText("*Debes rellenar todos los campos*");
                binding.errormsg.setVisibility(View.VISIBLE);
            } else if (!isEmailValid()) {
                binding.errormsg.setText("*El correo debe ser válido*");
                binding.errormsg.setVisibility(View.VISIBLE);
            } else if (!isPhoneNumberValid()) {
                binding.errormsg.setText("*El número de teléfono debe ser válido*");
                binding.errormsg.setVisibility(View.VISIBLE);
            } else {

                // Get the image from the ImageView and save it to a temporary file
                Drawable drawable = binding.profileImage.getDrawable();
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
                UserModel newData = new UserModel(user.getId(),
                        binding.tvUsername.getText().toString(),
                        binding.tvFullname.getText().toString(),
                        binding.tvEmail.getText().toString(),
                        binding.tvPhone.getText().toString());

                if (!user.equals(newData) && currentFile == newfile) {

                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            DataResult result = model.editData(token, newData, newfile, getApplicationContext());

                            if (result.getResult().equals("1")) {

                                controller.displayActivity(getApplicationContext(), HomeActivity.class);

                            }else if (result.getResult().equals("0000")) {
                                controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
                            } else {
                                controller.displayErrorMessage(EditProfileActivity.this, "No se ha podido editar la información");
                            }
                        }
                    });


                } else if (!user.equals(newData) && currentFile != newfile) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            DataResult result = model.editData(token, newData, newfile, getApplicationContext());

                            if (result.getResult().equals("1")) {

                                controller.displayActivity(getApplicationContext(), HomeActivity.class);

                            }else if (result.getResult().equals("0000")) {
                                controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
                            } else {
                                controller.displayErrorMessage(EditProfileActivity.this, "No se ha podido editar la información");
                            }
                        }
                    });

                } else if (user.equals(newData) && currentFile == newfile) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            controller.displayErrorMessage(EditProfileActivity.this, "No has editado ningún dato");
                        }
                    });

                }
            }


        } else if (view.getId() == binding.profileImage.getId()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_LOAD_IMAGE);

        }
    }

    /**
     * Called when an activity launched by this activity exits, giving the requestCode, resultCode,
     * and data associated with it.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be
     *                    attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                newfile = loadImage(data);
            }
        }
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on
     * ActivityCompat.requestPermissions().
     *
     * @param requestCode  The request code passed in ActivityCompat.requestPermissions().
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions. This array is the same
     *                     length as the requested permissions array. The elements of this array are
     *                     either PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                newfile = loadImage(intent);
            } else {
                Toast.makeText(this, "Permiso denegado para leer el almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Load an image selected from the gallery and display it in the profile image view.
     *
     * @param data The intent containing the image data.
     * @return The File object representing the loaded image file.
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
     * Check if any of the input fields (username, full name, email, phone) are empty.
     *
     * @return true if any of the fields are empty, false otherwise.
     */
    private boolean areFieldsEmpty() {
        return binding.tvUsername.getText().toString().equals("") ||
                binding.tvFullname.getText().toString().equals("") ||
                binding.tvEmail.getText().toString().equals("") ||
                binding.tvPhone.getText().toString().equals("");

    }


    /**
     * Check if the email field is valid.
     *
     * @return true if the email is valid, false otherwise.
     */
    private boolean isEmailValid() {
        String email = binding.tvEmail.getText().toString().trim();
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
     * Check if the phone number field is valid.
     *
     * @return true if the phone number is valid, false otherwise.
     */
    private boolean isPhoneNumberValid() {
        String phoneNumber = binding.tvPhone.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            return false;
        }
        return isNumeric(phoneNumber);
    }

    /**
     * Check if a given string consists only of numeric characters.
     *
     * @param str the string to check
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


}
