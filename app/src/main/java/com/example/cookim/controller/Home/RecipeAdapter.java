package com.example.cookim.controller.Home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.example.cookim.R;
import com.example.cookim.controller.Controller;
import com.example.cookim.controller.MyProfileActivity;
import com.example.cookim.databinding.ItemRecipeContentBinding;
import com.example.cookim.model.DataResult;
import com.example.cookim.model.Model;
import com.example.cookim.model.recipe.Recipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private List<Long> recipesLiked;
    private ItemRecipeContentBinding binding;
    private View.OnClickListener listener;
    private boolean press;
    ExecutorService executor;
    Handler handler;
    Model model;
    Controller controller;
    String token;
    long rol;
    Context context;

    HomeListener homeListener;


    public RecipeAdapter(List<Recipe> recipeList, String token, long userRol, Context context) {

        this.recipeList = recipeList;
        this.token = token;
        this.recipesLiked = recipesLiked;
        this.rol = userRol;
        model = new Model(context);
        this.context = context;
        controller = new Controller();
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
        if (rol != 1) {
            holder.binding.ivoptions.setVisibility(View.GONE);
        }

        holder.binding.ivoptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Manejar la selección de los elementos del menú
                        switch (item.getItemId()) {
                            case R.id.itemRemove:

                                homeListener.onItemClicked(recipe.getId(), 4);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }


        });

        holder.binding.btLike.setImageResource(recipe.isLiked() ? R.drawable.selectedheart : R.drawable.nonselectedheart);
        holder.binding.btSave.setImageResource(recipe.isSaved() ? R.drawable.oven2 : R.drawable.oven);

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

        holder.binding.btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean pressLike = !recipe.isLiked();
                recipe.setLiked(pressLike);

                recipe.setLikes(pressLike ? recipe.getLikes() + 1 : recipe.getLikes() - 1);
                holder.binding.btLike.setImageResource(recipe.isLiked() ? R.drawable.selectedheart : R.drawable.nonselectedheart);
                holder.binding.tvLikes.setText(String.valueOf(recipe.getLikes()));

                //send 1 if user likes the recipe, 0 if unlikes
                int likeValue = pressLike ? 1 : 0;

                DataResult result = sendLike(likeValue, String.valueOf(recipe.getId()));
                if (result != null && result.getResult() != null && result.getResult().equals("1")) {
                    try {
                        holder.binding.tvLikes.setText(String.valueOf(recipe.getLikes()));
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else {

//                            pressLike = false;
                }
            }


        });

        holder.binding.btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeListener.onItemClicked(recipe.getId(), 3);

            }
        });

        holder.binding.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean pressSave = !recipe.isSaved();
                recipe.setSaved(pressSave);

                int saveValue = recipe.isSaved() ? 1 : 0;
                DataResult result = saveRecipe(saveValue, String.valueOf(recipe.getId()));

                if (result != null && result.getResult() != null && result.getResult().equals("1")) {
                    try {
                        holder.binding.btSave.setImageResource(recipe.isSaved() ? R.drawable.oven2 : R.drawable.oven);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else {
                    recipe.setSaved(!recipe.isSaved());
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
                homeListener.onItemClicked(recipe.getId_user(), 2);
            }
        });

    }

    private DataResult saveRecipe(int num, String id) {
        String numero = String.valueOf(num);
        String parametros = token + ":" + numero + ":" + id;
        DataResult result = null;

        try {

            result = executor.submit(() -> {
                return model.saveRecipe(parametros, context);
            }).get();
        } catch (Exception e) {
            System.out.println("Error al enviar like: " + e.getMessage());
        }

        return result;
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
        String parametros = token + ":" + numero + ":" + id;
        DataResult result = null;

        try {

            result = executor.submit(() -> {
                return model.likeRecipe(parametros, context);
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

