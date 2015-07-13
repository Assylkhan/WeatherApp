package com.sample.weatherapp.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.sample.weatherapp.app.R;
import com.sample.weatherapp.app.model.Data;
import com.sample.weatherapp.app.model.DayWeather;
import com.sample.weatherapp.app.util.UrlTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    public static Data newData;
    MyTabListener nowListener = new MyTabListener(this, newData, "now",
            FragmentNow.class);

    MyTabListener forecastListener = new MyTabListener(this, newData,
            "forecast", FragmentForecast.class);

    public ActionBar bar;
    public ActionBar.Tab now;
    ActionBar.Tab forecast;
    public ImageView imv;
    boolean refreshAnim = false;
    MenuItem refreshItem;
    UrlTask urlTask = null;
    public boolean visibleOnScreen = false;
    public boolean showNewData = false;
    public SharedPreferences mSettings;
    private static final String GREG = "mainActivity";

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(GREG, "onPause");
        visibleOnScreen = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(GREG, "onResume");
        if (showNewData) {
            afterUrlTask();
            showNewData = false;
        }
        visibleOnScreen = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(GREG, "OnCreate");
        setContentView(R.layout.first_activity);
        bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        now = bar.newTab();
        now.setText("       Сейчас    ");
        now.setTabListener(nowListener);
        bar.addTab(now);

        forecast = bar.newTab();
        forecast.setText("     Прогноз     ");
        forecast.setTabListener(forecastListener);
        bar.addTab(forecast);

        mSettings = getSharedPreferences("LAST_DATA", Context.MODE_PRIVATE);
        newData = new Data();
        if (mSettings.contains("title")) { 			// get last save data
            Log.d(GREG, "get last save data");
            newData.title = mSettings.getString("title", null);
            newData.urlStrDay = mSettings.getString("urlStrDay", null);
            newData.urlStrForecast = mSettings
                    .getString("urlStrForecast", null);

            DayWeather dw = new DayWeather();
            try {
                Log.d(GREG, "try pars from last data");

                dw.parsDay(new JSONObject(mSettings.getString("joDay", null)),
                        this);
                newData.setNowWeather(dw);

                JSONObject jo = new JSONObject(mSettings.getString(
                        "joForecast", null));
                JSONArray list = jo.getJSONArray("list");
                int d = list.length();
                DayWeather[] forecast = new DayWeather[d];

                for (int i = 0; i <= d - 1; i++) {
                    JSONObject day = (JSONObject) list.get(i);
                    Log.d(GREG, "forecast for (i=" + i + ")");
                    forecast[i] = new DayWeather();
                    forecast[i].parsForecast(day, this);
                }
                newData.setForecast(forecast);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bar.setTitle(newData.title);
        } else {

            // ask user chose city or coordinate
            Log.d(GREG, "startChangeActivity");
            startChangeActivity();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        Log.d("anim", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (null == refreshItem) {
            // first time create menu
            refreshItem = menu.findItem(R.id.refresh);
            return true;
        }

        if (!refreshAnim && null != refreshItem.getActionView()) {
            // stop animation
            Log.d("anim", "set action view null");
            refreshItem.getActionView().clearAnimation();
            return true;
        } else {
            // start animation
            refreshItem = menu.findItem(R.id.refresh);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imv = (ImageView) inflater.inflate(R.layout.imv_refresh, null);
            Animation an = AnimationUtils.loadAnimation(this,
                    R.anim.loadingrotate);
            an.setRepeatCount(Animation.INFINITE);
            imv.startAnimation(an);

            Log.d("anim", "set action view imv");
            refreshItem.setActionView(imv);
            refreshItem.setIcon(null);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editCity:
                startChangeActivity();
                break;
            case R.id.refresh:
                refresh();
                break;
        }
        return true;
    }

    public void startChangeActivity() {
        Log.d(GREG, "startChangeActivity");
        Intent intent = new Intent();
//        startActivityForResult(intent, 1);
        finish();
    }

    private void refresh() {
        // enough data to query
        if (null == newData.urlStrForecast || null == newData.urlStrDay
                || null == newData.title) {

            Toast.makeText(this, "�������� �����", Toast.LENGTH_LONG).show();

            return;
        }
        stopQuery();
        // start new query with last data
        urlTask = (UrlTask) new UrlTask(this, newData, newData.urlStrDay,
                newData.urlStrForecast, newData.title).execute();
    }

    public void afterUrlTask() {
        if (now != null) {
            now.select();
            bar.setTitle(newData.title);
        }
    }

    private void stopQuery() {// stop another query
        if (null != urlTask
                && (!urlTask.isCancelled() || AsyncTask.Status.FINISHED != urlTask
                .getStatus()))
            urlTask.cancel(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(GREG, "onActivityResult");
        switch (resultCode) {
            case 1:
                // start new query with "city"
                String city = intent.getStringExtra("city");
                Log.d(GREG, "city " + city);
                stopQuery();
                Log.d(GREG, "start newURL request code 1");
                urlTask = (UrlTask) new UrlTask(this, newData, newData.STR_WEATHER
                        + "q=" + city, newData.STR_FORECAST + "q=" + city
                        + "&cnt=14", city).execute();

                break;
            case 2:
                // start new query with coordinate
                String lat = intent.getStringExtra("lat");
                String lon = intent.getStringExtra("lon");
                Log.d(GREG, "request code 2");
                stopQuery();
                urlTask = (UrlTask) new UrlTask(this, newData, newData.STR_WEATHER
                        + "lat=" + lat + "&lon=" + lon, newData.STR_FORECAST
                        + "lat=" + lat + "&lon=" + lon + "&cnt=14", lat + " " + lon)
                        .execute();
                break;
        }
    }

    public void loadAnimationStart() {
        // start loading animation
        if (!refreshAnim) {
            Log.d("anim", "animation successful start ");
            refreshAnim = true;
            invalidateOptionsMenu();
        }
    }


    public void loadAnimationStop() {
        // stop loading animation
        if (refreshAnim) {
            Log.d("anim", "animation successful stop");
            refreshAnim = false;
            invalidateOptionsMenu();
        }
    }
}
