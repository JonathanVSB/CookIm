package com.example.cookim.controller.Add;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookim.databinding.ItemAddStepBinding;
import com.example.cookim.databinding.ItemNewStepContentBinding;
import com.example.cookim.model.recipe.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {


    private List<Step> stepList;
    private View.OnClickListener listener;
    Handler handler;
    private ItemNewStepContentBinding binding;
    private LayoutInflater inflater;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Intent data;
    private File file;


    public StepAdapter(ArrayList<Step> stepList, LayoutInflater inflater){
        this.stepList = stepList;
        this.inflater = inflater;
        handler = new Handler(Looper.getMainLooper());
    }


    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewStepContentBinding binding = ItemNewStepContentBinding.inflate(inflater,parent, false);
        return new StepViewHolder(binding.getRoot());
    }


    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Step step = stepList.get(position);
        holder.binding.stepPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // selectImage(holder.getAdapterPosition(), view.getContext());
            }
        });
    }

//    private void selectImage(int position, Context context) {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        ((Activity) context).startActivityForResult(intent, position);
//    }
//
//    private void onActivityForResult(int requestCode, int resultCode, Intent data) {
//
//        this.data = data; // Agrega esta línea para guardar la referencia a 'data'
//
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//            } else {
//                //file = loadImage(data);
//            }
//        }
//    }

    /**
     * Load the Image in the imageView
     *
     * @param
     */
//    private File loadImage(Intent data) {
//        Uri selectedImage = data.getData();
//        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//        cursor.moveToFirst();
//        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//        String picturePath = cursor.getString(columnIndex);
//        cursor.close();
//
//        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//        binding.stepPic.setImageBitmap(bitmap);
//
//        return new File(picturePath);
//    }


    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        private ItemNewStepContentBinding binding;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewStepContentBinding.bind(itemView);
        }
    }



}
