package com.example.chatpeki.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Kisiler implements IKullanici {
    String Ad,Soyad,KullaniciAdi,Resim,Eposta,uid;
    private List<Arkadas> arkadasList;//bu dinleyici butun arkadas listesi eklendiginde bize gelen bildirimi dinler//
    private IKisilerEklendiListener geriSayimDinleyici;//bu dinleyici butun arkadaslar eklenene kadar geri sayar sifirlanirsa
                                                    //tamamlandiDinleyici.onKisiEklendi methodu cagirilarak tamamlandiDinleyicisinin
                                                    //guncel veriye erismesini saglar.
    public Kisiler() {

    }

    public Kisiler(String ad, String soyad, String kullaniciAdi, String resim, String eposta, String uid) {
        Ad = ad;
        Soyad = soyad;
        KullaniciAdi = kullaniciAdi;
        Resim = resim;
        Eposta = eposta;
        this.uid = uid;
    }

    public String getAd() {
        return Ad;
    }

    public void setAd(String ad) {
        Ad = ad;
    }

    public String getSoyad() {
        return Soyad;
    }

    public void setSoyad(String soyad) {
        Soyad = soyad;
    }

    public String getKullaniciAdi() {
        return KullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        KullaniciAdi = kullaniciAdi;
    }

    public String getResim() {
        return Resim;
    }

    public void setResim(String resim) {
        Resim = resim;
    }

    public String getEposta() {
        return Eposta;
    }

    public void setEposta(String eposta) {
        Eposta = eposta;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private void setGeriSayimDinleyici(IKisilerEklendiListener listener) {
        this.geriSayimDinleyici = listener;
    }

    public List<Arkadas> getArkadasList() {
        return arkadasList;
    }

    @Override
    public void arkadaslariGetir(final IKisilerEklendiListener listener) {
        if(this.arkadasList == null)
            this.arkadasList = new ArrayList<>();
        final DatabaseReference referenceArkadas = FirebaseDatabase.getInstance().getReference("Arkadaslar");
        final DatabaseReference referenceKisi = FirebaseDatabase.getInstance().getReference("Kullanicilar");
        referenceArkadas.child(getUid()).addValueEventListener(new ValueEventListener() {
            int elemanSayisi;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                elemanSayisi = (int)dataSnapshot.getChildrenCount();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    referenceKisi.child(key).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Kisiler kisi = dataSnapshot.getValue(Kisiler.class);
                                    arkadasList.add(new Arkadas(kisi));
                                    elemanSayisi--;
                                    geriSayimDinleyici.onKisiEklendi();
                                    System.out.println("###############"+elemanSayisi);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("TAG", "onCancelled: "+databaseError.getMessage());
                                }
                            }
                    );
                }
                //tum arkadasların listeye eklendigi dogrulaniyor
                Kisiler.this.setGeriSayimDinleyici(new IKisilerEklendiListener() {
                    @Override
                    public void onKisiEklendi() {
                        if(elemanSayisi==0) {
                            listener.onKisiEklendi();
                            System.out.println("onKisiEklendi cagrıldı");
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: "+databaseError.getMessage());
            }
        });
    }

    @Override
    public void arkadaslaraBildir(final Bildirimler bildirim) {
        arkadaslariGetir(new IKisilerEklendiListener() {
            @Override
            public void onKisiEklendi() {
                if(bildirim.bildiri_alan_id == null) {
                    for (int i = 0; i < arkadasList.size(); i++) {
                        arkadasList.get(i).bildir(bildirim);
                    }
                } else {
                    int i=0;
                    for (i = 0; i < arkadasList.size(); i++) {
                        if(bildirim.bildiri_alan_id.equals(arkadasList.get(i).getKisi().getUid()))
                            break;
                    }
                    arkadasList.get(i).bildir(bildirim);
                }
            }
        });
       // List<Kisiler> kisi = arkadaslariGetir();
    }

}

