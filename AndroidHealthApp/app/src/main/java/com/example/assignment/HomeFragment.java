package com.example.assignment;


import android.content.Intent;
import android.icu.number.Precision;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.assignment.databinding.HomeFragmentBinding;
import com.example.assignment.weatherModel.Root;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private HomeFragmentBinding binding;
    private FirebaseAuth authentication;


    public HomeFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        authentication = FirebaseAuth.getInstance();

        // Generate and implement weather interface
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create a class through Retrofit
        WeatherRetrofitAPI weatherInterface = retrofit.create(WeatherRetrofitAPI.class);

        // Call weather
        Call<Root> weather = weatherInterface.getWeather();

        // Get data
        weather.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                // Get root
                Root root = response.body();
                // Get temperature
                double tempTemperature = root.getMain().getTemp();
                // transfer
                double temperature = tempTemperature - 273.15;
                // humidity
                int humidity = root.getMain().getHumidity();
                // pressure
                int pressure = root.getMain().getPressure();
                // weather conclusion
                String weather = "Temperature(Celsius): " + Math.round(temperature) + "\n\n"
                        + "Humidity: " + humidity + "\n\n" + "Pressure: " + pressure;
                // Set weather view
                binding.weatherView.setText(weather);
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                System.out.print(t.getMessage());
            }
        });

        // set sign out btn
        binding.logoutButton.setOnClickListener(v -> {
            authentication.signOut();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


