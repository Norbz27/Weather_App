package com.example.weather_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.when;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private WeatherApi mockWeatherApi;

    @Mock
    private Call<WeatherResponse> mockWeatherCall;

    @Mock
    private Call<WeatherForecast> mockForecastCall;

    private WeatherViewModel weatherViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        weatherViewModel = new WeatherViewModel();
    }

    @Test
    public void testFetchWeatherSuccess() {
        WeatherResponse mockResponse = new WeatherResponse();
        WeatherResponse.Main main = new WeatherResponse.Main();
        main.temp = 25.0f;
        mockResponse.main = main;
        when(mockWeatherApi.getWeather(anyDouble(), anyDouble(), any(), any())).thenReturn(mockWeatherCall);

        Mockito.doAnswer(invocation -> {
            Callback<WeatherResponse> callback = invocation.getArgument(0);
            callback.onResponse(mockWeatherCall, Response.success(mockResponse));
            return null;
        }).when(mockWeatherCall).enqueue(any(Callback.class));

        weatherViewModel.fetchWeather(37.7749, -122.4194);

        assertNotNull(weatherViewModel.getWeather().getValue());
        assertEquals(25.0f, weatherViewModel.getWeather().getValue().main.temp, 0.01);
    }

    @Test
    public void testFetchWeatherFailure() {
        when(mockWeatherApi.getWeather(anyDouble(), anyDouble(), any(), any())).thenReturn(mockWeatherCall);
        Mockito.doAnswer(invocation -> {
            Callback<WeatherResponse> callback = invocation.getArgument(0);
            callback.onFailure(mockWeatherCall, new Throwable("API failure"));
            return null;
        }).when(mockWeatherCall).enqueue(any(Callback.class));

        weatherViewModel.fetchWeather(37.7749, -122.4194);

        assertNull(weatherViewModel.getWeather().getValue());
    }

    @Test
    public void testFetchWeeklyWeatherSuccess() {
        WeatherForecast mockForecastResponse = new WeatherForecast();
        when(mockWeatherApi.getWeeklyWeather(anyDouble(), anyDouble(), any(), any())).thenReturn(mockForecastCall);
        Mockito.doAnswer(invocation -> {
            Callback<WeatherForecast> callback = invocation.getArgument(0);
            callback.onResponse(mockForecastCall, Response.success(mockForecastResponse));
            return null;
        }).when(mockForecastCall).enqueue(any(Callback.class));

        weatherViewModel.fetchWeeklyWeather(37.7749, -122.4194);

        assertNotNull(weatherViewModel.getWeatherForecast().getValue());
    }

    @Test
    public void testFetchWeeklyWeatherFailure() {
        when(mockWeatherApi.getWeeklyWeather(anyDouble(), anyDouble(), any(), any())).thenReturn(mockForecastCall);
        Mockito.doAnswer(invocation -> {
            Callback<WeatherForecast> callback = invocation.getArgument(0);
            callback.onFailure(mockForecastCall, new Throwable("API failure"));
            return null;
        }).when(mockForecastCall).enqueue(any(Callback.class));

        weatherViewModel.fetchWeeklyWeather(37.7749, -122.4194);

        assertNull(weatherViewModel.getWeatherForecast().getValue());
    }

    @Test
    public void testFetchLocationSuccess() {
        Location mockLocation = new Location("mockProvider");
        mockLocation.setLatitude(37.7749);
        mockLocation.setLongitude(-122.4194);
        weatherViewModel.getLocation().observeForever(location -> {
            assertNotNull(location);
            assertEquals(37.7749, location.getLatitude(), 0.01);
            assertEquals(-122.4194, location.getLongitude(), 0.01);
        });

        weatherViewModel.setLocation(mockLocation);
    }
}
