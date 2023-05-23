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
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        model  = new Model(this);
        controller = new Controller();
        token = model.readFile(getApplicationContext(), "token");

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

            if (areFieldsEmpty()) {
                binding.erroMsg.setText("*Debes rellenar todos los campos*");
                binding.erroMsg.setVisibility(View.VISIBLE);
            }else if (!toWeakPass()) {
                binding.erroMsg.setText("*La contraseña es demasiado debil*");
                binding.erroMsg.setVisibility(View.VISIBLE);

            } else {
                if (!binding.tvnewPass.getText().toString().equals(binding.tvnewPass2.getText().toString())) {
                    //TODO
                    binding.erroMsg.setText("*Las nuevas contraseñas no coinciden*");
                    binding.erroMsg.setVisibility(View.VISIBLE);
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

                            }else if (result.getResult().equals("0000")) {
                                controller.displayActivity(getApplicationContext(),NoConnectionActivity.class);
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

    /**
     * Check the fields of the view to check if any of them is empty
     *
     * @return
     */
    private boolean areFieldsEmpty() {
        return binding.tvnewPass.getText().toString().isEmpty()||
                binding.tvPass.getText().toString().isEmpty() ||
                binding.tvnewPass2.getText().toString().isEmpty();

    }
}
