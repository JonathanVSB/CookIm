package com.example.cookim.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivityNoConnectionBinding;

public class NoConnectionActivity extends Activity {


    private ActivityNoConnectionBinding binding;
    private int code;
    Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoConnectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        handler = new Handler(Looper.getMainLooper());

        Intent intent = getIntent();

        code = intent.getIntExtra("ErrorCode", -1);

        handler.post(new Runnable() {
            @Override
            public void run() {
                loadPage(code);
            }
        });


    }

    /**
     * loads the data of error of the view
     * @param code
     */
    private void loadPage(int code) {

        switch (code) {
            case 101:
                binding.dataMsg.setText("No hay conexión con el servidor");
                binding.img.setImageResource(R.drawable.ic_exclamation);
                break;

            case 102:
                binding.dataMsg.setText("No hay conexión con a internet");
                binding.img.setImageResource(R.drawable.ic_wifi_slash);
                break;


        }
    }
}
