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
import android.view.View;
import android.widget.TableRow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.controller.Add.AddRecipeActivity;
import com.example.cookim.controller.Home.HomeActivity;
import com.example.cookim.databinding.ActivityCommentBinding;
import com.example.cookim.databinding.ItemCommentBinding;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        model = new Model();
        controller = new Controller();
        executor = Executors.newSingleThreadExecutor();
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        int recipeId = intent.getIntExtra("recipe_id", -1);
        token = model.readToken(getApplicationContext());
        setupEditTextListener();

        binding.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHomePage();
            }
        });

        binding.btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createComment(recipeId);
            }
        });


        loadComments(recipeId);


    }

    /**
     * check the params to create a new coment and send petition
     */
    private void createComment(int id) {

        if (!binding.etComment.getText().toString().isEmpty() && binding.etComment.getText().toString().length() < 280) {

            Comment comment = new Comment(id, binding.etComment.getText().toString());

            if (comment != null) {
                try {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            result = model.createNewComment(token, comment);

                        }
                    });

                    if (result.getResult().equals("1")) {
                        System.out.println("new comment successfully created");



                    } else {
                        controller.displayErrorMessage(getApplicationContext(),"El comentario no ha podido ser subido");

                    }


                } catch (Exception e) {
                    System.out.println(e.toString());
                }


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);

                builder.setTitle("ERROR")
                        .setMessage("El comentario no ha podido crearse")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
            loadPage(id);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);

            builder.setTitle("ERROR")
                    .setMessage("El comentario no ha puede estar vacio ni superar los 280 caracteres")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }

    /**
     * reload the page of comments
     *
     * @param id
     */
    private void loadPage(int id) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("recipe_id", id);
        startActivity(intent);
    }

    /**
     * Method to control an inform the user about te length of his comment
     * if text becomes red, it means this text is too long
     * else, the text remains black
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
     * Loads all comments to the view
     *
     * @param id
     */
    private void loadComments(long id) {

        try {

            executor.execute(new Runnable() {

                @Override
                public void run() {
                    comments = model.getCommentsOfRecipe(token, id);

                    if (comments != null) {
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

                                    TableRow.LayoutParams params = new TableRow.LayoutParams();
                                    params.setMargins(100, 0, 0, 0); // Replace -50 with the number of pixels you want to move to the left
                                    row.setLayoutParams(params);

                                    row.addView(commentBinding.getRoot());

                                    binding.tlComments.addView(row);
                                }
                            });

                        }


                    }
                }
            });

        } catch (Exception e) {
        }


    }

    /**
     * Displays the home page of the app and senda the user object to next activity
     */
    private void showHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
