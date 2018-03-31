package com.lesforest.apps.mirrapp.model;

import java.util.Date;

public class ClaimBuilder {
    private String name;
    private Date timestamp;
    private String imageLink;
    private boolean isPayed;
    private double user_price;
    private double real_price;
    private double my_money;

    public ClaimBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ClaimBuilder setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ClaimBuilder setImageLink(String imageLink) {
        this.imageLink = imageLink;
        return this;
    }

    public ClaimBuilder setIsPayed(boolean isPayed) {
        this.isPayed = isPayed;
        return this;
    }

    public ClaimBuilder setUser_price(double user_price) {
        this.user_price = user_price;
        return this;
    }

    public ClaimBuilder setReal_price(double real_price) {
        this.real_price = real_price;
        return this;
    }

    public ClaimBuilder setMy_money(double my_money) {
        this.my_money = my_money;
        return this;
    }

}