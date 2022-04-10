package com.example.test;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Minhajul Islam  on 10/04/2022.
 */
public interface IWeatherData {
    @GET("posts/1/comments")
    Call<List<JsonPlaceModel>> getImage();

    @GET("TestApi/GetSection")
    Call<GetClassMain> getSchoolClass();
}
