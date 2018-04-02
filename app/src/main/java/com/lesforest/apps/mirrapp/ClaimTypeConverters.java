package com.lesforest.apps.mirrapp;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lesforest.apps.mirrapp.model.Claim;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ClaimTypeConverters {

    private static Gson gson = new Gson();
//    Gson gson = new Gson();
    
    @TypeConverter
    public static List<String> stringClaimList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someClaimToString(List<String> someObjects) {
        String s = gson.toJson(someObjects);
        return null != s?s:"";
    }
}