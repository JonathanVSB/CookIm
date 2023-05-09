package com.example.cookim.dao;

public class Path {

    final static String PATH ="https://91.107.198.64:443/Cookim/";

    public final String RELATIVEPATH= "http://91.107.198.64";
    public final String STEPS= PATH+ "steps";

    public final String HELLO= "192.168.127.102/hello";

    //Mia 192.168.81.1:7070
    //Samuel 192.168.127.102
    //443
    //Server 91.107.198.64:7070
    public final String LOGIN =  PATH + "login";
    public final String OTHERPROFILES = PATH + "show-user-profile";
    public final String ADDRECIPE = PATH +"add-recipe";
    public final String INGREDIENTS = PATH + "ingredient-list";
    public final String LOGOUT = PATH + "logout";
    public final String MYPROFILE = PATH + "show-user-home-data";
    public final String HOMEPAGE = PATH + "home-page";
    public final String LIKE = PATH + "like";
    public final String SAVE = PATH + "save";
    public final String SIGN = PATH + "sign-in";
    public final String AUTOLOGIN = PATH + "autologin";



}
