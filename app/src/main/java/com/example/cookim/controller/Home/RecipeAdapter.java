package com.example.cookim.controller.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.cookim.R;
import com.example.cookim.controller.RecipeStepsActivity;
import com.example.cookim.databinding.ItemRecipeContentBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;

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


    HomeListener homeListener;

    public RecipeAdapter(List<Recipe> recipeList) {

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

        if (recipe.getPath_img() != null && !recipe.getPath_img().isEmpty()) {
            //Load img with Glide
            String img = model.downloadImg(recipe.getPath_img());
            int width = 500; // Ancho deseado en píxeles
            int height = 216;

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(width, height) // Establece las dimensiones deseadas
                    .transform(new FitCenter());

            Glide.with(holder.itemView.getContext())
                    .load(img)
                    .apply(requestOptions)
                    .into(holder.binding.img01);
        } else {
            holder.binding.img01.setImageResource(R.drawable.tostadas_de_pollo_con_lechuga);
        }



//
//            Glide.with(holder.itemView.getContext())
//                    .load(img)
//


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
                } else {

                    press = false;
                }
            }
        });

        holder.binding.viewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeListener.onItemClicked(recipe.getId(), 1);
            }
        });

        holder.binding.tvPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeListener.onItemClicked(recipe.getUser_id(), 2);
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


    public void setHomeListener(HomeListener listener) {
        this.homeListener = listener;
    }


}

