package com.example.cookim.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cookim.R;
import com.example.cookim.databinding.ActivityHomeBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomePage extends Activity {

    private ActivityHomeBinding binding;
    List<Recipe> recipes;
    private final String URL = "http://91.107.198.64:7070/Cookim/";
    private final String URL2 = "http://192.168.127.102:7070/Cookim/";
    private final String URL3 = "http://192.168.127.94:7070/Cookim/";

    Executor executor = Executors.newSingleThreadExecutor();
    Handler handler;

    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        handler = new Handler(Looper.getMainLooper());


        recipes = new ArrayList<>();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String token = readToken();

        loadHomePage(token);

    }

    private void loadHomePage(String a) {
        String data1 = "my-profile";
        String data2 = "home-page";
        String url = URL3;
        String token = "token=" + a;

        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    user = readUserResponse((url + data1), token);
                    List<Recipe> recipes = loadRecipes();
                    List<RecipeAdapter> adapters = new ArrayList<>();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            displayRecipes(recipes);
                        }
                    });

                    if (user != null) {
                        binding.tvUsername.setText(user.getUsername());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Post Execute
                                if (user != null) {
//                                    executor.execute(() -> {
//                                        try {
//                                            File proFile = new File(getFilesDir(), "user3.jpg");
//                                            if (!proFile.exists()) {
//                                                URL beeUrl = new URL("http://91.107.198.64:7070" + result.getResult2());
//                                                Bitmap beeBitmap = BitmapFactory.decodeStream(beeUrl.openStream());
//                                                FileOutputStream beeOut = new FileOutputStream(proFile);
//                                                beeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, beeOut);
//                                                beeOut.flush();
//                                                beeOut.close();
//                                            }
//                                            runOnUiThread(() -> binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(proFile.getAbsolutePath())));
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            System.out.println("PETA EN ESTA LINEA: " + e.toString());
//                                            binding.profileImage.setImageResource(R.drawable.guest_profile);
//                                        }
//                                    });

//                                        String profileUrl = "http://91.107.198.64:7070" + result.getResult2();
//                                        Glide.with(HomePage.this)
//                                                .load(profileUrl)
//                                                .into(binding.profileImage);
                                } else {
                                    binding.profileImage.setImageResource(R.drawable.guest_profile);
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("ERROR" + e.toString());
        }

    }

    /**
     * Gives the list of recipes to the RecipeAdapter
     * @param recipes
     */
    private void displayRecipes(List<Recipe> recipes) {
        RecipeAdapter adapter = new RecipeAdapter(recipes);
        binding.recommendationsRv.setAdapter(adapter);
        binding.recommendationsRv.setLayoutManager(new LinearLayoutManager(HomePage.this));
    }


    /**
     * Do petition
     *
     * @param urlString
     * @param token
     * @return
     */

    private DataResult readResponse(String urlString, String token) {
        DataResult result = null;
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
                result = parseDataResult(inputStream);

                inputStream.close();
            }

        } catch (Exception e) {
            // Toast.makeText(this, "Error connecting server", Toast.LENGTH_LONG).show();
            System.out.println("PETA EN ESTA LINEA: " + e.toString());
        }

        return result;
    }

    private DataResult parseDataResult(InputStream inputStream) {
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

            // Removes the quotes around the braces
            String jsonString = stringBuilder.toString().replaceAll("\"\\{", "{").replaceAll("\\}\"", "}");


            // Debugging statement
            System.out.println("Respuesta JSON modificada: " + jsonString);

            // Checks if the modified string starts and ends with "{" and "}"
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
        } catch (JsonSyntaxException e) {
            // Debugging statement
            System.out.println("Error al analizar la respuesta JSON: " + e.toString());
        }

        // Returns the UserModel object
        return result;
    }

    private UserModel readUserResponse(String urlString, String token) {
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


    private UserModel parseUserModel(InputStream inputStream) {
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
     * Creates HTTP petition, to get the data of all recipes
     */
    private List<Recipe> loadRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String petition = URL3 + "home-page";
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
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

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
    private String getReponseBody(HttpURLConnection con) throws IOException {
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
     * Read an internal file read the token stored there
     *
     * @return the token or null
     */
    private String readToken() {
        // Gets an instance of the application context
        Context context = getApplicationContext();

        // Open the file in write mode and create the FileOutputStream object
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput("token.txt");

            //Reads the token data from file
            StringBuilder stringBuilder = new StringBuilder();
            int c;
            while ((c = inputStream.read()) != -1) {
                stringBuilder.append((char) c);
            }
            String token = stringBuilder.toString();

            //Close the FileInputStream Object
            inputStream.close();

            // returns token
            return token;
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error al leer la respuesta: " + e.toString());
        }

        // if file is empty, returns null
        return null;
    }
}