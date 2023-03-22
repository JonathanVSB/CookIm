package com.example.cookim.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookim.HomePage;
import com.example.cookim.controller.Controller;
import com.example.cookim.databinding.ActivityLoginBinding;
import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    View.OnClickListener listener;
    Switch swLogOption;


    ExecutorService executor;
    Handler handler;

    Model model;
    boolean validate = false;
    Controller controller = new Controller();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        setContentView(binding.getRoot());
        model = new Model();

        String username = binding.etUsername.getText().toString();
        initElements();


    }

    /**
     * Displays the home page of the app
     */
    private void showHomePage(UserModel user) {
        Intent intent = new Intent(this, HomePage.class);

        intent.putExtra("userModel", user);

        startActivity(intent);
    }


    /**
     * initialize all elements
     */
    private void initElements() {
        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction(v);
            }
        });

    }

    /**
     * gets the parameters introduced by the user to log in the app
     * to prepare a validation of them.
     *
     * @param v
     */
    private void loginAction(View v) {
        if (v.getId() == binding.btLogin.getId()) {
            String name = binding.etUsername.getText().toString();
            String password = binding.etPass.getText().toString();
            LoginModel userData = new LoginModel(name, password);

            validation(userData);

        }
    }

    /**
     * Adds all necessaries elements to listener
     */
    private void addElementsToListener() {
        binding.btLogin.setOnClickListener(listener);


    }


    /**
     * Prepares the http request and gestionated the action if validation is correct
     *
     * @param loginModel
     */
    private void validation(LoginModel loginModel) {
        String url = "http://192.168.127.80:7070/login";
        String username = loginModel.getUserName();
        String password = loginModel.getPassword();
        String parametros = "username=" + username + "&password=" + password;

        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //Background work here
                    //System.out.println("ENTRA");
                    UserModel userModel = readResponse(url, parametros);
                    if (userModel != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute

                                showHomePage(userModel);
                                // Una Vegada finalitza el Background work (Thread.sleep(6000);) s'executa això
                            }
                        });
                    } else {
                        //Toast.makeText(this, "credential incorrect", Toast.LENGTH_LONG).show();
                        binding.errormsg.setVisibility(View.VISIBLE);
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("ERROR" + e.toString());
        }

    }

    /**
     * Reads Http File and writes in internal Storage
     *
     * @param urlString  : Url to read File
     * @param parameters : Parameters for the HTTP POST request
     */
    private UserModel readResponse(String urlString, String parameters) {
        boolean respuesta = false;
        UserModel user = null;
        int i = 0;
        try {
            // Petició HTTP
            System.out.println("ENTRA  " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Write parameters to the request
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(parameters.getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            // If the response does not enclose an entity, there is no need
//            // to worry about connection release
//            if (connection != null) {
//                // Read Stream
//                InputStream inputStream = connection.getInputStream();
//
////                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
////                StringBuilder stringBuilder = new StringBuilder();
////                String linea;
////                while ((linea = bufferedReader.readLine()) != null) {
////                    stringBuilder.append(linea).append("");
////                }
////                bufferedReader.close();
////                String contenido = stringBuilder.toString();
////
////                System.out.println("Respuesta: " + contenido);
////
////                respuesta = Boolean.parseBoolean(contenido);
//
//                // Handle the response here (e.g., parse JSON, update UI, etc.)
//
//                // Close the input stream
//                inputStream.close();

            if (connection != null) {
                // Leer Stream
                InputStream inputStream = connection.getInputStream();

                // Parsear la respuesta y crear un objeto UserModel
                user = parseUser(inputStream);

                // Cerrar el input stream
                inputStream.close();

            }

        } catch (Exception e) {
            System.out.println("Ex: " + i + e.toString());
        }

        return user; // Retornar el objeto UserModel creado
    }

    private UserModel parseUser(InputStream inputStream) {
        UserModel user = new UserModel();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                // Analizar la respuesta y actualizar el objeto UserModel
                // según el formato esperado
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Error al analizar la respuesta: " + e.toString());
        }

        return user;

    }
}


