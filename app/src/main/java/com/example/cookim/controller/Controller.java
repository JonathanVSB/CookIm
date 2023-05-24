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
     * Displays an error message in the form of an AlertDialog.
     * @param context The context in which the error message is displayed.
     * @param mensaje The error message to be shown.
     */
    public void displayErrorMessage(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ERROR")
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
     * Displays a confirmation dialog with an "Aceptar" (Accept) and "Cancelar" (Cancel) button options.
     * If "Aceptar" is clicked, it navigates to the HomeActivity and finishes the current activity.
     * If "Cancelar" is clicked, the dialog is closed.
     * @param context The context in which the confirmation dialog is displayed.
     * @param mensaje The message to be shown in the confirmation dialog.
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

    /**
     * Displays an activity with a specific ID.
     * @param context The context from which the activity is launched.
     * @param Class The class of the activity to display.
     * @param id The ID to pass to the activity.
     */
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
     *Displays the user's profile with specified IDs.
     * @param context The context from which the profile activity is launched.
     * @param Class The class of the profile activity to display.
     * @param id The ID of the user's profile.
     * @param id2 Additional ID parameter for the profile activity.
     */
    public void displayMyProfile(Context context, Class<?> Class, long id, int id2) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("MyUserID", id);
        intent.putExtra("userID", id2);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Displays the comment page for a specific recipe.
     * @param context The context from which the comment page activity is launched.
     * @param Class The class of the comment page activity to display.
     * @param id The ID of the recipe for which the comment page is displayed.
     */
    public void displayCommentPage(Context context, Class<?> Class, int id) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("recipe_id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Displays the recipe details page with specified IDs.
     * @param context The context from which the recipe details activity is launched.
     * @param Class The class of the recipe details activity to display.
     * @param id The ID of the recipe for which the details are displayed.
     * @param myId The ID of the user viewing the recipe details.
     */
    public void displayRecipeDetails(Context context, Class<?> Class, int id, long myId) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("recipe_id", id);
        intent.putExtra("MyUserID", myId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Displays the edit profile page with user information.
     * @param context The context from which the edit profile activity is launched.
     * @param Class The class of the edit profile activity to display.
     * @param user The UserModel object containing the user information.
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
     * Displays the category activity with recipe JSON data.
     * @param context The context from which the category activity is launched.
     * @param Class The class of the category activity to display.
     * @param recipeJson The JSON data representing the recipe.
     */
    public void displayCategoryActivity(Context context, Class<?> Class, String recipeJson) {
        Intent intent = new Intent(context, Class);
        intent.putExtra("recipeJson", recipeJson);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Displays the error view with the specified error code.
     * @param context The context from which the error view activity is launched.
     * @param id The error code to be displayed.
     */
    public void displayErrorView(Context context, int id) {
        Intent intent = null;
        intent = new Intent(context, NoConnectionActivity.class);
        intent.putExtra("ErrorCode", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);


    }
}
