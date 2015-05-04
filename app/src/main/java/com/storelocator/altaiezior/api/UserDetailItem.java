package com.storelocator.altaiezior.api;

/**
 * Created by altaiezior on 20/4/15.
 */
public class UserDetailItem{
    String fname;
    String lname;
    String email;
    String shop_name;
    Long id;
    Long mobile;
    String shop_address;


    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public Long getMobile() {
        return mobile;
    }

    public String getShop_address() {
        return shop_address;
    }
}