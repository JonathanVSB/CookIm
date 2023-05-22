package com.example.cookim.controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.cookim.R;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityEditPasswordBinding;
import com.example.cookim.model.Data;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditPasswordActivity extends Activity {

    ActivityEditPasswordBinding binding;
    Controller controller;
    Model model;
    ExecutorService executor;
    String token;
    DataResult result;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        model = new Model();
        controller = new Controller();
        token = model.readToken(this);
        result = null;
        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
        binding = ActivityEditPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateActions(view);
            }
        });
        binding.btsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestionateActions(view);
            }
        });
    }

    /**
     * Process the actions of the view
     *
     * @param v
     */
    private void gestionateActions(View v) {
        if (v.getId() == binding.ivcancel.getId()) {
            controller.displayActivity(this, HomeActivity.class);
        } else if (v.getId() == binding.btsend.getId()) {

            if (!toWeakPass()) {
                binding.erroMsg.setText("*La contraseña es demasiado debil*");
                binding.erroMsg.setVisibility(View.VISIBLE);
            } else {
                if (!binding.tvnewPass.getText().toString().equals(binding.tvnewPass2.getText().toString())) {
                    //TODO
                    //the camps of new pass are not the same
                } else if (binding.tvnewPass.getText().toString().equals(binding.tvnewPass2.getText().toString())) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            result = model.changePass(token, binding.tvPass.getText().toString(), binding.tvnewPass.getText().toString(), getApplicationContext());

                            if (result.getResult().equals("1")) {
                                controller.displayActivity(getApplicationContext(), HomeActivity.class);
                            } else if (result.getResult().equals("2")) {
                                //TODO
                                //wrong current pass
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.erroMsg.setText(result.getData().toString());
                                    }
                                });

                            } else if (result.getResult().equals("3")) {
                                //TODO
                                //The pass its the same
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.erroMsg.setText(result.getData().toString());
                                    }
                                });

                            } else {
                                //TODO
                                //token error or others
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.erroMsg.setText(result.getData().toString());
                                    }
                                });

                            }
                        }
                    });

                }
            }


        }
    }

    /**
     * prevents the user to introduce very short password
     *
     * @return
     */
    private boolean toWeakPass() {
        String pass = binding.tvnewPass.getText().toString();
        if (pass.length() < 6) {
            return false;
        }
        return true;
    }
}
