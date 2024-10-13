package com.example.weather_app;

public class WeeklyWeatherSummary {
    private String date;
    private double avgTemp;
    private String description;
    private final String iconCode;

    public WeeklyWeatherSummary(String date, double avgTemp, String description, String iconCode) {
        this.date = date;
        this.avgTemp = avgTemp;
        this.description = description;
        this.iconCode = iconCode;
    }

    public String getDate() {
        return date;
    }

    public double getAvgTemp() {
        return avgTemp;
    }

    public String getDescription() {
        return description;
    }

    public String getIconCode() {
        return iconCode;
    }
}

