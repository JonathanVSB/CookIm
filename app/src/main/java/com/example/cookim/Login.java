package com.example.cookim;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    View.OnClickListener listener;
    EditText etUsername, etPass;
    Button btLogin, btGoogle;
    Switch swLogOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        initElements();
        addElementsToListener();
    }

    //Initialize all elements of the layout
    private void initElements(){

        //EditTexts
        etUsername = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPass);

        //Buttons
        btLogin = findViewById(R.id.btLogin);


        //Switch
        swLogOption = findViewById(R.id.swlogoption);

    }

    /**
     * Adds all necessaries elements to listener
     */
    private void addElementsToListener(){



    }
}
