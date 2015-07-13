package com.sample.weatherapp.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.sample.weatherapp.app.R;
import com.sample.weatherapp.app.util.GPSTracker;

public class LocationActivity extends Activity implements OnClickListener, LocationListener {

    private final String GREG = "locationActivity";
    Activity locationActivity = this;
    private AutoCompleteTextView tvEnterCity;
    private EditText lat;
    private EditText lon;
    private LocationManager locationManager;
    private String provider;
    GPSTracker gps;

    private String formatLocation(Location location) {
        if (location == null) return "";
        return
                String.format(
                        "%1$.4f, %2$.4f",
                        location.getLatitude(), location.getLongitude());
//        new Date(
//                        location.getTime()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(GREG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        tvEnterCity = (AutoCompleteTextView) findViewById(R.id.autoCompleteCity);
        String[] cities = getResources().getStringArray(R.array.city);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, cities);
        tvEnterCity.setAdapter(adapter);

        Button btnSearchByCity = (Button) findViewById(R.id.btnSearchByCity);
        btnSearchByCity.setOnClickListener(this);

        lat = (EditText) findViewById(R.id.editLat);
        lon = (EditText) findViewById(R.id.editLon);

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                Toast toast = Toast.makeText(locationActivity,
                        "Пишите на английском",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                for (int i = start; i < end; i++) {
                    if (!isEnglish(source.charAt(i))) {
                        toast.show();
                        return "";
                    }
                }
                return null;
            }

            private boolean isEnglish(char charAt) {
                String validationString = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM, ";
                if (validationString.indexOf(charAt) == -1)
                    return false;
                else
                    return true;
            }
        };
        tvEnterCity.setFilters(new InputFilter[]{filter});

        // Get the location manager
        /*gps = new GPSTracker(LocationActivity.this);
        if (gps.canGetLocation()) {
            lat.setText(Double.toString(gps.getLatitude()));
            lon.setText(String.valueOf(gps.getLongitude()));
        }*/
        Button btnSearchByCrd = (Button) findViewById(R.id.btnSearchByCrd);
        btnSearchByCrd.setOnClickListener(this);
    }

    public void onLocationChanged(Location location) {
        int latitude = (int) (location.getLatitude());
        int longitude = (int) (location.getLongitude());
        lat.setText(String.valueOf(latitude));
        lon.setText(String.valueOf(longitude));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        locationManager.removeUpdates(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        switch (v.getId()) {
            case R.id.btnSearchByCity:
                String str = tvEnterCity.getText().toString();
                Log.d(GREG, "read city " + str);
                intent.putExtra("city", str);
                setResult(1, intent);
                startActivity(intent);
                break;
            case R.id.btnSearchByCrd:
                String lat = this.lat.getText().toString();
                String lon = this.lon.getText().toString();
                Log.d(GREG, "read lat " + lat + "lon " + lon);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                setResult(2, intent);
                startActivity(intent);
                break;
        }
//        finish();
    }
}
