package com.example.chatpeki.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.View.AnasayfaActivity;
import com.example.chatpeki.View.Kayit_olActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class KayitolController {
    private FirebaseAuth mYetki;
    private DatabaseReference veriYolu;
    private DatabaseReference kokReferans;
    private Boolean testMode = false;

    private Kayit_olActivity kayitolAktivity;

    //singleton
    private static KayitolController kayitolController;
    public static KayitolController getInstance(){
        if (kayitolController==null){
            kayitolController=new KayitolController();
        }
        return kayitolController;
    //singleton
    }
    public void  baslangic(){
        mYetki = FirebaseAuth.getInstance();
        kokReferans = FirebaseDatabase.getInstance().getReference();
        veriYolu = FirebaseDatabase.getInstance().getReference();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void yeniHesapOlustur(final String name, final String surName,
                                 final String e_posta, final String kullanici, final String sifree){
        baslangic();

        final CompletableFuture<String> dMesaj = new CompletableFuture<>();
        mYetki.createUserWithEmailAndPassword(e_posta, sifree)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //benzersiz bildirim ıd
                            String cihazToken = FirebaseInstanceId.getInstance().getToken();
                            String mevcutKullaniciId = DatabaseFacade.UId();
                            kokReferans.child("Kullanicilar").child(mevcutKullaniciId).setValue("");
                            kokReferans.child("Kullanicilar").child(mevcutKullaniciId).child("cihaz_token")
                                    .setValue(cihazToken);
                            if(!testMode) {

                            }
                            HashMap<String, String> profilHaritasi = new HashMap<>();
                            profilHaritasi.put("uid", mevcutKullaniciId);
                            profilHaritasi.put("Ad", name);
                            profilHaritasi.put("Soyad", surName);
                            profilHaritasi.put("KullaniciAdi", kullanici);
                            profilHaritasi.put("Eposta", e_posta);
                            profilHaritasi.put("Sifre", sifree);
                            veriYolu.child("Kullanicilar").child(mevcutKullaniciId).setValue(profilHaritasi)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if(!testMode) {
                                                    Intent anasayfayaDondur = new Intent(getKayitolAktivity(), AnasayfaActivity.class);
                                                    anasayfayaDondur.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    getKayitolAktivity().startActivity(anasayfayaDondur);
                                                    getKayitolAktivity().finish();
                                                    Toast.makeText(getKayitolAktivity(), "Yeni hesap başarı ile oluşturuldu.", Toast.LENGTH_SHORT).show();

                                                }
                                                dMesaj.complete("Basari ile kayit olundu");
                                            } else {
                                                if(!testMode) {
                                                    String mesaj = task.getException().toString();
                                                    Toast.makeText(getKayitolAktivity(), "Hata:" + mesaj + "", Toast.LENGTH_LONG).show();

                                                }
                                                dMesaj.complete("Kayit Basarisiz Oldu");
                                            }
                                        }
                                    });
                        } else {
                            String mesaj = task.getException().toString();
                            if(!testMode) {
                                Toast.makeText(getKayitolAktivity(), "Hata:" + mesaj + "Bilgilerinizi kontrol ediniz.", Toast.LENGTH_SHORT).show();
                            }
                            dMesaj.complete("Bilgilerinizi kontrol ediniz.");
                        }
                        if(!testMode)
                            getKayitolAktivity().progresBitir();
                    }
                });

    }

    public Kayit_olActivity getKayitolAktivity() {
        return kayitolAktivity;
    }

    public void setKayitolAktivity(Kayit_olActivity kayitolAktivity) {
        this.kayitolAktivity = kayitolAktivity;
    }
}
