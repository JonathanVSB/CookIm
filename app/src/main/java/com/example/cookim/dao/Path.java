package com.example.cookim.dao;

import android.content.Context;

import com.example.cookim.model.Model;

import java.io.FileInputStream;

public class Path {

    Context cont;


    public final String PATH = "https://91.107.198.64:443/Cookim/" ;



    public final String RELATIVEPATH = "http://91.107.198.64";
    public final String  STEPS = PATH + "steps";

    public final String  HELLO = "192.168.127.102/hello";

    public final String  LOGIN = PATH + "login";
    public final String  OTHERPROFILES = PATH + "show-user-profile";
    public final String  ADDRECIPE = PATH + "add-recipe";
    public final String  INGREDIENTS = PATH + "ingredient-list";
    public final String  LOGOUT = PATH + "logout";
    public final String  MYPROFILE = PATH + "show-user-home-data";
    public final String  HOMEPAGE = PATH + "home-page";
    public final String  LIKE = PATH + "like";
    public final String  FOLLOW = PATH + "user-follow";
    public final String  SAVE = PATH + "favorite-recipes";
    public final String  SIGN = PATH + "sign-in";
    public final String  AUTOLOGIN = PATH + "autologin";
    public final String  COMMENTS = PATH + "get-recipe-parent-comment";
    public final String  NEWCOMMENT = PATH + "recipe-comment";
    public final String  SEARCH = PATH + "search-recipes";
    public final String  FAVORITES = PATH + "get-favorite-recipes";
    public final String  REMOVERECIPE = PATH + "remove-recipe";
    public final String  EDITDATA = PATH + "my-profile/modify-account";
    public final String  CHANGEPASS = PATH + "my-profile/modify-password";


}
