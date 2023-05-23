package com.example.cookim.model.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Recipe {

    public int id;
    public int id_user;

    @Expose(serialize = false, deserialize = false)
    public transient File file;

    public String name;
    public String description;
    public String path_img;
    public double rating;
    public int likes;
    public String user_name;
    public String path;
    public List<Step> steps;

    public List<Category> categoryList;
    public List<Comment> comments;
    public List<Ingredient> ingredients;
//    private String base64Image;

    private boolean liked;
    private boolean saved;







    //CONSTRUCTORS
    /*public Recipe(int user_id, String name, String description, List<Step> recipe_steps, List<Comment> comments) {
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.recipe_steps = new ArrayList<>();
        this.comments = new ArrayList<>();
    }*/

    public Recipe() {
    }




    public Recipe(File file, String name, String description, List<Step> steps, List<Ingredient> ingredients) {
        this.file = file;
        this.name = name;
        this.description = description;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public Recipe(/*File file, */String name, String description) {
//        this.file = file;
        this.name = name;
        this.description = description;
        this.likes = 0;
//        this.base64Image = encodeFileToBase64(file);
    }

    public Recipe(int id, int id_user, String name, String description, String path_img, double rating, int likes, List<Ingredient> ingredients, List<Step>
            steps) {
        this.id = id;
        this.id_user = id_user;
        this.name = name;
        this.description = description;
        this.path_img = path_img;
        this.rating = rating;
        this.likes = likes;
        this.ingredients = ingredients;
        this.steps = steps;

    }

    public Recipe(int id, int id_user, String name, String description, String path_img, boolean liked, boolean saved, double rating, int likes, String user_name, String path, List<Step> steps, List<Ingredient> ingredients) {
        this.id = id;
        this.id_user = id_user;
        this.name = name;
        this.description = description;
        this.path_img = path_img;
        this.liked = liked;
        this.saved = saved;
        this.rating = rating;
        this.likes = likes;
        this.user_name = user_name;
        this.path = path;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public Recipe(int id, int id_user, String name, String description, String path_img, double rating, int likes, String user_name) {
        this.id = id;
        this.id_user = id_user;
        this.name = name;
        this.description = description;
        this.path_img = path_img;
        this.rating = rating;
        this.likes = likes;
        this.user_name = user_name;
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.comments = new ArrayList<>();

    }

    public Recipe(int id, int id_user, String name, String description, String path_img, double rating, int likes) {
        this.id = id;
        this.id_user = id_user;
        this.name = name;
        this.description = description;
        this.path_img = path_img;
        this.rating = rating;
        this.likes = likes;
        this.steps = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.ingredients = new ArrayList<>();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath_img() {
        return path_img;
    }

    public void setPath_img(String path_img) {
        this.path_img = path_img;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getUsername() {
        return user_name;
    }

    public void setUsername(String username) {
        this.user_name = username;
    }

    //NEW USAGE
    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        if (this.file == null && this.path_img != null) {
            this.file = new File(this.path_img);
        }
        return this.file;
    }

    public void setFile(File file) {
        this.path_img = file.getPath();
    }
    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

///METHODS

//    private String encodeFileToBase64(File file) {
//        String code= "";
//        try {
//            FileInputStream inputStream = new FileInputStream(file);
//            byte[] bytes = new byte[(int) file.length()];
//            inputStream.read(bytes);
//            inputStream.close();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                code = Base64.getEncoder().encodeToString(bytes);
//                return code;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return code;
//    }
}
