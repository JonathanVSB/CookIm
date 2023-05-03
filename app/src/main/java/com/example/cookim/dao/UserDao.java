package com.example.cookim.dao;

import com.example.cookim.model.DataResult;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class UserDao {

    NukeSSLCerts n;

    public UserDao() {
        n = new NukeSSLCerts();
        n.nuke();


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
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
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


    /**
     * creates a request to the path specified, using the token of the user as validation key
     * @param urlString
     * @param token
     * @return
     */
    public UserModel readUserResponse(String urlString, String token) {
        UserModel result = null;
        try {
            // HTTPS request
            System.out.println("ENTRA  " + urlString);
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
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

    /**
     * Gets the data introduced by user in all camps and send petition to create this new user
     * if the user adds profile image it sends the image to
     * @param username
     * @param password
     * @param full_name
     * @param email
     * @param phone
     * @param id_rol
     * @param file
     * @return
     */
    public DataResult validationNewUser(String username, String password, String full_name, String email, String phone, long id_rol, File file, String path) {
//        String urlString = URL3 + "sign-in";
        DataResult result = null;

        try {
            System.out.println("ENTRA  " + path);
            URL url = new URL(path);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Create the multipart/form-data request body
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            OutputStream os = connection.getOutputStream();
            DataOutputStream wr = new DataOutputStream(os);
            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"username\"\r\n\r\n" + username + "\r\n");
            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"password\"\r\n\r\n" + password + "\r\n");
            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"full_name\"\r\n\r\n" + full_name + "\r\n");
            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"email\"\r\n\r\n" + email + "\r\n");
            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"phone\"\r\n\r\n" + phone + "\r\n");
            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"id_rol\"\r\n\r\n" + id_rol + "\r\n");

            // Add the image file if it is not null
            if (file != null) {
                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"img\"; filename=\"" + file.getName() + "\"\r\n");
                wr.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()) + "\r\n\r\n");
                FileInputStream inputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    wr.write(buffer, 0, bytesRead);
                }
                wr.writeBytes("\r\n");
                inputStream.close();
            }

            wr.writeBytes("--" + boundary + "--\r\n");
            wr.flush();
            wr.close();

            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into DataResult object
                result = parseResponse(inputStream);

                inputStream.close();
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }

        return result;
    }

    public DataResult validateToken(String urlString, String token) {
        DataResult result = null;
        try {
            // HTTPS request
            System.out.println("ENTRA  " + urlString);
            java.net.URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
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
                result = parseResponse(inputStream);
                inputStream.close();

            } else {
                // Debugging statement
                System.out.println("Error al conectar con el servidor: " + connection.getResponseMessage());
            }

            connection.disconnect();

        } catch (Exception e) {
            // Debugging statement
            System.out.println("Error al conectar con el servidor: " + e.toString());
        }

        return result;
    }
}
