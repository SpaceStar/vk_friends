package com.example.vkfriends;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class BindingAdapters {

    private BindingAdapters() {}

    @BindingAdapter("app:loadImage")
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .circleCrop()
                .into(view);
    }
}
