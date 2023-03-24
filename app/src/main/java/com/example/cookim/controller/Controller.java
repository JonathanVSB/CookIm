package com.example.cookim.controller;

import com.example.cookim.model.Model;
import com.example.cookim.model.user.LoginModel;
import com.example.cookim.model.user.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Controller {


    private List<UserModel> userList = new ArrayList<>();

    Model model;

    public Controller() {

    }

    private void HomePage() {

    }

    public boolean validateUser(LoginModel loginModel) {
        boolean result = false;

        //Search matches in list of users
        for (UserModel usr : userList) {
            //if find matches result is true
            if (usr.getUsername().equals(loginModel.getUserName()) && usr.getPassword().equals(loginModel.getPassword())) {
                result = true;
            }

        }

        return result;
    }

    public boolean validateUserServer(LoginModel loginModel) {
        boolean validation = false;
        String url = "http://192.168.1.47:7070/login";
        String username = loginModel.getUserName();
        String password = loginModel.getPassword();

        try {
            // Creamos instancia URL
            URL obj = new URL(url);

            // Creamos una conexión HTTP con el servidor
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Establecemos el método de solicitud POST
            con.setRequestMethod("POST");

            // Establecemos el tipo de contenido de la solicitud como JSON
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Creamos un objeto JSON para enviar las credenciales del usuario
            JSONObject requestJson = new JSONObject();
            requestJson.put("username", username);
            requestJson.put("password", password);

            // Escribimos los datos de la solicitud al servidor se escriben los parámetros de la
            // solicitud en él en forma de bytes.
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(requestJson.toString().getBytes(StandardCharsets.UTF_8));
            }

            // Obtenemos la respuesta del servidor y la leemos como un objeto JSON
            JSONObject responseJson;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                responseJson = new JSONObject(response.toString());
            }

            // Verificamos si las credenciales son válidas y obtenemos el token de sesión
            if (responseJson.getBoolean("success")) {
                String token = responseJson.getString("token");
                // Aquí podemos guardar el token en una variable global o en SharedPreferences para mantener
                // la sesión iniciada en futuras solicitudes.
                validation = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return validation;

    }
}
