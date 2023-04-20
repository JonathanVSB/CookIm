package com.example.cookim.dao;

import android.widget.Toast;

import com.example.cookim.model.DataResult;
import com.example.cookim.model.recipe.Recipe;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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
        int i = 0;
        try {
            //HTTP request
            System.out.println("ENTRA  " + urlString);
            java.net.URL url = new URL(urlString);
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


}
