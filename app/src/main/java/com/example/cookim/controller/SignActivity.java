package com.example.cookim.controller;

import android.content.Intent;
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

import com.example.cookim.R;
import com.example.cookim.databinding.ActivitySigninBinding;

public class SignActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            }
        } else if (v.getId() == binding.profileImage.getId()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }
    }

    private boolean areFieldsEmpty() {
        return binding.etUsername.getText().toString().equals("") ||
                binding.etPassword.getText().toString().equals("") ||
                binding.etFullname.getText().toString().equals("") ||
                binding.etEmail.getText().toString().equals("") ||
                binding.etTel.getText().toString().equals("") ||
                !isPhoneNumberValid();
    }

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

    private boolean isPhoneNumberValid() {
        String phoneNumber = binding.etTel.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            return false;
        }
        return TextUtils.isDigitsOnly(phoneNumber);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
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
}
