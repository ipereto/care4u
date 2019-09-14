package com.care4u.data.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class ProductRequest {

    @SerializedName("file")
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
