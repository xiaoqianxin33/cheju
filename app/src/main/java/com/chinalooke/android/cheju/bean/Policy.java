package com.chinalooke.android.cheju.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xiao on 2016/8/9.
 */
public class Policy implements Serializable {

    private String city;
    private String phone;
    private String IdNo;
    private String carNo;
    private Date regDate;
    private String userName;
    private String frameNo;
    private String engine;
    private String brand;
    private String policyDate;
    private String price;
    private String type;
    private String objectId;
    private String userid;
    private String company;
    private String forceimgs;
    private String businessimage;
    private String detail;
    private String discountPrice;


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getBusinessimage() {
        return businessimage;
    }

    public void setBusinessimage(String businessimage) {
        this.businessimage = businessimage;
    }

    public String getForceimgs() {
        return forceimgs;
    }

    public void setForceimgs(String forceimgs) {
        this.forceimgs = forceimgs;
    }

    public String getCompany() {
        return company;

    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    private String status;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPolicy_expire() {
        return policy_expire;
    }

    public void setPolicy_expire(String policy_expire) {
        this.policy_expire = policy_expire;
    }

    private String policy_expire;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdNo() {
        return IdNo;
    }

    public void setIdNo(String idNo) {
        this.IdNo = idNo;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPolicyDate() {
        return policyDate;
    }

    public void setPolicyDate(String policyDate) {
        this.policyDate = policyDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
