package com.example.cookim.dao;

import android.content.Context;

import com.example.cookim.model.Model;

import java.io.FileInputStream;

public class Path {

    Context cont;


    String PATH;

    public final String RELATIVEPATH;
    public final String STEPS;

    public final String HELLO;

    public final String LOGIN;
    public final String OTHERPROFILES;
    public final String ADDRECIPE;
    public final String INGREDIENTS;
    public final String LOGOUT;
    public final String MYPROFILE;
    public final String HOMEPAGE;
    public final String LIKE;
    public final String FOLLOW;
    public final String SAVE;
    public final String SIGN;
    public final String AUTOLOGIN;
    public final String COMMENTS;
    public final String NEWCOMMENT;
    public final String SEARCH;
    public final String FAVORITES;
    public final String REMOVERECIPE;
    public final String EDITDATA;
    public final String CHANGEPASS;

    public Path(Context context) {
        cont = context;
        PATH = readFile(cont,"Path");/*"https://91.107.198.64:443/Cookim/"*/;

        //Mia 192.168.81.1:7070
        //Samuel 192.168.127.102
        //443
        //Server 91.107.198.64:7070
        RELATIVEPATH = "http://91.107.198.64";
        STEPS = PATH + "steps";

        HELLO = "192.168.127.102/hello";

        LOGIN = PATH + "login";
        OTHERPROFILES = PATH + "show-user-profile";
        ADDRECIPE = PATH + "add-recipe";
        INGREDIENTS = PATH + "ingredient-list";
        LOGOUT = PATH + "logout";
        MYPROFILE = PATH + "show-user-home-data";
        HOMEPAGE = PATH + "home-page";
        LIKE = PATH + "like";
        FOLLOW = PATH + "user-follow";
        SAVE = PATH + "favorite-recipes";
        SIGN = PATH + "sign-in";
        AUTOLOGIN = PATH + "autologin";
        COMMENTS = PATH + "get-recipe-parent-comment";
        NEWCOMMENT = PATH + "recipe-comment";
        SEARCH = PATH + "search-recipes";
        FAVORITES = PATH + "get-favorite-recipes";
        REMOVERECIPE = PATH + "remove-recipe";
        EDITDATA = PATH + "my-profile/modify-account";
        CHANGEPASS = PATH + "my-profile/modify-password";
    }


    /**
     * Read an internal file and retrieve the token stored there
     *
     * @return the data of file or null
     */
    public static String readFile(Context cont, String filename) {
        // Gets an instance of the application context
        Context context = cont;

        // Open the file in write mode and create the FileOutputStream object
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput(filename+".txt");


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
