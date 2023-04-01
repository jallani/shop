package com.zamili.shop;

public class usermodel {
    private String ProImage;
    private String Proname;
    private String Prophonenumber;
    private String Proprice;
    private String productId;
    private String timestamp;
    private String uid;

    public usermodel() {
    }

    public usermodel(String proImage, String proname, String prophonenumber, String proprice, String productId, String timestamp, String uid) {
        ProImage = proImage;
        Proname = proname;
        Prophonenumber = prophonenumber;
        Proprice = proprice;
        this.productId = productId;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getProImage() {
        return ProImage;
    }

    public void setProImage(String proImage) {
        ProImage = proImage;
    }

    public String getProname() {
        return Proname;
    }

    public void setProname(String proname) {
        Proname = proname;
    }

    public String getProphonenumber() {
        return Prophonenumber;
    }

    public void setProphonenumber(String prophonenumber) {
        Prophonenumber = prophonenumber;
    }

    public String getProprice() {
        return Proprice;
    }

    public void setProprice(String proprice) {
        Proprice = proprice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
