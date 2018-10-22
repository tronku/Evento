package com.example.tronku.eventmanager;

public class Event {
    private String societyName, eventName, eventDesc, dateTime, imgUrl;

    public Event() {
    }

    public Event(String societyName, String eventName, String eventDesc, String dateTime, String imgUrl) {
        this.societyName = societyName;
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.dateTime = dateTime;
        this.imgUrl = imgUrl;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
