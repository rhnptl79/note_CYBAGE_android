package com.example.notesapp.data;

import java.io.Serializable;

public class NotesData implements Serializable {


    int nId;
    String title;
    String description;
    String imagePath;
    String lat;
    String lng;
    String voicePath;
    int catId;
    String timeStamp;
    public NotesData(int nId,String title, String description, String imagePath, String lat, String lng, String voicePath,String catId,String timeStamp) {
        this.nId = nId;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.lat = lat;
        this.lng = lng;
        this.voicePath = voicePath;
        this.timeStamp = timeStamp;
        this.catId = Integer.parseInt(catId);
    }

    public int getnId() {
        return nId;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getVoicePath() {
        return voicePath;
    }
    public int getCatId() {
        return catId;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

}
