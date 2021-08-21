package com.example.chatpeki.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Bildirimler {
    public String bildirim_icerik, bildiren_id,bildiri_alan_id,bildirim_resim,post_id;

    public Bildirimler() {
    }

    public Bildirimler(String bildirim_icerik, String bildiren_id, String bildiri_alan_id, String bildirim_resim, String post_id) {
        this.bildirim_icerik = bildirim_icerik;
        this.bildiren_id = bildiren_id;
        this.bildiri_alan_id = bildiri_alan_id;
        this.bildirim_resim = bildirim_resim;
        this.post_id = post_id;
    }

    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("bildirim_icerik", bildirim_icerik);
        result.put("bildiren_id", bildiren_id);
        result.put("bildiri_alan_id", bildiri_alan_id);
        result.put("bildirim_resim", bildirim_resim);
        result.put("post_id", post_id);

        return result;
    }

    public String getBildirim_icerik() {
        return bildirim_icerik;
    }

    public void setBildirim_icerik(String bildirim_icerik) {
        this.bildirim_icerik = bildirim_icerik;
    }

    public String getBildiren_id() {
        return bildiren_id;
    }

    public void setBildiren_id(String bildiren_id) {
        this.bildiren_id = bildiren_id;
    }

    public String getBildirim_resim() {
        return bildirim_resim;
    }

    public void setBildirim_resim(String bildirim_resim) {
        this.bildirim_resim = bildirim_resim;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getBildiri_alan_id() {
        return bildiri_alan_id;
    }

    public void setBildiri_alan_id(String bildiri_alan_id) {
        this.bildiri_alan_id = bildiri_alan_id;
    }
}
