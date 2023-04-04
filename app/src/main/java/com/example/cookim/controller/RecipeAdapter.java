package com.example.cookim.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookim.R;
import com.example.cookim.databinding.ItemRecipeContentBinding;
import com.example.cookim.model.recipe.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Recipe recipe;
    private ItemRecipeContentBinding binding;
    private View.OnClickListener listener;
    private boolean press;

    public RecipeAdapter(Recipe recipe) {
        this.recipe = recipe;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemRecipeContentBinding.inflate(inflater, parent, false);
        return new RecipeViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.tvNameRecipe.setText(recipe.getName());
        //holder.ivRecipeImage.setImageResource(recipe.getPath());
        holder.tvLikes.setText(String.valueOf(recipe.getLikes()));

        binding.btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageActions();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameRecipe;
        private ImageView ivRecipeImage;
        private TextView tvLikes;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNameRecipe = itemView.findViewById(R.id.name_recipe);
            ivRecipeImage = itemView.findViewById(R.id.img01);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }
    }

    private void pageActions() {
        if (!press) {
            binding.btLike.setImageResource(R.drawable.selectedheart);
            int likes = Integer.parseInt(binding.tvLikes.getText().toString());
            likes++;
            binding.tvLikes.setText(Integer.toString(likes));
            press = true;
        } else {
            binding.btLike.setImageResource(R.drawable.nonselectedheart);
            int likes = Integer.parseInt(binding.tvLikes.getText().toString());
            likes--;
            binding.tvLikes.setText(Integer.toString(likes));
            press = false;
        }
    }
}
