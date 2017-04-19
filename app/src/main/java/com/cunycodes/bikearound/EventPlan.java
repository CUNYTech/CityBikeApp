package com.cunycodes.bikearound;

public class EventPlan {

    int id;
    String date, time, place;

    public EventPlan(){

    }

    public EventPlan(int id, String date, String time, String place){
        this.id = id;
        this.date = date;
        this.time = time;
        this.place = place;
    }

    public EventPlan(String date, String time, String place){
        this.date = date;
        this.time = time;
        this.place = place;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setTime(String time){
        this.time = time;
    }

    public void setPlace(String place){
        this.place = place;
    }

    public int getId(){
        return this.id;
    }

    public String getDate(){
        return this.date;
    }

    public String getTime(){
        return this.time;
    }

    public String getPlace(){
        return this.place;
    }
}
