package com.example.cookim.controller;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookim.R;
import com.example.cookim.databinding.ItemRecipeContentBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;
import com.example.cookim.model.user.LoginModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private ItemRecipeContentBinding binding;
    private View.OnClickListener listener;
    private boolean press;
    ExecutorService executor;
    Handler handler;
    Model model;
    Context context1;

    private final String URL = "http://91.107.198.64:7070/Cookim/";
    private final String URL2 = "http://192.168.127.102:7070/Cookim/";

    private final String URL3 = "http://192.168.127.94:7070/Cookim/";


    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        context1 = context;
        this.recipeList = recipeList;
        model = new Model();
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
//        this.press = false;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRecipeContentBinding binding = ItemRecipeContentBinding.inflate(inflater, parent, false);
        return new RecipeViewHolder(binding.getRoot());
    }


    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.binding.nameRecipe.setText(recipe.getName());
        holder.binding.tvLikes.setText(String.valueOf(recipe.getLikes()));
        holder.binding.tvPoster.setText(recipe.getUsername());
        holder.binding.btLike.setImageResource(recipe.isLiked() ? R.drawable.selectedheart : R.drawable.nonselectedheart);

        holder.binding.btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean press = !recipe.isLiked();
                recipe.setLiked(press);
                recipe.setLikes(press ? recipe.getLikes() + 1 : recipe.getLikes() - 1);
                holder.binding.tvLikes.setText(String.valueOf(recipe.getLikes()));

                //send 1 if user likes the recipe, 0 if unlikes
                int likeValue = press ? 1 : 0;
                DataResult result = sendLike(likeValue, String.valueOf(recipe.getId()));

                if (result.getResult().equals("1")) {
                    try {
                        holder.binding.tvLikes.setText(String.valueOf(recipe.getLikes()));
                        holder.binding.btLike.setImageResource(recipe.isLiked() ? R.drawable.selectedheart : R.drawable.nonselectedheart);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }else {

                    press = false;
                }
            }
        });

        holder.binding.nameRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayRecipeStepsLayout();
            }
        });

        holder.binding.img01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayRecipeStepsLayout();
            }
        });

    }


    @Override
    public int getItemCount() {
        return recipeList.size();
    }


    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private ItemRecipeContentBinding binding;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecipeContentBinding.bind(itemView);
        }
    }


    /**
     * Displays the view of the recipe and the steps necessaries to cook it
     */
    private void displayRecipeStepsLayout(/*int id/*Context context*/) {
        Intent intent = new Intent(context1, RecipeStepsActivity.class);
        context1.startActivity(intent);

    }

    private DataResult sendLike(int num, String id) {
        String numero = String.valueOf(num);
        String parametros = "num=" + numero + "&recipe_id=" + id;
        DataResult result = null;

        try {
            result = executor.submit(() -> {
                return model.likeRecipe(parametros);
            }).get();
        } catch (Exception e) {
            System.out.println("Error al enviar like: " + e.getMessage());
        }

        return result;
    }



    }

