package com.sample.weatherapp.app.activity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sample.weatherapp.app.R;
import com.sample.weatherapp.app.model.DayWeather;

public class ForecastAdapter extends BaseExpandableListAdapter {
    DayWeather forecast[];
    Context ctx;
    LayoutInflater lInflater;
    String GREG = "ForecastAdapter";

    public ForecastAdapter(Activity ctx, DayWeather[] forecast) {
        this.forecast = forecast;
        this.ctx = ctx;
        this.lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return forecast.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return forecast[groupPosition];
    }

    @Override
    public Object getChild(int arg0, int arg1) {
        Log.d(GREG, "getChild" + arg0);
        return forecast[arg0];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Log.d(GREG, "getChildId" + childPosition);
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Log.d(GREG, "start getView: ");
        View viewA = convertView;
        if (viewA == null) {
            Log.d(GREG, "if viewA==null: ");
            viewA = lInflater.inflate(R.layout.list_item_day, parent, false);
        }

        Log.d(GREG, "start try at getView ");

        ((TextView) viewA.findViewById(R.id.tvDate))
                .setText(forecast[groupPosition].date);

        ((TextView) viewA.findViewById(R.id.tvDayTemperature))
                .setText(forecast[groupPosition].dayTemperature + "..."
                        + forecast[groupPosition].nightTemperature + "C");

        ((TextView) viewA.findViewById(R.id.tvDescription))
                .setText(forecast[groupPosition].description);
        ((ImageView) viewA.findViewById(R.id.imDay))
                .setImageResource(forecast[groupPosition].imageId);
        return viewA;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup parent) {
        Log.d(GREG, "start getChildView: ");
        View viewChild = convertView;
        if (viewChild == null) {
            Log.d(GREG, "if viewA==null: ");
            viewChild = lInflater.inflate(R.layout.extendable_list_item_day,
                    parent, false);
        }

        Log.d(GREG, "start try at getView ");
        ((TextView) viewChild.findViewById(R.id.ELtvMornTemperature))
                .setText(forecast[groupPosition].mornTemperature + "C");
        ((TextView) viewChild.findViewById(R.id.ELtvDayTemperature))
                .setText(forecast[groupPosition].dayTemperature + "C");
        ((TextView) viewChild.findViewById(R.id.ELtvNightTemperature))
                .setText(forecast[groupPosition].nightTemperature + "C");
        ((TextView) viewChild.findViewById(R.id.ELtvEveTemperature))
                .setText(forecast[groupPosition].eveTemperature + "C");

        ((TextView) viewChild.findViewById(R.id.ELtvWindSpead))
                .setText(forecast[groupPosition].windSpeed);
        ((TextView) viewChild.findViewById(R.id.ELtvHumidity))
                .setText(forecast[groupPosition].humidity);
        ((TextView) viewChild.findViewById(R.id.ELtvPressure))
                .setText(forecast[groupPosition].pressure);
        return viewChild;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
