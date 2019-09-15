package com.care4u.service;

public class PredictProductServiceClient {

    public static APIService request() {
        return RetrofitClient.getClient("http://192.168.1.1:5000/predict/").create(APIService.class);
    }
}