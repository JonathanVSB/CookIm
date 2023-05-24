package com.example.cookim.dao;

import com.example.cookim.model.DataResult;
import com.example.cookim.model.recipe.Comment;
import com.example.cookim.model.recipe.Ingredient;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.recipe.Step;
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
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class CommentDao {

    public CommentDao() {


    }

    /**
     * Retrieves all comments from the specified path using a POST request.
     *
     * @param path   The URL path to retrieve the comments from.
     * @param token  The authentication token.
     * @param id     The ID used as a parameter in the request.
     * @return A list of Comment objects representing the comments retrieved.
     */
    public List<Comment> getAllComments(String path, String token, long id) {
        List<Comment> data = new ArrayList<>();
        String param = token + ":" + String.valueOf(id);

        try {
            // HTTPS request
            System.out.println("ENTRA  " + path);
            URL url = new URL(path);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String authHeader = "Bearer " + param;
            connection.setRequestProperty("Authorization", authHeader);

            // Write parameters to the request
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(param.getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            if (connection != null) {
                // read Stream
                InputStream inputStream = connection.getInputStream();

                // parse the response into UserModel object

                data = parseCommentList(inputStream);

                inputStream.close();

            }


        } catch (Exception e) {

            System.out.println("ERROR: " + e.toString());
        }


        return data;
    }

    /**
     * Parses the JSON response from the input stream into a list of Comment objects.
     *
     * @param inputStream The input stream containing the JSON response.
     * @return A list of Comment objects parsed from the JSON response.
     */
    private List<Comment> parseCommentList(InputStream inputStream) {
        List<Comment> result = new ArrayList<>();

        try {
            // Initializes a BufferedReader object to read the InputStream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Parses the JSON response and extracts the "data" array
            JsonObject responseJson = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            JsonArray commentsJson = responseJson.getAsJsonArray("data");

            // Parses each comment object in the array and adds it to the result list
            for (JsonElement commentJson : commentsJson) {
                // Extracts the comment data from the JSON object
                int id = commentJson.getAsJsonObject().get("id").getAsInt();
                int user_id = commentJson.getAsJsonObject().get("id_user").getAsInt();
                String username = commentJson.getAsJsonObject().get("username").getAsString();
                int recipe_id = commentJson.getAsJsonObject().get("id_recipe").getAsInt();
                String text = commentJson.getAsJsonObject().get("text").getAsString();
                int id_parent = commentJson.getAsJsonObject().get("id_parent_comment").getAsInt();
                String path = commentJson.getAsJsonObject().get("path_user_profile").getAsString();

                // Creates a new Comment object with the extracted data
                Comment comment = new Comment(id, user_id, username, path, recipe_id, text, id_parent);

                // Adds the new Comment object to the list
                result.add(comment);
            }

            // Closes the BufferedReader
            bufferedReader.close();

        } catch (IOException e) {
            // Debugging statement
            System.out.println("Error al leer la respuesta: " + e.toString());
        } catch (JsonSyntaxException e) {
            // Debugging statement
            System.out.println("Error al analizar la respuesta JSON: " + e.toString());
        }

        return result;
    }

    /**
     * Adds a new comment to the specified path using the provided token and comment data.
     *
     * @param path    The URL path to send the request to.
     * @param token   The authentication token.
     * @param comment The comment to be added.
     * @return A DataResult object representing the result of the operation.
     */
    public DataResult addNewComment(String path, String token, Comment comment) {
        DataResult result = null;
        String param = token + ":" + comment.getId_recipe() + ":"+comment.getText()+ ":" + comment.getId_parent() ;
        try {

            System.out.println("ENTRA  " + path);
            java.net.URL url = new URL(path);

            //HTTPS request
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            System.out.println(param);

            String authHeader = "Bearer " + param;
            connection.setRequestProperty("Authorization", authHeader);

            // Write parameters to the request
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(param.getBytes(StandardCharsets.UTF_8));
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
            System.out.println(e.toString());
            return null;
        }
        return result;


    }

    /**
     * Parses the response from an InputStream into a DataResult object.
     *
     * @param inputStream The InputStream containing the response data.
     * @return A DataResult object representing the parsed response, or null if there was an error.
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
}


