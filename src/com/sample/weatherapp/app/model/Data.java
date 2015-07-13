package com.sample.weatherapp.app.model;

import android.util.Log;

public class Data {
    String greg = "dataClass";

    private DayWeather nowWeather;
    private DayWeather[] forecast;

    public final String STR_WEATHER = "http://api.openweathermap.org/data/2.5/weather?";
    public final String STR_FORECAST = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    public String urlStrDay;
    public String urlStrForecast;
    public String title;

    public DayWeather getNowWeather() {
        return nowWeather;
    }

    public void setNowWeather(DayWeather nowWeather) {
        Log.d(greg, "setWeather");
        this.nowWeather = nowWeather;
    }

    public DayWeather[] getForecast() {
        return forecast;
    }

    public void setForecast(DayWeather forecast[]) {
        Log.d(greg, "setForecast");
        this.forecast = forecast;
    }
}
