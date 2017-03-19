package com.cunycodes.bikearound;


public class PopularPaths {

    private String cardName;
    private int imageResourceId;
    private String address;

    public void setCardName(String name) {
        this.cardName = name;
    }

    public void setImageResourceId(int img){
        this.imageResourceId = img;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public int getImageResourceId(){
        return imageResourceId;
    }

    public String getCardName(){
        return cardName;
    }

    public String getAddress() {
        return address;
    }
}
