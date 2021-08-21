package com.example.chatpeki.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class KullaniciDurum {
    private String durum,aktifKullaniciId;

    private DatabaseReference kullanicilarReference;

    public KullaniciDurum() {

    }

    public void KullaniciDurumuGuncelle(String durum){
        String kaydedilenaktifZaman,kaydedilenaktifTarih;
        Calendar calendar=Calendar.getInstance();
        //tarih formati
        SimpleDateFormat aktifTarih=new SimpleDateFormat("MMM dd,yyyy");
        kaydedilenaktifTarih=aktifTarih.format(calendar.getTime());
        //Saat formati
        SimpleDateFormat aktifSaat=new SimpleDateFormat("hh:mm a");
        kaydedilenaktifZaman=aktifSaat.format(calendar.getTime());
        HashMap<String,Object> cevrimiciDurumuMap=new HashMap<>();
        cevrimiciDurumuMap.put("Zaman",kaydedilenaktifZaman);
        cevrimiciDurumuMap.put("Tarih",kaydedilenaktifTarih);
        cevrimiciDurumuMap.put("Durum",durum);
        aktifKullaniciId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        kullanicilarReference = FirebaseDatabase.getInstance().getReference();
        kullanicilarReference.child("Kullanicilar").child(aktifKullaniciId).child("KullaniciDurumu").updateChildren(cevrimiciDurumuMap);
    }
}
