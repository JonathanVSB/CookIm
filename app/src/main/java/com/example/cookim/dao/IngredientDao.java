package com.example.cookim.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cookim.model.recipe.Ingredient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class IngredientDao {

    public List<Ingredient> getAll(String path, String token, int id) {
        List<Ingredient> list = new ArrayList<>();
        String param = token + ":" + String.valueOf(id);

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

                list = parseIngredientList(inputStream);

                inputStream.close();

            }

        } catch (Exception e) {
            System.out.println("PETA EN ESTA LINEA: " + e.toString());
        }

        return list;

    }

    public List<Ingredient> parseIngredientList(InputStream inputStream) {
        List<Ingredient> result = new ArrayList<>();

        try {
            // Initializes a BufferedReader object to read the InputStream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Parses the JSON response and extracts the "data" object
            JsonObject responseJson = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            JsonArray dataJson = responseJson.get("data").getAsJsonArray();

            // Parses each ingredient object in the "data" array and adds it to the result list
            for (JsonElement ingredientJson : dataJson) {
                // Extracts the ingredient data from the JSON object
                int id = ingredientJson.getAsJsonObject().get("id").getAsInt();

                String name = ingredientJson.getAsJsonObject().get("name").getAsString();

                // Creates a new Ingredient object with the extracted data
                Ingredient ingredient = new Ingredient(id, name);

                // Adds the new Ingredient object to the list of ingredients
                result.add(ingredient);
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
     * Search the max id number of ingredients in local database
     *
     * @param ingredients
     * @return
     */
    public int getMaxIngredientId(BBDDIngredients ingredients) {
        int maxId = -1;

        try {
            SQLiteDatabase db = ingredients.getReadableDatabase();
            String query = "SELECT MAX(PK_Id) FROM Ingrediente";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                maxId = cursor.getInt(0);
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return maxId;
    }


}
