package com.example.cookim.exceptions;

public enum OpResult {
    DB_NOCONN(101),//No connection with database
    DB_NORESPONSE(102);//Error inserting data
   //TODO add more codes

    private final int code;

    OpResult(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}