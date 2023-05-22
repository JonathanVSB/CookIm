package com.example.cookim.exceptions;

public class PersistException extends Exception {

    private int code;

    PersistException(String message, int code) {
        super(message);
        this.code = code;
        this.toString();
    }

    public PersistException(int code) {
        this.code = code;
    }




    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersistException{");
        sb.append("message=").append(getMessage());
        sb.append("code=").append(code);
        sb.append('}');
        return sb.toString();
    }



}
