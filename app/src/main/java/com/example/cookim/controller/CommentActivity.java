package com.example.cookim.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityCommentBinding;
import com.example.cookim.databinding.ItemCommentBinding;
import com.example.cookim.exceptions.PersistException;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Comment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommentActivity extends Activity {


    Model model;
    Controller controller;
    List<Comment> comments;
    ActivityCommentBinding binding;
    Handler handler;
    ExecutorService executor;
    String token;
    List<Comment> commnets;
    DataResult result;

    /**
     *Method called when the comment activity is created.
     *  Performs the following tasks:
     *  Sets up the activity layout.
     *  Retrieves the authentication token.
     *  Sets up the listener for the input text field.
     *  Displays existing comments.
     *  Sets up the listeners for the "Cancel" and "Send" buttons.
     *  If there is no authentication token, redirects to the login page.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        model = new Model(this);
        controller = new Controller();
        executor = Executors.newSingleThreadExecutor();
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        int recipeId = intent.getIntExtra("recipe_id", -1);
        token = model.readFile(getApplicationContext(), "token");




        setupEditTextListener();
        binding.message.setVisibility(View.VISIBLE);

        if (token != null) {
            binding.ivcancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    controller.displayActivity(getApplicationContext(), HomeActivity.class);
                }
            });

            binding.btSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createComment(recipeId);
                }
            });


            loadComments(recipeId);
//            setupEmojiCompat();

        } else {
            controller.displayLogInPage(this, LoginActivity.class);
        }


    }

    /**
     * Create a comment with the ID of the given line.
     * @param id The ID of the comment.
     */
    private void createComment(int id) {

        if (!binding.etComment.getText().toString().isEmpty() && binding.etComment.getText().toString().length() < 280) {

            Comment comment = new Comment(id, binding.etComment.getText().toString());

            if (comment != null) {
                try {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            result = model.createNewComment(token, comment, getApplicationContext());

                        }
                    });

                    if (result.getResult().equals("1")) {
                        System.out.println("new comment successfully created");
                        controller.displayActivity(getApplicationContext(), NoConnectionActivity.class);
                        finish();

                    } else if (result.getResult().equals("0000")) {
                        controller.displayActivity(getApplicationContext(), NoConnectionActivity.class);
                        finish();
                    } else {
                        controller.displayErrorMessage(CommentActivity.this, "El comentario no ha podido ser subido");

                    }


                } catch (Exception e) {
                    System.out.println(e.toString());
                }


            } else {

                controller.displayErrorMessage(CommentActivity.this, "El comentario no ha podido crearse");

            }
            loadPage(id);
        } else {

            controller.displayErrorMessage(CommentActivity.this, "El comentario no ha puede estar vacio ni superar los 280 caracteres");


        }

    }

    /**
     * Loads the CommentActivity with the specified ID.
     * @param id The ID of the recipe to load.
     */
    private void loadPage(int id) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("recipe_id", id);
        startActivity(intent);
    }

    /**
     * Sets up a text change listener for the EditText.
     * It changes the text color of the EditText based on the length of the text.
     */
    private void setupEditTextListener() {
        binding.etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No es necesario implementar este método
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 280) {
                    // El texto supera los 280 caracteres
                    binding.etComment.setTextColor(Color.RED); // Cambiar color del texto a rojo
                } else {
                    // El texto tiene 280 caracteres o menos
                    binding.etComment.setTextColor(Color.BLACK); // Restaurar el color del texto a negro
                }
            }
        });
    }


    /**
     * Loads the comments for a specific recipe ID.
     * @param id The ID of the recipe to load the comments for.
     */
    private void loadComments(long id) {

        try {

            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        comments = model.getCommentsOfRecipe(token, id, getApplicationContext());

                        if (comments != null) {

                            if (comments.size() != 0) {

                                //hide the message to new comments
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.message.setVisibility(View.GONE);
                                    }
                                });

                                System.out.println("la lista de comentarios esta llena");

                                for (int i = 0; i < comments.size(); i++) {

                                    ItemCommentBinding commentBinding = ItemCommentBinding.inflate(getLayoutInflater());
                                    int position = i;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            commentBinding.tvUsername.setText(comments.get(position).getUsername());
                                            commentBinding.tvComment.setText(comments.get(position).getText());
                                            if (comments.get(position).getPath() != null) {
                                                String img = model.downloadImg(comments.get(position).getPath());
                                                Glide.with(CommentActivity.this)
                                                        .load(img)
                                                        .listener(new RequestListener<Drawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                                // Handle image loading failure here
                                                                commentBinding.userimg.setImageResource(R.drawable.tostadas_de_pollo_con_lechuga);
                                                                return false;
                                                            }

                                                            @Override
                                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                // The image has been loaded successfully
                                                                return false;
                                                            }
                                                        })
                                                        .into(commentBinding.userimg);

                                            }
                                            TableRow row = new TableRow(CommentActivity.this);
                                            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                                                    TableRow.LayoutParams.MATCH_PARENT,
                                                    TableRow.LayoutParams.WRAP_CONTENT
                                            );
                                            rowParams.setMargins(0, 0, 0, 20);
                                            rowParams.gravity = Gravity.CENTER;
                                            row.setLayoutParams(rowParams);

                                            row.addView(commentBinding.getRoot());

                                            binding.tlComments.addView(row);
                                        }
                                    });
                                }
                            } else {
                                //controller.displayLogInPage(getApplicationContext(), LoginActivity.class);
                            }
                        } else {
                            controller.displayLogInPage(getApplicationContext(), LoginActivity.class);
                        }

                    } catch (PersistException e) {
                        controller.displayErrorView(getApplicationContext(), e.getCode());
                    }

                }
            });

        } catch (Exception e) {

        }


    }

    /**
     *
     */
//    private void setupEmojiCompat() {
//
//        // Crea una solicitud de fuente usando Google Play Services como proveedor
//        FontRequest fontRequest = new FontRequest(
//                "com.google.android.gms.fonts", // Google Play Services font provider authority
//                "com.google.android.gms", // Google Play Services font provider package
//                "Noto Color Emoji Compat", // Query String
//                R.array.com_google_android_gms_fonts_certs); // Certificates set
//
//        // Crea la configuración con la solicitud de fuente
//        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(getApplicationContext(), fontRequest)
//                .setReplaceAll(true)
//                .setEmojiSpanIndicatorEnabled(true)
//                .setEmojiSpanIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.emoji_span_indicator_color));
//
//        EmojiCompat.init(config);
//    }
}