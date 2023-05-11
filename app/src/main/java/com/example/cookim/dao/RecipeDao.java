package com.example.cookim.dao;

import android.widget.Toast;

import com.example.cookim.model.DataResult;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class RecipeDao {
    public RecipeDao() {


    }

    /**
     * Creates HTTP petition, to get the data of all recipes
     */
    public List<Recipe> loadRecipes(String path) {
        List<Recipe> recipes = new ArrayList<>();
        String petition = path;
        try {
            //Generem l'objecte URL que fa servir HttpURLConnection
            URL url = new URL(petition);

            //L'objecte HttpUrlConnection ens permet manipular una connexió HTTP.
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            //Connectem amb el servidor
            conn.connect();

            String response = getReponseBody(conn);

            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            if (jsonObject.get("result").getAsInt() == 1) {
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                Gson gson = new Gson();
                for (JsonElement element : jsonArray) {
                    Recipe recipe = gson.fromJson(element, Recipe.class);
                    recipes.add(recipe);
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            //hrow new RuntimeException(e);
        }
        return recipes;
    }

    /**
     * gets the body response of HTTP request and returnsit as String
     *
     * @param con
     * @return
     * @throws IOException
     */
    public String getReponseBody(HttpURLConnection con) throws IOException {
        BufferedReader br;

        if (con.getResponseCode() >= 400) {
            //if code response superior to 400 displays error
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        } else {
            //if code response lower than 400 displays error gets response
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        return sb.toString();

    }


    /**
     * Reads Http File and writes in internal Storage
     *
     * @param urlString  : Url to read File
     * @param parameters : Parameters for the HTTP POST request
     */
    public DataResult readResponse(String urlString, String parameters) {

        DataResult result = null;
        String param = parameters;
        int i = 0;
        try {
            //HTTP request
            System.out.println("ENTRA  " + urlString);
            java.net.URL url = new URL(urlString);
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
     * Sends the token of the user. if finds this session else, returns null. Then search the recipe
     * by his id. if find matches, return the recipe and the steps
     * else returns null recipe
     *
     * @param path
     * @param id
     * @param token
     * @return
     */
    public Recipe loadRecipeSteps(String path, int id, String token) {

        Recipe result = null;
        String param = token + ":" + String.valueOf(id);
        int i = 0;
        try {
            // HTTP request
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

                result = parseRecipe(inputStream);

                inputStream.close();

            }

        } catch (Exception e) {
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

        return result;

    }

    /**
     * Parses the json response into Recipe object
     *
     * @param inputStream
     * @return
     */
    public Recipe parseRecipe(InputStream inputStream) {
        Recipe result = null;

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

                if (jsonObject.has("result") && jsonObject.has("data")) {
                    int resultCode = jsonObject.get("result").getAsInt();
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");

                    if (resultCode == 1) {
                        int id = dataObject.get("id").getAsInt();
                        int user_id = dataObject.get("id_user").getAsInt();
                        String name = dataObject.get("name").getAsString();
                        String description = dataObject.get("description").getAsString();
                        String path_img = dataObject.get("path_img").getAsString();
                        double rating = dataObject.get("rating").getAsDouble();
                        int likes = dataObject.get("likes").getAsInt();
                        String user = dataObject.get("user_name").getAsString();
                        String path = dataObject.get("path").getAsString();

                        List<Ingredient> ingredients = new ArrayList<>();
                        JsonArray ingredientsArray = dataObject.getAsJsonArray("ingredients");
                        for (JsonElement ingredientElement : ingredientsArray) {
                            JsonObject ingredientObject = ingredientElement.getAsJsonObject();
                            int ingredientId = ingredientObject.get("id").getAsInt();
                            String ingredientName = ingredientObject.get("name").getAsString();
                            ingredients.add(new Ingredient(ingredientId, ingredientName));
                        }

                        List<Step> steps = new ArrayList<>();
                        JsonArray stepsArray = dataObject.getAsJsonArray("steps");
                        for (JsonElement stepElement : stepsArray) {
                            JsonObject stepObject = stepElement.getAsJsonObject();
                            int stepId = stepObject.get("id").getAsInt();
                            int recipe_id = stepObject.get("recipe_id").getAsInt();
                            int step_number = stepObject.get("step_number").getAsInt();
                            String stepDescription = stepObject.get("description").getAsString();
                            String stepImg = stepObject.get("path").getAsString();
                            steps.add(new Step(stepId, recipe_id, step_number, stepDescription, stepImg));
                        }

                        result = new Recipe(id, user_id, name, description, path_img, rating, likes, user, path, steps, ingredients);
                    } else {
                        // Debugging statement
                        result = null;
                    }
                } else {
                    // Debugging statement
                    System.out.println("La respuesta no tiene el formato esperado");
                }

            } else {
                // Debugging statement
                System.out.println("La respuesta no es un objeto JSON válido");
            }
        } catch (IOException e) {
            // Debugging statement
            System.out.println("Error al leer la respuesta: " + e.toString());
        } catch (JsonSyntaxException e) {
            // Debugging statement
            System.out.println("Error al analizar la respuesta JSON: " + e.toString());
        }

        // Returns the UserModel object
        return result;
    }

    /**
     * search all recipes of the user using his token as validation key
     *
     * @param path
     * @param token
     * @return
     */
    public List<Recipe> loadMyRecipes(String path, String token, long id) {
        List<Recipe> recipes = new ArrayList<>();
        String param = token + ":" + String.valueOf(id);
        int i = 0;
        try {
            // HTTP request
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

                recipes = parseRecipeList(inputStream);

                inputStream.close();

            }

        } catch (Exception e) {
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

        return recipes;

    }

    /**
     * Parses the Json response into Recipe List
     *
     * @param inputStream
     * @return
     */
    public List<Recipe> parseRecipeList(InputStream inputStream) {
        List<Recipe> result = new ArrayList<>();

        try {
            // Initializes a BufferedReader object to read the InputStream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Parses the JSON response and extracts the "data" object
            JsonObject responseJson = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            JsonObject dataJson = responseJson.get("data").getAsJsonObject();

            // Extracts the "recipes" array from the "data" object
            JsonArray recipesJson = dataJson.get("recipes").getAsJsonArray();

            // Parses each recipe object in the "recipes" array and adds it to the result list
            for (JsonElement recipeJson : recipesJson) {
                // Extracts the recipe data from the JSON object
                int id = recipeJson.getAsJsonObject().get("id").getAsInt();
                int user_id = recipeJson.getAsJsonObject().get("id_user").getAsInt();
                String name = recipeJson.getAsJsonObject().get("name").getAsString();
                String description = recipeJson.getAsJsonObject().get("description").getAsString();
                String path_img = recipeJson.getAsJsonObject().get("path_img").getAsString();
                double rating = recipeJson.getAsJsonObject().get("rating").getAsDouble();
                int likes = recipeJson.getAsJsonObject().get("likes").getAsInt();
                //String user_name = recipeJson.getAsJsonObject().get("user_name").getAsString();
                //String path = recipeJson.getAsJsonObject().get("path").getAsString();

                // Creates a new Recipe object with the extracted data and empty ingredient and step lists
                Recipe recipe = new Recipe(id, user_id, name, description, path_img, rating, likes);

                // Adds the new Recipe object to the list of recipes
                result.add(recipe);
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
     * send pttion to add new Recipe to server
     *
     * @param path
     * @param token
     * @param recipe
     * @return
     */
    public DataResult addRecipe(String path, String token, Recipe recipe, File file) {
        DataResult result = null;

        try {
            Gson gson = new Gson();
            System.out.println("ENTRA  " + path);

            URL url = new URL(path);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Create the multipart/form-data request body
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            OutputStream os = connection.getOutputStream();
            DataOutputStream wr = new DataOutputStream(os);

            // Send recipe as JSON
            wr.writeBytes("--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"recipe\"\r\n\r\n" + gson.toJson(recipe) + "\r\n");

            List<Ingredient> ingredients = recipe.getIngredients();
            for (int i = 0; i < ingredients.size(); i++) {
                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"ingredients[" + i + "]\"\r\n\r\n" + gson.toJson(ingredients.get(i)) + "\r\n");
            }

            // Send steps as JSON and images
            List<Step> steps = recipe.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);

                // Send step as JSON
                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"steps[" + i + "]\"\r\n\r\n" + gson.toJson(step) + "\r\n");

                // Send step image
                File stepImage = step.getFile();
                if (stepImage != null) {
                    wr.writeBytes("--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"steps[" + i + "].image\"; filename=\"" + stepImage.getName() + "\"\r\n");
                    wr.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(stepImage.getName()) + "\r\n\r\n");
                    FileInputStream inputStream = new FileInputStream(stepImage);
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        wr.write(buffer, 0, bytesRead);
                    }
                    wr.writeBytes("\r\n");
                    inputStream.close();
                }
            }

            // Send file
            if (file != null) {
                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"\r\n");
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


}




