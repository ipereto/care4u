package com.care4u.service;

import com.care4u.data.model.ProductResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {

    @POST("/predict")
    @Multipart
    Call<ProductResponse> saveImage(@Part MultipartBody.Part file);
}
