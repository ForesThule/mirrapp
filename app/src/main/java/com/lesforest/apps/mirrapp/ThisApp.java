package com.lesforest.apps.mirrapp;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lesforest.apps.mirrapp.components.MyAppGlideModule;

import timber.log.Timber;


public class ThisApp extends Application {

    private AppDatabase db;
    public MyAppGlideModule myAppGlideModule;

    public static ThisApp get(Context ctx) {
        return (ThisApp) ctx.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        myAppGlideModule = new MyAppGlideModule();

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "main_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) +
                            ":timber: line=" + element.getLineNumber() +
                            " method: " + element.getMethodName();
                }
            });
        }
    }


    public AppDatabase getDb() {
        return db;
    }
}
