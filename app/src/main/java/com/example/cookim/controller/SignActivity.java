package com.example.cookim.controller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.widget.Toast;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivitySigninBinding;

public class SignActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Intent data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
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
            }else{
                binding.errormsg.setVisibility(View.INVISIBLE);
                if (createUser()){
                    //Display the next page of settings
                }
            }
        } else if (v.getId() == binding.profileImage.getId()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);

        }
    }

    /**
     * Send petition to server to create a new user using the text of the fields.
     * returns, true if the user is correctly inserted, false otherwise
     * @return
     */
    private boolean createUser() {


        return true;
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
                binding.etTel.getText().toString().equals("") ||
                !isPhoneNumberValid();
    }

    /**
     * Checks if the email has the correct structure
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
     * checks the number to prevents wrong number formats like string or chars
     * @return
     */
    private boolean isPhoneNumberValid() {
        String phoneNumber = binding.etTel.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            return false;
        }
        return TextUtils.isDigitsOnly(phoneNumber);
    }


    /**
     *  Search for an image in storage and sets the imageview with
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
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
                loadImage(data);
            }
        }
    }

    /**
     *
     * @param requestCode The request code passed in {@link #requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
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
     * @param data
     */
    private void loadImage(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        binding.profileImage.setImageBitmap(bitmap);
    }


}
