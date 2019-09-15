package com.care4u;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class Care4uApplication extends Application {
    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
