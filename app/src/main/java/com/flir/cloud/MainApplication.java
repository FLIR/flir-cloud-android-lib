package com.flir.cloud;

import android.app.Application;
import android.content.Context;


import com.flir.cloud.di.ApplicationComponent;
import com.flir.cloud.di.ApplicationModule;
import com.flir.cloud.di.DaggerApplicationComponent;
import com.flir.cloud.di.NetworkModule;
import com.flir.cloud.di.Rx;
import com.flir.cloud.di.SdkModule;

public class MainApplication extends Application {

    private ApplicationComponent component;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        inject();
        context = getApplicationContext();
    }

    private void inject() {
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .sdkModule(new SdkModule())
                .rx(new Rx())
                .build();

    }

    public void refreshInjection(){
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .sdkModule(new SdkModule())
                .rx(new Rx())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return component;
    }

    public static Context getAppContext() {
        return MainApplication.context;
    }

}
