package com.example.cookim.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityPresentationBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;

import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PresentationActivity extends Activity {


    private ActivityPresentationBinding binding;

    String token;
    Model model;
    Controller controller;

    ExecutorService executor;


    /**
     * Called when the activity is starting. This is where most initialization
     * should go: calling setContentView(int) to inflate the activity's UI,
     * using findViewById to programmatically interact with widgets in the UI,
     * and initializing the activity's data.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the data it most recently supplied in
     *                           onSaveInstanceState(Bundle). Note: Otherwise, it is null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());

        executor = Executors.newSingleThreadExecutor();
        model  = new Model(this);
        controller = new Controller();


        token = model.readFile(getApplicationContext(), "token");



        if (token == null) {
            displayLogInPage();

        } else if (token != null) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //validates the token
                    DataResult result = model.autologin(token,getApplicationContext());

                    //if resuls is not null
                    if (result != null) {

                        //if result.getResult()==1 validation is correct
                        if (result.getResult().equals("1")) {
                            showHomePage();
                            finish();
                        }
                        //Token is not validated
                        else {
                            displayLogInPage();
                            finish();
                        }
                    }else if (result.getResult().equals("0000")) {
                        controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
                    } else {
                        displayLogInPage();
                        finish();

                    }

                }
            });


        }

    }

    /**
     * Display the login Page
     */
    private void displayLogInPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * display home page view
     */
    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }



}
