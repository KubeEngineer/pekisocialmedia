package com.example.chatpeki.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post implements Cloneable{
    public String durum_icerik,gonderen_id,gonderi_id,resim,tarih;

    public Post() {
    }

    public Post(String durum_icerik, String gonderen_id, String gonderi_id, String resim, String tarih) {
        this.durum_icerik = durum_icerik;
        this.gonderen_id = gonderen_id;
        this.gonderi_id = gonderi_id;
        this.resim = resim;
        this.tarih = tarih;
    }

    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("durum_icerik", durum_icerik);
        result.put("gonderen_id", gonderen_id);
        result.put("gonderi_id", gonderi_id);
        result.put("resim", resim);
        result.put("tarih", tarih);

        return result;
    }

    public String getDurum_icerik() {
        return durum_icerik;
    }

    public void setDurum_icerik(String durum_icerik) {
        this.durum_icerik = durum_icerik;
    }

    public String getGonderen_id() {
        return gonderen_id;
    }

    public void setGonderen_id(String gonderen_id) {
        this.gonderen_id = gonderen_id;
    }

    public String getGonderi_id() {
        return gonderi_id;
    }

    public void setGonderi_id(String gonderi_id) {
        this.gonderi_id = gonderi_id;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }
}
