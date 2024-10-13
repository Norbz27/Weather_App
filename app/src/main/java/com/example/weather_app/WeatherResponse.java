package com.example.weather_app;

import java.util.List;

public class WeatherResponse {
    public Main main;
    public List<Weather> weather;
    public String name;

    public static class Main {
        public float temp;
    }

    public static class Weather {
        public String description;
        public String icon;
    }
}
