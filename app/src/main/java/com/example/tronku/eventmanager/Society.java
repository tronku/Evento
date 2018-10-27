package com.example.tronku.eventmanager;

public class Society {
    private String name, uri, id;

    public Society() {
    }

    public Society(String name, String uri, String id) {
        this.name = name;
        this.uri = uri;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
