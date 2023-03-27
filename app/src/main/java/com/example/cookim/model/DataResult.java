package com.example.cookim.model;

import java.io.Serializable;

public class DataResult implements Serializable {

    String result;
    String result2;
    Object data;


    //Constructor

    public DataResult(String result, String result2) {
        this.result = result;
        this.result2 = result2;
    }

    public DataResult(String result, String result2, Object data) {
        this.result = result;
        this.result2 = result2;
        this.data = data;
    }

    //GETTERS AND SETTERS


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult2() {
        return result2;
    }

    public void setResult2(String result2) {
        this.result2 = result2;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
