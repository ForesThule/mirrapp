package com.lesforest.apps.mirrapp;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lesforest.apps.mirrapp.components.MyAppGlideModule;

import timber.log.Timber;


public class ThisApp extends Application {


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

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
//                .addMigrations(MIGRATION_1_2)
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
