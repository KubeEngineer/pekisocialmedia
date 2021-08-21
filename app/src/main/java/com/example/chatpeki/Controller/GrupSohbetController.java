package com.example.chatpeki.Controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaNesneDinleyici;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.View.GrupSohbetActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GrupSohbetController {

    private DatabaseReference kullaniciYolu;
    private DatabaseReference grupAdiYolu;
    private DatabaseReference grupMesajAnahtariYolu;
    private DatabaseFacade DF = DatabaseFacade.get();
    private String  aktifKullaniciId=FirebaseAuth.getInstance().getCurrentUser().getUid();
    private GrupSohbetActivity grupSohbetActivity;
    private String mevcutGrupadi, aktifKullaniciAdi, aktifKullaniciSoyad, aktifTarih, aktifZaman;
    private static GrupSohbetController grupSohbetController;

    //singleton
    public static GrupSohbetController getInstance() {
        if(grupSohbetController==null){
            grupSohbetController=new GrupSohbetController();

        }
        return grupSohbetController;
    }
    public void baslangic(){
        aktifKullaniciId = DatabaseFacade.UId();
        grupAdiYolu = FirebaseDatabase.getInstance().getReference().child("Gruplar").child(getMevcutGrupadi());
    }

    public void veriKontrolEtme() {
    baslangic();
        grupAdiYolu.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    mesajlariGoster(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    mesajlariGoster(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void mesajlariGoster(DataSnapshot dataSnapshot) {
        baslangic();
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String sohbetadi = (String) ((DataSnapshot) iterator.next()).getValue();
            String sohbetmesaji = (String) ((DataSnapshot) iterator.next()).getValue();
            String sohbetSoyadi = (String) ((DataSnapshot) iterator.next()).getValue();
            String sohbettarihi = (String) ((DataSnapshot) iterator.next()).getValue();
            String sohbetzamani = (String) ((DataSnapshot) iterator.next()).getValue();
            String gonderen_id = (String) ((DataSnapshot) iterator.next()).getValue();
            getGrupSohbetActivity().layoutInflater();
            if (aktifKullaniciId.equals(gonderen_id)) {
                getGrupSohbetActivity().mesajGondereniYansitma(getGrupSohbetActivity().layoutInflater(),sohbetmesaji,sohbetzamani);

            } else {
                getGrupSohbetActivity().mesajAlaniYansitma(getGrupSohbetActivity().layoutInflater(),sohbetmesaji,sohbetzamani,sohbetadi,sohbetSoyadi);
            }
            //scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            getGrupSohbetActivity().scrollViewGoster();
        }
    }


    public void mesajiGonder(String mesaj) {
        baslangic();
        String mesajAnahtari = grupAdiYolu.push().getKey();
        if (mesaj.trim().isEmpty()) {
            Toast.makeText(getGrupSohbetActivity(), "Mesaj alani boş olmaz.", Toast.LENGTH_LONG).show();
        } else {
            Calendar tarihIcinTakvim = Calendar.getInstance();
            SimpleDateFormat aktifTarihFormati = new SimpleDateFormat("MMM dd, yyyy");
            aktifTarih = aktifTarihFormati.format(tarihIcinTakvim.getTime());

            Calendar zamanIcinTakvim = Calendar.getInstance();
            SimpleDateFormat aktifZamanFormati = new SimpleDateFormat("hh:mm:ss a");
            aktifZaman = aktifZamanFormati.format(zamanIcinTakvim.getTime());

            HashMap<String, Object> grupMesajAnahtari = new HashMap<>();
            grupAdiYolu.updateChildren(grupMesajAnahtari);
            grupMesajAnahtariYolu = grupAdiYolu.child(mesajAnahtari);

            HashMap<String, Object> mesajBilgisiMap = new HashMap<>();
            mesajBilgisiMap.put("Ad ", getAktifKullaniciAdi());
            mesajBilgisiMap.put("Soyad ", getAktifKullaniciSoyad());
            mesajBilgisiMap.put("Mesaj ", mesaj);
            mesajBilgisiMap.put("Tarih ", aktifTarih);
            mesajBilgisiMap.put("Zaman", aktifZaman);
            mesajBilgisiMap.put("gonderen_id", aktifKullaniciId);
            grupMesajAnahtariYolu.updateChildren(mesajBilgisiMap);
        }
    }

    public void  veritabanindanKullaniciBilgisiAl(){
        baslangic();
        String path = "Kullanicilar/"+aktifKullaniciId;
        DF.oku().nesneGetir(path, Kisiler.class, new IOkumaNesneDinleyici<Kisiler>() {
            @Override
            public void tamamlandi(Kisiler object) {
                setAktifKullaniciAdi(object.getKullaniciAdi());
                setAktifKullaniciSoyad(object.getSoyad());
            }
        });
    }

    public GrupSohbetActivity getGrupSohbetActivity() {
        return grupSohbetActivity;
    }

    public void setGrupSohbetActivity(GrupSohbetActivity grupSohbetActivity) {
        this.grupSohbetActivity = grupSohbetActivity;
    }

    public String getMevcutGrupadi() {
        return mevcutGrupadi;
    }

    public void setMevcutGrupadi(String mevcutGrupadi) {
        this.mevcutGrupadi = mevcutGrupadi;
    }

    public String getAktifKullaniciAdi() {
        return aktifKullaniciAdi;
    }

    public void setAktifKullaniciAdi(String aktifKullaniciAdi) {
        this.aktifKullaniciAdi = aktifKullaniciAdi;
    }

    public String getAktifKullaniciSoyad() {
        return aktifKullaniciSoyad;
    }

    public void setAktifKullaniciSoyad(String aktifKullaniciSoyad) {
        this.aktifKullaniciSoyad = aktifKullaniciSoyad;
    }
}
