package com.sample.weatherapp.app.util;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.sample.weatherapp.app.activity.MainActivity;
import com.sample.weatherapp.app.model.Data;
import com.sample.weatherapp.app.model.DayWeather;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UrlTask extends AsyncTask<Void, Integer, Integer> {
    private String urlStrDay;
    private String urlStrForecast;
    private String title;
    private Data data;

    private MainActivity mainActivity;
    private String greg = "UrlTask";
    private DayWeather nowWeather;
    private DayWeather[] forecastWeather;
    JSONObject joDay;
    JSONObject joForecast;

    public UrlTask(MainActivity mainActivity, Data d, String day,
                   String forecast, String title) {
        Log.d(greg, "constructor ");
        data = d;
        this.title = title;
        urlStrDay = day; // day
        urlStrForecast = forecast; // forecast
        this.mainActivity = mainActivity;

    }

    @Override
    protected void onPreExecute() {
        mainActivity.loadAnimationStart();
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        if (isCancelled())
            return null;
        try {// day
            Log.d(greg, urlStrDay);
            joDay = urlConnect2(urlStrDay);
            parseDay(joDay);
        } catch (JSONException e) {
            e.printStackTrace();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        } catch (ParseException e) {
            e.printStackTrace();
            return 3;
        }

        if (isCancelled())
            return null;

        try {// forecast
            Log.d(greg, urlStrForecast);
            joForecast = urlConnect2(urlStrForecast);
            parseForecast(joForecast);
        } catch (JSONException e) {
            e.printStackTrace();
            return 1;

        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        } catch (ParseException e) {
            e.printStackTrace();
            return 3;
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            // if data was not received
            case 2:
                Toast.makeText(mainActivity, "�������� ����������� � ���������",
                        Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(mainActivity,
                        "������ �� ����� �������� �� ������ ������",
                        Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(mainActivity,
                        "error",
                        Toast.LENGTH_LONG).show();
                break;

            // if data has been successfully received
            case 0:
                // save query string and title to refresh data next time
                data.urlStrDay = urlStrDay;
                data.urlStrForecast = urlStrForecast;
                data.title = title;
                data.setForecast(forecastWeather);
                data.setNowWeather(nowWeather);

                SharedPreferences.Editor editor = mainActivity.mSettings.edit();
                editor.putString("title", title);
                editor.putString("urlStrDay", urlStrDay);
                editor.putString("urlStrForecast", urlStrForecast);
                editor.putString("joDay", String.valueOf(joDay));
                editor.putString("joForecast", String.valueOf(joForecast));
                editor.apply();

                // refresh visible activity
                if (mainActivity.visibleOnScreen)
                    mainActivity.afterUrlTask();
                else
                    mainActivity.showNewData = true;
                break;
        }
        mainActivity.loadAnimationStop();
    }

    private void parseForecast(JSONObject weatherJson) throws JSONException {
        Log.d(greg, "try parse jsonForecast: " + weatherJson);
        if (weatherJson.has("cnt")){
            Log.d(greg, "try parse forecast start");
            int d = weatherJson.getInt("cnt");
            DayWeather[] forecast = new DayWeather[d];
            JSONArray list = weatherJson.getJSONArray("list");
            for (int i = 0; i <= d - 1; i++) {
                JSONObject day = (JSONObject) list.get(i);
                Log.d("greg", "forecast for (i=" + i + ")");
                forecast[i] = new DayWeather();
                forecast[i].parsForecast(day, mainActivity);
            }
            forecastWeather = forecast;
            return;
        }
        throw new JSONException("server error");
    }

    void parseDay(JSONObject weatherJson) throws JSONException {
        Log.d(greg, "try parse jsonDay: " + weatherJson);
        if (weatherJson.has("main")) {
            Log.d(greg, "try parse weather start");
            DayWeather nowWeather = new DayWeather();
            nowWeather.parsDay(weatherJson, mainActivity);
            this.nowWeather = nowWeather;
            return;
        }
        throw new JSONException("server error");
    }

    JSONObject urlConnect2(String url) throws
            IOException, JSONException, IOException {
        JSONObject jo = new JSONObject(
                EntityUtils.toString(new DefaultHttpClient().execute(
                        new HttpGet(url)).getEntity()));
        return jo;
    }
}
