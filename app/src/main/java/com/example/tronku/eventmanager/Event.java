package com.example.tronku.eventmanager;

public class Event {
    private String societyName, eventName, eventDesc, startDateTime, endDateTime, imgUrl, contact_person, contact_no;

    public Event(String societyName, String eventName, String eventDesc, String startDateTime, String endDateTime, String imgUrl, String contact_person, String contact_no) {
        this.societyName = societyName;
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.imgUrl = imgUrl;
        this.contact_person = contact_person;
        this.contact_no = contact_no;
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

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }
}
