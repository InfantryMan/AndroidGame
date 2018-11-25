package com.game.rk6cooperation.androidgame.Network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit-описание API сервиса для получения данных об очках игроков
 */
interface RetrofitApi {
    @GET("scoreboard/")
    Call<ResponseBody> getUsersList(@Query("id") Integer id, @Query("page") Integer page);

    @GET("/")
    Call<ResponseBody> test();

    @GET("scoreboard/{id}")
    Call<ResponseBody> getUser(@Path("id") Integer id);
}
