package com.example.cookim.model;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

public class Petitions {

    private final OkHttpClient client;
    private Gson gson;


    public Petitions() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }
}
