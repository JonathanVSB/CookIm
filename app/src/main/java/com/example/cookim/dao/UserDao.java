package com.example.cookim.dao;

import com.example.cookim.exceptions.OpResult;
import com.example.cookim.exceptions.PersistException;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.recipe.Step;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class UserDao {

    NukeSSLCerts n;

    public UserDao() {
        n = new NukeSSLCerts();
        n.nuke();


    }

    /**
     * Sends a POST request to the specified URL and reads the response.
     *
     * @param urlString   the URL to send the request to
     * @param parameters  the request parameters
     * @return the result of the request
     */
    public DataResult readResponse(String urlString, String parameters) {

        DataResult result = null;
        int i = 0;
        try {
            //HTTPS request
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


            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);


            // Write parameters to the request
            String requestBody = "";
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(requestBody.getBytes(StandardCharsets.UTF_8));
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
        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            result = new DataResult();
            result.setResult("0002");
            result.setData("Request timed out");
        } catch (Exception e) {
            //Log.e("readResponse error", "Error during HTTPS request: " + e.toString());
            result = new DataResult();
            result.setResult("0001");
            result.setData("Error during HTTPS request");
        }

//        return DataResult;
        return result;
    }

    /**
     * Parses the JSON response from the InputStream and returns a DataResult object.
     *
     * @param inputStream the InputStream containing the JSON response
     * @return the parsed DataResult object
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
     * Sends an HTTPS request to the specified URL with the provided token and reads the response to obtain a UserModel object.
     *
     * @param urlString the URL to send the request to
     * @param token the authentication token
     * @return the UserModel object obtained from the response
     * @throws PersistException if there is an error during the request or response parsing
     */
    public UserModel readUserResponse(String urlString, String token) throws PersistException {
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

            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);


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

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            throw new PersistException(OpResult.DB_NORESPONSE.getCode());

        } catch (Exception e) {
            //Log.e("readResponse error", "Error during HTTPS request: " + e.toString());

        }

        return result;
    }



    /**
     * Parses the InputStream response into a UserModel object.
     *
     * @param inputStream the InputStream containing the response
     * @return the parsed UserModel object
     */
    public UserModel parseUserModel(InputStream inputStream) {
        UserModel result = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                stringBuilder.append(linea);
            }

            bufferedReader.close();
            String jsonString = stringBuilder.toString().replaceAll("\"\\{", "{").replaceAll("\\}\"", "}");

            System.out.println("Respuesta JSON modificada: " + jsonString);

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

                    if (dataObject.has("recipe_likes")) {
                        JsonArray recipeLikesArray = dataObject.getAsJsonArray("recipe_likes");
                        List<Long> recipeLikes = new ArrayList<>();
                        for (JsonElement recipeLikeElement : recipeLikesArray) {
                            recipeLikes.add(recipeLikeElement.getAsLong());
                        }
                        result.setRecipe_likes(recipeLikes);
                    }

                    if (dataObject.has("nFollowers")) {
                        result.setNumFollowers(dataObject.get("nFollowers").getAsInt());
                    }

                    if (dataObject.has("follow")) {
                        result.setFollow(dataObject.get("follow").getAsBoolean());
                    }
                } else {
                    System.out.println("La respuesta indica un error");
                }

            } else {
                System.out.println("La respuesta no es un objeto JSON válido");
            }
        } catch (IOException e) {
            System.out.println("Error al leer la respuesta: " + e.toString());
        } catch (JsonSyntaxException e) {
            System.out.println("Error al analizar la respuesta JSON: " + e.toString());
        }

        return result;
    }


    /**
     * Validates a new user by sending a request with the user information and optional image file.
     *
     * @param username  the username of the new user
     * @param password  the password of the new user
     * @param full_name the full name of the new user
     * @param email     the email of the new user
     * @param phone     the phone number of the new user
     * @param id_rol    the ID of the role for the new user
     * @param file      the image file of the new user (can be null)
     * @param path      the URL path for the request
     * @return the DataResult object containing the response data
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

            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);


            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into DataResult object
                result = parseResponse(inputStream);

                inputStream.close();
            }

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            result = new DataResult();
            result.setResult("0002");
            result.setData("Request timed out");
        } catch (Exception e) {
            //Log.e("readResponse error", "Error during HTTPS request: " + e.toString());
            result = new DataResult();
            result.setResult("0001");
            result.setData("Error during HTTPS request");
        }

        return result;
    }

    /**
     * Validates a token by sending a request with the token to the specified URL.
     *
     * @param urlString the URL to send the request to
     * @param token     the token to validate
     * @return the DataResult object containing the response data
     */
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
            } catch (Exception e) {
                // Debugging statement
                result = new DataResult();
                result.setResult("2");
                result.setData("Error DataOutputStream: " + e.toString());
                System.out.println("Error DataOutputStream: " + e.toString());
            }

            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);


            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into DataResult object
                result = parseResponse(inputStream);
                inputStream.close();

            } else {
                result = new DataResult();
                result.setResult("3");
                result.setData("Error al conectar con el servidor: " + connection.getResponseMessage());
                // Debugging statement
                System.out.println("Error al conectar con el servidor: " + connection.getResponseMessage());
            }

            connection.disconnect();

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            result = new DataResult();
            result.setResult("0002");
            result.setData("Request timed out");
        } catch (Exception e) {
            //Log.e("readResponse error", "Error during HTTPS request: " + e.toString());
            result = new DataResult();
            result.setResult("0001");
            result.setData("Error during HTTPS request");
        }
        return result;
    }

    /**
     * Reads the response for retrieving data of another user from the specified path using the provided token and user ID.
     *
     * @param path  the path to send the request to
     * @param token the token to authenticate the request
     * @param id    the ID of the user to retrieve data for
     * @return the UserModel object containing the user data
     * @throws PersistException if there is an error during the request or response parsing
     */
    public UserModel readOtherUserResponse(String path, String token, long id) throws PersistException {
        UserModel result = null;
        String param = token + ":" + String.valueOf(id);
        try {
            // HTTPS request
            System.out.println("ENTRA  " + path);
            URL url = new URL(path);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set authorization header with token
            String authHeader = "Bearer " + param;
            connection.setRequestProperty("Authorization", authHeader);

            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);


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

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            throw new PersistException(OpResult.DB_NORESPONSE.getCode());

        } catch (Exception e) {
            //  Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + e.toString());
        }

        return result;
    }

    /**
     * Edits the user data by sending a request to the specified path with the provided token, username, full name, email,
     * phone, path_img, and file.
     *
     * @param path     the path to send the request to
     * @param token    the token to authenticate the request
     * @param username the username of the user
     * @param full_name the full name of the user
     * @param email    the email of the user
     * @param phone    the phone number of the user
     * @param path_img the image path of the user
     * @param file     the image file of the user
     * @return the DataResult object containing the result of the request
     */
    public DataResult editUserData(String path, String token, String username, String full_name, String email, String phone, String path_img, File file) {
        DataResult result = null;
        String param = token + ":" + String.valueOf(username) + ":" + String.valueOf(full_name) + ":" + String.valueOf(email) + ":" + String.valueOf(phone) + ":" + String.valueOf(path_img) + ":" + String.valueOf(file);

        try {
            // HTTPS request
            System.out.println("ENTRA  " + path);
            URL url = new URL(path);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set authorization header with token
            String authHeader = "Bearer " + param;
            connection.setRequestProperty("Authorization", authHeader);

            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);


            String boundary = "*****";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Write parameters to the request body
            String requestBody = "";
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"username\"\r\n\r\n");
                wr.writeBytes(username + "\r\n");

                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"full_name\"\r\n\r\n");
                wr.writeBytes(full_name + "\r\n");

                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"email\"\r\n\r\n");
                wr.writeBytes(email + "\r\n");

                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"phone\"\r\n\r\n");
                wr.writeBytes(phone + "\r\n");

                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"path_img\"\r\n\r\n");
                wr.writeBytes(path_img + "\r\n");

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
            }

            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into DataResult object
                result = parseResponse(inputStream);

                inputStream.close();
            }
        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            result = new DataResult();
            result.setResult("0002");
            result.setData("Request timed out");
        } catch (Exception e) {
            //Log.e("readResponse error", "Error during HTTPS request: " + e.toString());
            result = new DataResult();
            result.setResult("0001");
            result.setData("Error during HTTPS request");
        }

        return result;
    }

}
