package com.example.watcher24.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface CityService {
    @POST()
    Call<JsonObject>getCity(@Url String url,@Body JsonObject body);

}
