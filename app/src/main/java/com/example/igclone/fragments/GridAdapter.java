package com.example.igclone.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.igclone.Post;
import com.example.igclone.R;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<Post> {

    Context context;

    public GridAdapter(@NonNull Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView =  convertView;

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid, parent,false);
        }

        Post post = getItem(position);
        ImageView ivGrid = listItemView.findViewById(R.id.ivGrid);
        Glide.with(getContext()).load(post.getImage().getUrl()).transform(new CenterCrop()).into(ivGrid);
        return listItemView;
    }
}
