package com.example.assignment;

import com.example.assignment.weatherModel.Root;

import retrofit2.http.GET;
import retrofit2.Call;

public interface WeatherRetrofitAPI {
    @GET("weather?q=Melbourne, AU&appid=872cfb217cd6562bd3d83688140e9ca6")
    Call<Root> getWeather();
}
