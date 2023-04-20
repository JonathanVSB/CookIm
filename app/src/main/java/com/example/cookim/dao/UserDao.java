package com.example.cookim.dao;

import com.example.cookim.model.DataResult;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class UserDao {

    public UserDao() {

    }

    /**
     * Reads Http File and writes in internal Storage
     *
     * @param urlString  : Url to read File
     * @param parameters : Parameters for the HTTP POST request
     */
    public DataResult readResponse(String urlString, String parameters) {

        DataResult result = null;
        int i = 0;
        try {
            //HTTP request
            System.out.println("ENTRA  " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String authHeader = "Bearer " + parameters;
            connection.setRequestProperty("Authorization", authHeader);


            // Write parameters to the request
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(parameters.getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into UserModel object

                result = parseResponse(inputStream);

                //
                inputStream.close();

            }

        } catch (Exception e) {
            //Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

//        return DataResult;
        return result;
    }

    /**
     * Reads the token received from server and saves it String variable
     *
     * @param inputStream
     * @return
     */
    public DataResult parseResponse(InputStream inputStream) {

        String jsonString = null;
        DataResult result = null;

        try {
            // Initializes a BufferedReader object to read the InputStream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Initializes a StringBuilder object to hold the JSON-formatted string
            StringBuilder stringBuilder = new StringBuilder();

            // Reads each line of the InputStream and appends it to the StringBuilder object
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                stringBuilder.append(linea);
            }

            // Closes the BufferedReader
            bufferedReader.close();

            // Converts the StringBuilder object to a string
            jsonString = stringBuilder.toString();

            // Debugging statement
            System.out.println("Respuesta JSON: " + jsonString);

            if (jsonString.trim().startsWith("{") && jsonString.trim().endsWith("}")) {
                Gson gson = new Gson();
                result = gson.fromJson(jsonString, DataResult.class);
            } else {
                // Debugging statement
                System.out.println("La respuesta no es un objeto JSON válido");
            }

        } catch (IOException e) {
            //Debugging statement
            System.out.println("Error al leer la respuesta: " + e.toString());
        }

        // Returns the DataResult object or null if there was an error
        return result;
    }


    public UserModel readUserResponse(String urlString, String token) {
        UserModel result = null;
        try {
            // HTTPS request
            System.out.println("ENTRA  " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set authorization header with token
            String authHeader = "Bearer " + token;
            connection.setRequestProperty("Authorization", authHeader);

            // Set content type to form url encoded
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Write parameters to the request body
            String requestBody = "";
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into DataResult object
                result = parseUserModel(inputStream);

                inputStream.close();

            }

        } catch (Exception e) {
            //  Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + e.toString());
        }

        return result;
    }

    /**
     * Reads the Json answer of the server and transforms it in DataResult object
     *
     * @param inputStream
     * @return
     */


    public UserModel parseUserModel(InputStream inputStream) {
        UserModel result = null;
        DataResult data = null;

        try {
            // Initializes a BufferedReader object to read the InputStream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Initializes a StringBuilder object to hold the JSON-formatted string
            StringBuilder stringBuilder = new StringBuilder();

            // Reads each line of the InputStream and appends it to the StringBuilder object
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                stringBuilder.append(linea);
            }

            // Closes the BufferedReader
            bufferedReader.close();

            // Removes the quotes around the braces
            String jsonString = stringBuilder.toString().replaceAll("\"\\{", "{").replaceAll("\\}\"", "}");


            // Debugging statement
            System.out.println("Respuesta JSON modificada: " + jsonString);

            // Checks if the modified string starts and ends with "{" and "}"
            if (jsonString.trim().startsWith("{") && jsonString.trim().endsWith("}")) {

                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

                if (jsonObject.has("data")) {
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");
                    result = new UserModel();
                    result.setId(dataObject.get("id").getAsLong());
                    result.setUsername(dataObject.get("username").getAsString());
                    result.setFull_name(dataObject.get("full_name").getAsString());
                    result.setEmail(dataObject.get("email").getAsString());
                    result.setPhone(dataObject.get("phone").getAsString());
                    if (dataObject.has("path_img")) {
                        result.setPath_img(dataObject.get("path_img").getAsString());
                    }

                    if (dataObject.has("description")) {
                        result.setDescription(dataObject.get("description").getAsString());
                    }
                    result.setId_rol(dataObject.get("id_rol").getAsLong());
                } else {
                    // Debugging statement
                    System.out.println("La respuesta indica un error");
                }

            } else {
                // Debugging statement
                System.out.println("La respuesta no es un objeto JSON válido");
            }
        } catch (IOException e) {
            //Debugging statement
            System.out.println("Error al leer la respuesta: " + e.toString());
        } catch (JsonSyntaxException e) {
            // Debugging statement
            System.out.println("Error al analizar la respuesta JSON: " + e.toString());
        }

        // Returns the UserModel object
        return result;
    }
}
