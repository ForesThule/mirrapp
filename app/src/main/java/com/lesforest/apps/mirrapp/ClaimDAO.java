package com.lesforest.apps.mirrapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.lesforest.apps.mirrapp.model.Claim;

import java.util.List;

/**
 * Created by ve on 29.03.18.
 */

@Dao
public interface ClaimDAO {

    @Query("SELECT * FROM claim")
    List<Claim> getAll();

//    @Query("SELECT * FROM claim WHERE uid IN (:userIds)")
//    List<Claim> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM claim WHERE name LIKE :first")
    @Transaction
    Claim findByTimestamp(String first);

//    @Query("SELECT * FROM claim WHERE name LIKE :first AND "
//            + "last_name LIKE :last LIMIT 1")
//    Claim findByTimestamp(String first);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Claim... claims);

    @Delete
    void delete(Claim claim);
}