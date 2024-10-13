package com.example.weather_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeeklyWeatherAdapter extends RecyclerView.Adapter<WeeklyWeatherAdapter.ViewHolder> {
    private final List<WeeklyWeatherSummary> dailyWeatherSummaries;

    public WeeklyWeatherAdapter(List<WeeklyWeatherSummary> dailyWeatherSummaries) {
        this.dailyWeatherSummaries = dailyWeatherSummaries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeeklyWeatherSummary summary = dailyWeatherSummaries.get(position);

        String dateString = summary.getDate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE dd", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            String formattedDate = outputFormat.format(date);
            holder.dateTextView.setText(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            holder.dateTextView.setText("Invalid date");
        }

        double tempCelsius = summary.getAvgTemp();

        double tempFahrenheit = (tempCelsius * 9 / 5) + 32;

        holder.tempTextView.setText(String.format("%.1f °C / %.1f °F", tempCelsius, tempFahrenheit));

        holder.descriptionTextView.setText(summary.getDescription());

        String iconCode = summary.getIconCode();
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
        Glide.with(holder.weatherIconImageView.getContext())
                .load(iconUrl)
                .into(holder.weatherIconImageView);
    }

    @Override
    public int getItemCount() {
        return dailyWeatherSummaries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView;
        private final TextView tempTextView;
        private final TextView descriptionTextView;
        private final ImageView weatherIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.text_view_date);
            tempTextView = itemView.findViewById(R.id.text_view_temp);
            descriptionTextView = itemView.findViewById(R.id.text_view_description);
            weatherIconImageView = itemView.findViewById(R.id.image_view_weather_icon);
        }
    }
}
