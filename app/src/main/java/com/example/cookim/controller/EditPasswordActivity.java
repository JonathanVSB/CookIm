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

    /**
     * Called when the activity is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
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
     * Handles the actions performed on views.
     * @param v The view on which the action is performed.
     */
    private void gestionateActions(View v) {
        if (v.getId() == binding.ivcancel.getId()) {
            controller.displayActivity(this, HomeActivity.class);
            finish();
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
                                finish();
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
     * Checks if the password is too weak.
     * @return true if the password is strong enough, false otherwise.
     */
    private boolean toWeakPass() {
        String pass = binding.tvnewPass.getText().toString();
        if (pass.length() < 6) {
            return false;
        }
        return true;
    }

    /**
     * Checks if any of the required fields are empty.
     * @return true if any of the fields are empty, false otherwise.
     */
    private boolean areFieldsEmpty() {
        return binding.tvnewPass.getText().toString().isEmpty()||
                binding.tvPass.getText().toString().isEmpty() ||
                binding.tvnewPass2.getText().toString().isEmpty();

    }
}
