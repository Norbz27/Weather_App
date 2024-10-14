package com.example.weather_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherViewModel extends ViewModel {
    private MutableLiveData<WeatherResponse> weatherData;
    private MutableLiveData<Location> locationData;
    private MutableLiveData<WeatherForecast> weatherForecast;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public WeatherViewModel() {
        weatherData = new MutableLiveData<>();
        locationData = new MutableLiveData<>();
        weatherForecast = new MutableLiveData<>();
    }

    public LiveData<WeatherResponse> getWeather() {
        return weatherData;
    }

    public LiveData<Location> getLocation() {
        return locationData;
    }

    public LiveData<WeatherForecast> getWeatherForecast() {
        return weatherForecast;
    }

    public void fetchWeather(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);

        String apiKey = "1afab9637f6b510f59ba5602c1f9d562";

        Call<WeatherResponse> call = weatherApi.getWeather(lat, lon, apiKey, "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("WeatherViewModel", "weather response: " + new Gson().toJson(response.body()));
                    weatherData.setValue(response.body());
                } else {
                    Log.e("WeatherViewModel", "Failed to get weather data: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherViewModel", "Failed to fetch weather data", t);
            }
        });

    }

    public void fetchWeeklyWeather(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        String apiKey = "1afab9637f6b510f59ba5602c1f9d562";

        Call<WeatherForecast> call = weatherApi.getWeeklyWeather(lat, lon, apiKey, "metric");
        call.enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("WeatherViewModel", "Forecast response: " + new Gson().toJson(response.body()));
                    weatherForecast.setValue(response.body());
                } else {
                    Log.e("WeatherViewModel", "Failed to get forecast data: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                Log.e("WeatherViewModel", "Failed to fetch forecast data", t);
            }
        });
    }

    public void fetchLocation(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Log.e("WeatherViewModel", "Location permission not granted.");
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                locationData.setValue(location);
            }
        }).addOnFailureListener(e -> {
            Log.e("WeatherViewModel", "Failed to get location", e);
        });
    }

    public void setLocation(Location location) {
        locationData.setValue(location);
    }

}
