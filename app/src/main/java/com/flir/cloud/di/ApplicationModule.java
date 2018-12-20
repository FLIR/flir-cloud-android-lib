package com.flir.cloud.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Moti Amar on 22/03/2017.
 */
@Module
public class ApplicationModule {
    private final Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    @Named("application")
    @Singleton
    Context provideApplicationContext() {
        return context;
    }

    @Provides
    @Singleton
    SharedPreferences providePrefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
