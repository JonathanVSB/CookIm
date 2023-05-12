package com.example.cookim.model.recipe;

import java.util.Date;

public class Comment {
    int id;
    int id_user;
    String username;
    String path;
    int id_recipe;
    String text;
    Date date;
    long id_parent;

    public Comment(String text) {
        this.id = id;
        this.id_user = id_user;
        this.id_recipe = id_recipe;
        this.text = text;
        this.date = date;
    }

    //CONSTRUCTOR


    public Comment(int id_recipe, String text) {
        this.id_recipe = id_recipe;
        this.text = text;
    }

    public Comment(int id, int id_user, String username, String path, int id_recipe, String text, long id_parent) {
        this.id = id;
        this.id_user = id_user;
        this.username = username;
        this.path = path;
        this.id_recipe = id_recipe;
        this.text = text;
        this.id_parent = id_parent;
    }

    public Comment(int id, int id_user, int id_recipe, String text, Date date) {
        this.id = id;
        this.id_user = id_user;
        this.id_recipe = id_recipe;
        this.text = text;
        this.date = date;
    }


    public Comment(int id, int id_user, int id_recipe, String text, long id_parent) {
        this.id = id;
        this.id_user = id_user;
        this.id_recipe = id_recipe;
        this.text = text;
        this.id_parent = id_parent;




    }

    //GETTERS AND SETTERS


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId_recipe() {
        return id_recipe;
    }

    public void setId_recipe(int id_recipe) {
        this.id_recipe = id_recipe;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId_parent() {
        return id_parent;
    }

    public void setId_parent(long id_parent) {
        this.id_parent = id_parent;
    }

    //TOSTRING


    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", id_user=" + id_user +
                ", username='" + username + '\'' +
                ", path='" + path + '\'' +
                ", id_recipe=" + id_recipe +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", id_parent=" + id_parent +
                '}';
    }
}
