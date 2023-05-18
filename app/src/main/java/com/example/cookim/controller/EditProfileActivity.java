package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

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

public class EditProfileActivity extends Activity {

    UserModel user;
    private ActivityEditProfileBinding binding;
    String token;
    Model model;
    Controller controller;
    File newfile;
    File currentFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = new Model();
        newfile = null;
        currentFile = null;
        controller = new Controller();
        Intent intent = getIntent();

        token = model.readToken(getApplicationContext());

        if (token != null && !token.isEmpty()) {
            long id = intent.getLongExtra("id", 0);
            String username = intent.getStringExtra("username");
            String full_name = intent.getStringExtra("fullname");
            String phoneNumber = intent.getStringExtra("phone");
            String email = intent.getStringExtra("email");
            String pic = intent.getStringExtra("path_img");
            user = new UserModel(id, username,full_name,email, phoneNumber,pic);
            if (user != null) {

                loadView(user);

            } else {
                controller.displayLogInPage(this, LoginActivity.class);
            }


        } else {
            controller.displayLogInPage(this, LoginActivity.class);
        }
    }

    /**
     * Loads the view with the data of given user
     */
    private void loadView(UserModel user) {
        //complete all camps.

        //Check the username
        if (!user.getUsername().isEmpty()){
            binding.tvUsername.setText(user.getUsername());
        }
        //Check the full name
        if (!user.getFull_name().isEmpty()){
            binding.tvFullname.setText(user.getFull_name());
        }
        //Check the email
        if (!user.getEmail().isEmpty()){
            binding.tvEmail.setText(user.getEmail());
        }
        //Check the pone
        if (!user.getPhone().isEmpty()){
            binding.tvPhone.setText(user.getPhone());
        }
        //Check the image
        if (!user.getPath_img().isEmpty()){
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

            //Gets the current profile image fom the user
            Drawable drawable = binding.profileImage.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            try {
                currentFile = File.createTempFile("stepImage", ".jpg", getCacheDir());
                FileOutputStream fos = new FileOutputStream(currentFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
    }

    /**
     * Process the acions selected by the user
     * @param view
     */
    private void gestionateActions(View view) {

        if (view.getId() == binding.ivcancel.getId()){
            controller.displayActivity(getApplicationContext(), HomeActivity.class);
        }else if (view.getId() == binding.ivaccept.getId()){
            //TODO

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

            if (!user.equals(newData) && currentFile == newfile){

                //TODO

            }else{
                //The data of the user has not suffered changes

            }




        }
    }
}
