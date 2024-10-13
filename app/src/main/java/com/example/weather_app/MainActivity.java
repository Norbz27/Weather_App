package com.example.weather_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private WeatherViewModel weatherViewModel;
    private TextView tempC, tempF, locationText, dateText, timeText, cityText;
    private RecyclerView weeklyWeatherRecyclerView;
    private ImageView weatherIconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkblue));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.lightblue));
        }

        tempC = findViewById(R.id.tempC);
        tempF = findViewById(R.id.tempF);
        locationText = findViewById(R.id.locationText);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        cityText = findViewById(R.id.cityText);
        weeklyWeatherRecyclerView = findViewById(R.id.weeklyWeatherRecyclerView);
        weatherIconImageView = findViewById(R.id.image_view_weather_icon);

        LinearLayoutManager weeklyLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weeklyWeatherRecyclerView.setLayoutManager(weeklyLayoutManager);


        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        fetchData();

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void fetchData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            weatherViewModel.fetchLocation(this);
        }

        weatherViewModel.getLocation().observe(this, location -> {
            if (location != null) {
                locationText.setText("Lat: "+location.getLatitude() + "\nLon: " + location.getLongitude());
                weatherViewModel.fetchWeather(location.getLatitude(), location.getLongitude());
                weatherViewModel.fetchWeeklyWeather(location.getLatitude(), location.getLongitude());
            }
        });

        weatherViewModel.getWeather().observe(this, weatherResponse -> {
            if (weatherResponse != null) {
                String iconCode = weatherResponse.weather.get(0).icon;
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                Glide.with(this)
                        .load(iconUrl)
                        .into(weatherIconImageView);

                double tempCelsius = weatherResponse.main.temp;

                double tempFahrenheit = (tempCelsius * 9 / 5) + 32;

                tempC.setText(String.format("%.1f °C", tempCelsius));
                tempF.setText(String.format("%.1f °F", tempFahrenheit));
                cityText.setText(weatherResponse.name);
            }
        });

        weatherViewModel.getWeatherForecast().observe(this, weatherForecast -> {
            if (weatherForecast != null) {
                List<WeatherForecast.Forecast> forecastList = weatherForecast.list;
                Map<String, List<WeatherForecast.Forecast>> dailyMap = new HashMap<>();

                for (WeatherForecast.Forecast forecast : forecastList) {
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(new Date(forecast.dt * 1000L));

                    if (!dailyMap.containsKey(date)) {
                        dailyMap.put(date, new ArrayList<>());
                    }
                    dailyMap.get(date).add(forecast);
                }

                List<WeeklyWeatherSummary> dailyWeatherSummaries = new ArrayList<>();

                for (String date : dailyMap.keySet()) {
                    List<WeatherForecast.Forecast> dailyForecasts = dailyMap.get(date);

                    double avgTemp = 0;
                    String description = "";
                    String iconCode = "";

                    for (WeatherForecast.Forecast forecast : dailyForecasts) {
                        avgTemp += forecast.main.temp;

                        if (description.isEmpty()) {
                            description = forecast.weather.get(0).description;
                            iconCode = forecast.weather.get(0).icon;
                        }
                    }
                    avgTemp /= dailyForecasts.size();

                    dailyWeatherSummaries.add(new WeeklyWeatherSummary(date, avgTemp, description, iconCode));
                }


                Collections.sort(dailyWeatherSummaries, (summary1, summary2) ->
                        summary1.getDate().compareTo(summary2.getDate()));

                Log.d("MainActivity", "Number of daily summaries: " + dailyWeatherSummaries.size());

                WeeklyWeatherAdapter adapter = new WeeklyWeatherAdapter(dailyWeatherSummaries);
                weeklyWeatherRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            String formattedDate = LocalDate.now().format(dateFormatter);
            dateText.setText(formattedDate);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            String formattedTime = LocalTime.now().format(timeFormatter);
            timeText.setText(formattedTime);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                weatherViewModel.fetchLocation(this);
            } else {
                locationText.setText("Location permission denied");
            }
        }
    }
}
