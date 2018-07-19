package com.lesforest.apps.mirrapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.lesforest.apps.mirrapp.ClaimTypeConverters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;

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

    @Override
    public String toString() {
        return "Claim{" +
                "observer=" + observer +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isFinish=" + isFinish +
                ", imageLinks=" + imageLinks +
                ", timestamp='" + timestamp + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", isPayed=" + isPayed +
                ", price=" + price +
                ", prepay=" + prepay +
                ", remain=" + remain +
                ", advance=" + advance +
                ", surcharge=" + surcharge +
                ", cash=" + cash +
                '}';
    }

    private boolean isFinish = false;


    public void setImageLinks(List<String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    @TypeConverters(ClaimTypeConverters.class)
    private List<String> imageLinks = new ArrayList<>();

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
    private double prepay;

    public double getRemain() {
        return remain;
    }

    public void setRemain(double remain) {
        this.remain = remain;
    }

    public double getAdvance() {
        return advance;
    }

    public void setAdvance(double advance) {
        this.advance = advance;
    }

    public double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(double surcharge) {
        this.surcharge = surcharge;
    }

    private double remain;
    private double advance;
    private double surcharge;
    private double cash;

    private Claim(String name, String timestamp) {
        this.name = name;
        this.timestamp = timestamp;
//        time = timestamp;
    }

    public Claim(String name, Date timestamp, String imageLink, boolean isPayed, double price, double prepay, double cash) {
        this.name = name;
//        this.timestamp = timestamp;
        this.imageLink = imageLink;
        this.isPayed = isPayed;
        this.price = price;
        this.prepay = prepay;
        this.cash = cash;
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
        setCash(calclulateProfit());
    }

    private double calclulateProfit() {
        return price- prepay;
    }

    public double getPrepay() {
        return prepay;
    }

    public void setPrepay(double prepay) {
        this.prepay = prepay;
        setCash(calclulateProfit());
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
        if (observer!= null) {

            observer.onNext(this);
        }
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

    public List<String> getImageLinks() {
        return imageLinks;
    }

    public void addImagelink(String path) {
        imageLinks.add(path);
    }

    public void finishClaim(Object __) {
        isFinish = true;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}
