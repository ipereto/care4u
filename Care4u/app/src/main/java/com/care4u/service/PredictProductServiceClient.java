package com.care4u.service;

public class PredictProductServiceClient {

    public static APIService request() {
        return RetrofitClient.getClient("http://34.217.105.95:8080/predict/").create(APIService.class);
    }
}