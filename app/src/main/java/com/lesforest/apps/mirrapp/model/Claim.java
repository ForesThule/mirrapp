package com.lesforest.apps.mirrapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ve on 27.03.18.
 */

@Entity
public class Claim {

    @Ignore
    Observer<Claim> observer;

    @ColumnInfo(name = "name")
    private String name;

//    private long time;
    private String description;

    public Claim() {
    }



//    public long getTime() {
//        return time;
//    }
//
//    public void setTime(long time) {
//        this.time = time;
//    }


    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    @PrimaryKey
    @NonNull
    private String timestamp;


    private String imageLink;
    boolean isPayed;
    private double price;
    private double real_price;
    private double profit;

    private Claim(String name, String timestamp) {
        this.name = name;
        this.timestamp = timestamp;
//        time = timestamp;
    }

    public Claim(String name, Date timestamp, String imageLink, boolean isPayed, double price, double real_price, double profit) {
        this.name = name;
//        this.timestamp = timestamp;
        this.imageLink = imageLink;
        this.isPayed = isPayed;
        this.price = price;
        this.real_price = real_price;
        this.profit = profit;
    }



    public static Claim createClaim(String name, String timestamp) {
        return new Claim(name, timestamp);
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        isPayed = payed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImageLink() {
        return imageLink;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        setProfit(calclulateProfit());
    }

    private double calclulateProfit() {
        return price-real_price;
    }

    public double getReal_price() {
        return real_price;
    }

    public void setReal_price(double real_price) {
        this.real_price = real_price;
        setProfit(calclulateProfit());
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
        if (observer!= null) {

            observer.onNext(this);
        }
    }


    @Override
    public String toString() {
        return "Claim{" +
                "name='" + name + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
