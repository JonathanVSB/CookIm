package com.example.cookim.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.cookim.model.Model;

public class Controller extends Activity {

    Model model;

    public Controller() {

    }

    public static void displayErrorMessage(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ERROR")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acciones a realizar al hacer clic en "Aceptar"
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Display the add recipe page
     * @param context The context from which the method is called
     * @param Class The class of the AddRecipeActivity
     */
    public void displayActivity(Context context, Class<?> Class) {
        Intent intent = new Intent(context, Class);
        context.startActivity(intent);
    }

    /**
     * Display the login page
     * @param context The context from which the method is called
     * @param Class The class of the LoginActivity
     */
    public void displayLogInPage(Context context, Class<?> Class) {
        Intent intent = new Intent(context, Class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Display my profile Page
     * @param context
     * @param Class
     * @param id
     * @param id2
     */
    public void displayMyProfile(Context context, Class<?> Class, long id, int id2) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("MyUserID", id);
        intent.putExtra("userID", id2);
        context.startActivity(intent);
    }

    /**
     * Display comment activity
     * @param context
     * @param Class
     * @param id
     */
    public void displayCommentPage(Context context, Class<?> Class,int id) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("recipe_id", id);
        context.startActivity(intent);
    }


}
