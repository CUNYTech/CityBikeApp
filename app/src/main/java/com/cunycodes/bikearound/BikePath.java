package com.cunycodes.bikearound;


public class BikePath {

    private String cardName;
    private String imageURL;
    private String address;
    private String lat;
    private String lon;

    public void setCardName(String name) {
        this.cardName = name;
    }

    public void setImageURL(String img){
        this.imageURL = img;
    }

    public void setAddress(String address){
        address = address.replaceAll("[^a-zA-Z0-9, ]","").replaceAll(",", ", ");
        this.address = address;
    }
    
    public void setLat(String f){
        lat = f;
    }
    
    public void setLon(String f){
        lon = f;
    }
    public String getLat(){
        return lat;
    }

    public String getLon(){ return lon; }
    
    public String getImageURL(){
        return imageURL;
    }

    public String getCardName(){
        return cardName;
    }

    public String getAddress() {
        return address;
    }
}
