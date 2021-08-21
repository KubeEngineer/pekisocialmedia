package com.example.chatpeki.Controller;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaNesneDinleyici;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.Post;
import com.example.chatpeki.View.ProfilActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;


public class ProfilController {

    private String mevcutKullaniciId;
    private DatabaseReference veriYolu;
    private ProfilActivity profilActivity;
    private StorageReference kullaniciProfilResimleriYolu;
    private StorageTask yuklemeGorevi;
    private Uri resimUri;
    private String myUri = "";

    //singleton
    private static ProfilController profilController;

    public static ProfilController getInstance() {
        if (profilController == null) {
            profilController = new ProfilController();

        }
        return profilController;
    }

    public void baslangic() {
        mevcutKullaniciId = DatabaseFacade.UId();
        veriYolu = FirebaseDatabase.getInstance().getReference();
        kullaniciProfilResimleriYolu = FirebaseStorage.getInstance().getReference().child("Profil Resimleri");
    }

    public void veriTabaninaResimYukle(
            final String adDegis,
            final String soyadDegis,
            final String kullaniciadiniDegis,
            final String epostaniDegis,
            final String sifreniDegis
    ) {
        baslangic();
        getProfilActivity().progresGoster();
        final StorageReference resimYolu = kullaniciProfilResimleriYolu.child(mevcutKullaniciId + "." + dosyaUzantisiAl(resimUri));
        yuklemeGorevi = resimYolu.putFile(resimUri);
        yuklemeGorevi.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return resimYolu.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri indirmeUrisi = task.getResult();
                    DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
                    myUri = indirmeUrisi.toString();
                    String gonderiId = mevcutKullaniciId;
                    HashMap<String, String> profilHaritasi = new HashMap<>();
                    profilHaritasi.put("uid", gonderiId);
                    profilHaritasi.put("Ad", adDegis);
                    profilHaritasi.put("Soyad", soyadDegis);
                    profilHaritasi.put("KullaniciAdi", kullaniciadiniDegis);
                    profilHaritasi.put("Eposta", epostaniDegis);
                    profilHaritasi.put("Sifre", sifreniDegis);
                    profilHaritasi.put("Resim", myUri);
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(sifreniDegis);
                    veriYolu.child(mevcutKullaniciId).setValue(profilHaritasi);
                    Toast.makeText(getProfilActivity(), "Başarıyla güncellendi.", Toast.LENGTH_LONG).show();
                    getProfilActivity().progressDurdur();
                } else {
                    String mesaj = task.getException().toString();
                    Toast.makeText(getProfilActivity(), "Hata:" + mesaj + "", Toast.LENGTH_LONG).show();
                    getProfilActivity().progressDurdur();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getProfilActivity(), "Hata:" + e.getMessage() + "", Toast.LENGTH_LONG).show();
                getProfilActivity().progressDurdur();
            }
        });
    }

    public String dosyaUzantisiAl(Uri uri) {
        ContentResolver contentResolver = getProfilActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void kullaniciBilgisiAl() {
        baslangic();
        veriYolu.child("Kullanicilar").child(mevcutKullaniciId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Ad")) && (dataSnapshot.hasChild("Soyad")) && dataSnapshot.hasChild("Resim")
                                && (dataSnapshot.hasChild("KullaniciAdi")) && (dataSnapshot.hasChild("Eposta"))
                                && (dataSnapshot.hasChild("Sifre"))) {
                            String adAl = dataSnapshot.child("Ad").getValue().toString();
                            String soyadAl = dataSnapshot.child("Soyad").getValue().toString();
                            String epostaAl = dataSnapshot.child("Eposta").getValue().toString();
                            String kullaniciadiAl = dataSnapshot.child("KullaniciAdi").getValue().toString();
                            String sifreAl = dataSnapshot.child("Sifre").getValue().toString();
                            String profilResmiAl = dataSnapshot.child("Resim").getValue().toString();
                            getProfilActivity().kullaniciBilgisiniYansitResimli(adAl, soyadAl, epostaAl, kullaniciadiAl, sifreAl, profilResmiAl);
                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Ad")) && (dataSnapshot.hasChild("Soyad")
                                && (dataSnapshot.hasChild("Sifre")) && (dataSnapshot.hasChild("Eposta")))
                                && (dataSnapshot.hasChild("KullaniciAdi"))) {
                            String adAl = dataSnapshot.child("Ad").getValue().toString();
                            String soyadAl = dataSnapshot.child("Soyad").getValue().toString();
                            String epostaAl = dataSnapshot.child("Eposta").getValue().toString();
                            String kullaniciadiAl = dataSnapshot.child("KullaniciAdi").getValue().toString();
                            String sifreAl = dataSnapshot.child("Sifre").getValue().toString();
                            getProfilActivity().kullaniciBilgisiniYansitResimsiz(adAl, soyadAl, epostaAl, kullaniciadiAl, sifreAl);
                        } else {
                            Toast.makeText(getProfilActivity(), "Lütfen profil bilgilerinizi ayarlayın.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public ProfilActivity getProfilActivity() {
        return profilActivity;
    }

    public void setProfilActivity(ProfilActivity profilActivity) {
        this.profilActivity = profilActivity;
    }

    public Uri getResimUri() {
        return resimUri;
    }

    public void setResimUri(Uri resimUri) {
        this.resimUri = resimUri;
    }
}
