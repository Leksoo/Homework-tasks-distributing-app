package com.ifmo.ct.homework.utils;

import com.google.gson.JsonObject;
import com.ifmo.ct.homework.model.GoogleSheet;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("tq")
    Call<String> get(@Query("key") String id);
}

