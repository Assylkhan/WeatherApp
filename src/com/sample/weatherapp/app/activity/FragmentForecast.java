package com.sample.weatherapp.app.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import com.sample.weatherapp.app.R;
import com.sample.weatherapp.app.model.DayWeather;


public class FragmentForecast extends Fragment {
    public View v;
    String greg = "FragmentForecast";
    ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(greg, "start");
        v = inflater.inflate(R.layout.fragment_forecast, null);
        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarFF);

        if (null != MainActivity.newData.getForecast()) {
            Log.d(greg, "get data");
            DayWeather[] dw = MainActivity.newData.getForecast();
            ForecastAdapter adapter = new ForecastAdapter(getActivity(), dw);
            ExpandableListView elvDay = (ExpandableListView) v
                    .findViewById(R.id.ELday);
            elvDay.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }

        return v;
    }
}