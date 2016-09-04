package com.chinalooke.android.cheju.bean;

import java.io.Serializable;

/**
 * Created by xiao on 2016/9/3.
 */
public class Address implements Serializable {

    private String name;
    private String address;
    private String phone;
    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
