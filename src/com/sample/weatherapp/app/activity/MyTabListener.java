package com.sample.weatherapp.app.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import com.sample.weatherapp.app.model.Data;

public class MyTabListener implements ActionBar.TabListener {

    private Fragment mFragment;
    private MainActivity mActivity;
    private final String mTag;
    private final Class mClass;
    Data data;
    ActionBar.Tab lastTab;
    FragmentTransaction lastFt;

    boolean isSelect = false;
    String GREG = "MyTabListener";

    public MyTabListener(MainActivity mainActivity, Data data, String mTag, Class<?> mClass) {
        Log.d(GREG, "myTabListenerConstructor");
        this.mActivity = mainActivity;
        this.data = data;
        this.mTag = mTag;
        this.mClass = mClass;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        lastTab = tab;
        lastFt = ft;
        if (mFragment == null) {
            Log.d("gerg", "onTabSelected " + mTag + "\ntab:" + tab + "\nft:" + ft);

            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(android.R.id.content, mFragment, mTag);

        } else {
            ft.attach(mFragment);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            Log.d("gerg", "onTabUnselected " + mTag + "\ntab:" + tab + "\nft:" + ft);
            ft.detach(mFragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.d("gerg", "onTabReselected " + mTag + "\ntab:" + tab + "\nft:" + ft);
        onTabUnselected(tab, ft);
        onTabSelected(tab, ft);
    }
}
