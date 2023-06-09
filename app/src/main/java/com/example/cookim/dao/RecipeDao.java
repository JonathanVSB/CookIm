package com.example.cookim.dao;

import android.util.Log;
import android.widget.Toast;

import com.example.cookim.exceptions.OpResult;
import com.example.cookim.exceptions.PersistException;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.recipe.Category;
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
import java.net.SocketTimeoutException;
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
     * Loads recipes from the specified path.
     *
     * @param path the URL path to load recipes from
     * @return a list of Recipe objects
     * @throws PersistException if there is an error in the persistence layer
     */
    public List<Recipe> loadRecipes(String path) throws PersistException {
        List<Recipe> recipes = new ArrayList<>();
        String petition = path;
        try {
            //Generem l'objecte URL que fa servir HttpURLConnection
            URL url = new URL(petition);

            //L'objecte HttpUrlConnection ens permet manipular una connexió HTTP.
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.setConnectTimeout(15 * 1000);
            conn.setReadTimeout(15 * 1000);

            //Connect with server

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
        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            throw new PersistException(OpResult.DB_NORESPONSE.getCode());

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            //hrow new RuntimeException(e);
        }
        return recipes;
    }

    /**
     * Retrieves the response body from the HttpURLConnection.
     *
     * @param con the HttpURLConnection object
     * @return the response body as a string
     * @throws IOException if an error occurs while reading the response body
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
     * Sends an HTTP POST request to the specified URL with the given parameters and reads the response.
     *
     * @param urlString  the URL to send the request to
     * @param parameters the parameters to include in the request
     * @return the parsed DataResult object representing the response, or null if an error occurs
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

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());

        } catch (Exception e) {
            //Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

//        return DataResult;
        return result;
    }

    /**
     * Parses the JSON response from the InputStream into a DataResult object.
     *
     * @param inputStream the InputStream containing the JSON response
     * @return the parsed DataResult object, or null if an error occurs
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
     * Loads the recipe steps from the specified path for the given recipe ID and token.
     *
     * @param path  the URL path to load the recipe steps from
     * @param id    the ID of the recipe
     * @param token the authentication token
     * @return the loaded Recipe object, or null if an error occurs
     * @throws PersistException if a timeout occurs during the request
     */
    public Recipe loadRecipeSteps(String path, int id, String token) throws PersistException {

        Recipe result = null;
        String param = token + ":" + String.valueOf(id);
        int i = 0;
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

            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);

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

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            throw new PersistException(OpResult.DB_NORESPONSE.getCode());

        } catch (Exception e) {
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

        return result;

    }

    /**
     * Parses the recipe data from the InputStream and constructs a Recipe object.
     *
     * @param inputStream the InputStream containing the recipe data
     * @return the parsed Recipe object, or null if an error occurs
     * @throws PersistException if a timeout occurs during the request
     */
    public Recipe parseRecipe(InputStream inputStream) throws PersistException {
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
                        String path_img = "";
                        if (dataObject.has("path_img")) {
                            path_img = dataObject.get("path_img").getAsString();
                        }

                        double rating = dataObject.get("rating").getAsDouble();
                        int likes = dataObject.get("likes").getAsInt();
                        String user = dataObject.get("user_name").getAsString();
                        String path = dataObject.get("path").getAsString();
                        boolean liked = false;
                        boolean saved = false;
                        if (dataObject.has("liked")) {
                            liked = dataObject.get("liked").getAsBoolean();
                        }
                        if (dataObject.has("saved")) {
                            saved = dataObject.get("saved").getAsBoolean();
                        }

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

                        // List<Category> categoryList = new ArrayList<>();
                        //JsonArray categoryArray = dataObject.getAsJsonArray("ingredients");
                        //for (JsonElement categoryElement : categoryArray) {
                        //  JsonObject categoryObject = categoryElement.getAsJsonObject();
                        //String categoryName = categoryObject.get("name").getAsString();
                        //categoryList.add(new Category(categoryName));
                        //}

                        result = new Recipe(id, user_id, name, description, path_img, liked, saved, rating, likes, user, path, steps, ingredients);
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
        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            throw new PersistException(OpResult.DB_NORESPONSE.getCode());
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
     * Loads the list of user recipes from the specified path using the provided parameters.
     *
     * @param path  the URL path for the request
     * @param param the request parameter
     * @return the list of loaded recipes
     * @throws PersistException if a timeout occurs during the request
     */
    public List<Recipe> loadMyRecipes(String path, String param) throws PersistException {
        List<Recipe> recipes = new ArrayList<>();

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

            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);

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

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            throw new PersistException(OpResult.DB_NORESPONSE.getCode());

        } catch (Exception e) {
            System.out.println("PETA EN ESTA LINEA: " + i + e.toString());
        }

        return recipes;

    }

    /**
     * Parses the JSON response from the InputStream and returns a list of Recipe objects.
     *
     * @param inputStream the InputStream containing the JSON response
     * @return a list of parsed Recipe objects
     * @throws PersistException if an error occurs during parsing or if a timeout occurs during the request
     */
    public List<Recipe> parseRecipeList(InputStream inputStream) throws PersistException {
        List<Recipe> result = new ArrayList<>();

        try {
            // Initializes a BufferedReader object to read the InputStream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Parses the JSON response
            JsonElement responseElement = JsonParser.parseReader(bufferedReader);

            JsonArray recipesJson;

            if (responseElement.isJsonObject()) {
                // Extracts the "data" object
                JsonObject responseJson = responseElement.getAsJsonObject();
                JsonElement dataJson = responseJson.get("data");

                // Extracts the "recipes" array from the "data" object
                if (dataJson.isJsonObject() && dataJson.getAsJsonObject().has("recipes")) {
                    recipesJson = dataJson.getAsJsonObject().get("recipes").getAsJsonArray();
                } else if (dataJson.isJsonArray()) {
                    recipesJson = dataJson.getAsJsonArray();
                } else {
                    throw new IllegalStateException("Unexpected JSON type for 'data': " + dataJson);
                }
            } else {
                throw new IllegalStateException("Unexpected JSON type: " + responseElement);
            }

            // Parses each recipe object in the "recipes" array and adds it to the result list
            for (JsonElement recipeJson : recipesJson) {
                // Extracts the recipe data from the JSON object
                JsonObject recipeObject = recipeJson.getAsJsonObject();
                int id = recipeObject.get("id").getAsInt();
                int user_id = recipeObject.get("id_user").getAsInt();
                String name = recipeObject.get("name").getAsString();
                String description = recipeObject.get("description").getAsString();
                String path_img = "";
                if (recipeObject.has("path_img")) {
                    path_img = recipeObject.get("path_img").getAsString();
                }
                double rating = recipeObject.get("rating").getAsDouble();
                int likes = recipeObject.get("likes").getAsInt();
                String username = "";
                if (recipeObject.has("user_name")) {
                    username = recipeObject.get("user_name").getAsString();
                }

                // Check if "liked" key exists in the JSON object
                boolean liked = recipeObject.has("liked") && recipeObject.get("liked").getAsBoolean();
                // Check if "saved" key exists in the JSON object
                boolean saved = recipeObject.has("saved") && recipeObject.get("saved").getAsBoolean();

                // Creates a new Recipe object with the extracted data
                Recipe recipe = new Recipe(id, user_id, name, description, path_img, rating, likes, username);

                // Set liked and saved properties of the recipe
                recipe.setLiked(liked);
                recipe.setSaved(saved);

                // Adds the new Recipe object to the list of recipes
                result.add(recipe);
            }


            // Closes the BufferedReader
            bufferedReader.close();

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            throw new PersistException(OpResult.DB_NORESPONSE.getCode());
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
     * Sends a POST request to the specified path to add a new recipe.
     *
     * @param path   the URL path to send the request to
     * @param token  the authentication token for the request
     * @param recipe the Recipe object to add
     * @param file   the image file associated with the recipe (can be null)
     * @return the result of the request
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

                System.out.println("Ingredients [" + i + "]: " + ingredients.get(i));
            }


            // Send steps as JSON and images
            List<Step> steps = recipe.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);

                // Send step as JSON
                wr.writeBytes("--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"steps[" + (i + 1) + "]\"\r\n\r\n" + gson.toJson(step) + "\r\n");

                // Send step image
                File stepImage = step.getFile();
                if (stepImage != null) {
                    wr.writeBytes("--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"step_file_" + (i + 1) + "\"; filename=\"" + stepImage.getName() + "\"\r\n");
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
            Log.e("readResponse error", "Error during HTTPS request: " + e.toString());

            result = new DataResult();
            result.setResult("0001");
            result.setData("Error during HTTPS request");
        }
        return result;
    }

    /**
     * @param path
     * @param param
     * @param cat
     * @return
     */
    public DataResult readCategoryResponse(String path, String param, List<Category> cat) {
        DataResult result = null;

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

            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);

            // Write parameters to the request
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(param.getBytes(StandardCharsets.UTF_8));
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
            }

        } catch (SocketTimeoutException e) {
            // timeout Exception
            //Log.e("readResponse error", "Request timed out: " + e.toString());
            result = new DataResult();
            result.setResult("0002");
            result.setData("Request timed out");
        } catch (Exception e) {
            Log.e("readResponse error", "Error during HTTPS request: " + e.toString());

            result = new DataResult();
            result.setResult("0001");
            result.setData("Error during HTTPS request");
        }
        return result;
    }

}




