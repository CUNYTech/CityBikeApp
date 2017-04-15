package com.cunycodes.bikearound;


public class BikePath {

    private String cardName;
    private String imageURL;
    private String address;

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
