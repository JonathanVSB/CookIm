package com.example.cookim.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.os.Parcelable;

import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.UserModel;

import java.io.Serializable;
import java.util.logging.Handler;

public class Controller extends Activity {

    Model model;


    public Controller() {

    }

    /**
     * Displays and error pop-up message
     * @param context
     * @param mensaje
     */
    public void displayErrorMessage(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Seguro que quieres continuar?")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Displays and error pop-up message to confirm the process the user selected
     * @param context
     * @param mensaje
     */
    public void displayConfirmEnd(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ERROR")
                .setMessage(mensaje)

                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayActivity(context, HomeActivity.class);

                        // Finalizar el Activity actual
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Display the add recipe page
     *
     * @param context The context from which the method is called
     * @param Class   The class of the AddRecipeActivity
     */
    public void displayActivity(Context context, Class<?> Class) {
        Intent intent = new Intent(context, Class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void displayActivityWithId(Context context, Class<?> Class, long id) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("MyUserID", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Display the login page
     *
     * @param context The context from which the method is called
     * @param Class   The class of the LoginActivity
     */
    public void displayLogInPage(Context context, Class<?> Class) {
        Intent intent = new Intent(context, Class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Display my profile Page
     *
     * @param context
     * @param Class
     * @param id
     * @param id2
     */
    public void displayMyProfile(Context context, Class<?> Class, long id, int id2) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("MyUserID", id);
        intent.putExtra("userID", id2);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Display comment activity
     *
     * @param context
     * @param Class
     * @param id
     */
    public void displayCommentPage(Context context, Class<?> Class, int id) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("recipe_id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * display Recipe Steps page
     *
     * @param context
     * @param Class
     * @param id
     */
    public void displayRecipeDetails(Context context, Class<?> Class, int id, long myId) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("recipe_id", id);
        intent.putExtra("MyUserID", myId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * display Edit profile Page
     *
     * @param context
     * @param Class
     * @param user
     */
    public void displayEditProfile(Context context, Class<?> Class, UserModel user) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("id", user.getId());
        intent.putExtra("username", user.getUsername());
        intent.putExtra("fullname", user.getFull_name());
        intent.putExtra("phone", user.getPhone());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("path_img", user.getPath_img());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param Class
     * @param recipeJson
     */
    public void displayCategoryActivity(Context context, Class<?> Class, String recipeJson) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("recipeJson", recipeJson);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void displayErrorView(Context context, int id) {
        Intent intent = null;
        intent = new Intent(context, NoConnectionActivity.class);
        intent.putExtra("ErrorCode", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);


    }
}
