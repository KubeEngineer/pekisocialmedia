package com.example.chatpeki.Controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.example.chatpeki.Adapter.yorumAdapter;
import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaListeDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaNesneDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IYazmaDinleyici;
import com.example.chatpeki.GenelDinleyiciler.IDinleyici;
import com.example.chatpeki.Model.Bildirimler;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.Yorum;
import com.example.chatpeki.View.YorumActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YorumController {
    private DatabaseFacade DF = DatabaseFacade.get();
    private String post_id,post_resim, alan_id, gonderen_id;
    private List<Yorum> yorumList=new ArrayList<>();
    private Kisiler kisi;
    private static YorumController yorumController;

    public static YorumController getInstance() {
        if(yorumController==null){
            yorumController=new YorumController();
        }
        return yorumController;
    }


    public void kullaniciBilgisi(){
        String path = "Kullanicilar/"+DatabaseFacade.UId();
        DF.oku().nesneGetir(path, Kisiler.class, new IOkumaNesneDinleyici<Kisiler>() {
            @Override
            public void tamamlandi(Kisiler object) {
                kisi =object;
            }
        });
    }

    public void yorumlariGetir(final IDinleyici dinleyici) {
        kullaniciBilgisi();
        String path = "Yorumlar/"+getPost_id();
        DF.oku().ListeGetir(path, Yorum.class, new IOkumaListeDinleyici<Yorum>() {
            @Override
            public void tamamlandi(ArrayList<Yorum> objectList) {
                dinleyici.tamamlandi(objectList);
            }
        });
    }

    public void yorumEkle(final String yorum, final RecyclerView view){
        kullaniciBilgisi();
        String path = "Yorumlar/"+getPost_id();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("yorum", yorum);
        hashMap.put("gonderen_id", getGonderen_id());
        DF.yaz().yaz(path, hashMap, new IYazmaDinleyici() {
            @Override
            public void tamamlandi() {
                if(kisi != null && !getAlan_id().equals(DatabaseFacade.UId())) {
                    String bildirim_icerik = kisi.getKullaniciAdi()+" İsimli kullanici gönderdiğin post'a \n\""+yorum+"\" dedi!";
                    Bildirimler bildirim = new Bildirimler
                            (bildirim_icerik, DatabaseFacade.UId(), getAlan_id(), getPost_resim(), getPost_id());
                    kisi.arkadaslaraBildir(bildirim);
                }
            }
        });
    }

    public List<Yorum> getYorumList() {
        return yorumList;
    }

    public void setYorumList(List<Yorum> yorumList) {
        this.yorumList = yorumList;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_resim() {
        return post_resim;
    }

    public void setPost_resim(String post_resim) {
        this.post_resim = post_resim;
    }

    public String getGonderen_id() {
        return gonderen_id;
    }

    public void setGonderen_id(String gonderen_id) {
        this.gonderen_id = gonderen_id;
    }

    public String getAlan_id() {
        return alan_id;
    }

    public void setAlan_id(String alan_id) {
        this.alan_id = alan_id;
    }
}

