package com.example.cookim.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Petitions {

    private final OkHttpClient client;
    private Gson gson;


    public Petitions() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public void makeRequest(String url, final PetitionsCallback callback){

        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Petitions", "Request failed", e);
                callback.onError(e);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("Petitions", "Request failed with code: " + response.code());
                    callback.onError(new IOException("Unexpected code " + response));
                } else {
                    String json = response.body().string();
                    Object obj = gson.fromJson(json, Object.class);
                    callback.onSuccess(obj);
                }
            }
        });
    }

    public interface PetitionsCallback {
        void onSuccess(Object response);
        void onError(Exception e);
    }
}
