package com.lesforest.apps.mirrapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.lesforest.apps.mirrapp.model.Claim;

/**
 * Created by ve on 29.03.18.
 */

@Database(entities = {Claim.class}, version = 10,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClaimDAO claimDAO();
}