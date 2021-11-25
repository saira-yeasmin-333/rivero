package com.example.watcher24.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {
    public static final RetrofitApi instance = new RetrofitApi();
    public NotificationService notificationService;

    private RetrofitApi(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .followRedirects(true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://countriesnow.space/api/v0.1/countries/population/cities")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        notificationService =  retrofit.create(NotificationService.class);
    }
}
