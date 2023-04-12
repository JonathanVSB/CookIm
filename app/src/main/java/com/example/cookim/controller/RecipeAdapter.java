package com.example.cookim.controller;

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
import com.example.cookim.model.recipe.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private ItemRecipeContentBinding binding;
    private View.OnClickListener listener;
    private boolean press;


    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
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
                holder.binding.btLike.setImageResource(press ? R.drawable.selectedheart : R.drawable.nonselectedheart);
            }
        });

        holder.binding.viewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageActions(v);
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


    private void pageActions(View v) {

        if (v.getId() == binding.btLike.getId()) {
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

        } else if (v.getId() == binding.viewRecipe.getId()) {
            //displayRecipeStepsLayout(getAdapterPosition(), itemView.getContext());

        }
    }

    /**
     * Displays the view of the recipe and the steps necessaries to cook it
     */
    private void displayRecipeStepsLayout(int id/*Context context*/) {
       /* Intent intent = new Intent(itemView.getContext(), RecipeStepsActivity.class);
        intent.putExtra("recipe_id", id);
        itemView.getContext().startActivity(intent);*/
    }


}
