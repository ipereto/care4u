package com.care4u.service;

public class PredictProductServiceClient {

    public static final String BASE_URL = "http://127.0.0.1:5000/predict/";

    public static APIService request() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
