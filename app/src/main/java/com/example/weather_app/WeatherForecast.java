package com.example.weather_app;

import java.util.List;

public class WeatherForecast {
    public List<Forecast> list;

    public static class Forecast {
        public long dt;
        public Main main;
        public List<Weather> weather;

        public static class Main {
            public double temp;
        }

        public static class Weather {
            public String description;
            public String icon;
        }
    }
}
