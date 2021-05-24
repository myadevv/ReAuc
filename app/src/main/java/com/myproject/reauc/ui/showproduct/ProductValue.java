package com.myproject.reauc.ui.showproduct;

public class ProductValue {
    private int resId;
    private String title;
    private String price;
    private String name;
    private String imageDir;
    private String endDate;
    private boolean payedStatus;

    // will be deprecated
    public String description;
    public String registerDate;

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price + " p"; };

    public void setPrice(int price) { this.price = price + " p"; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getImageDir() { return imageDir; }

    public void setImageDir(String imageDir) { this.imageDir = imageDir; }

    public String getEndDate() { return endDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean getPayedStatus() { return payedStatus; }

    public void setPayedStatus(String payed) {
        if (payed.equals("True")) payedStatus = true;
        else payedStatus = false;
    }
}
