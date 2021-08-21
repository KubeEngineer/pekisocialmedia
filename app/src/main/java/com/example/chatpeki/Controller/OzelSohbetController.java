package com.example.chatpeki.Controller;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.chatpeki.Adapter.mesajAdaptor;
import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaListeDinleyici;
import com.example.chatpeki.Model.Mesajlar;
import com.example.chatpeki.View.OzelSohbetActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import org.jetbrains.annotations.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OzelSohbetController {


    private Boolean testMode = true;
    private String MesajGonderenId,MesajiAliciId;
    private DatabaseReference reference;
    private DatabaseFacade DF = DatabaseFacade.get();
    private OzelSohbetActivity ozelSohbetActivity;
    private String aktifZaman;
    private StorageTask yuklemeGorevi;
    private String myUri="";

//singleton
    private static OzelSohbetController ozelSohbetController;
    public static OzelSohbetController getInstance() {
        if(ozelSohbetController==null){
            ozelSohbetController=new OzelSohbetController();

        }
        return ozelSohbetController;
    }//singleton
    private void baslangic(){
        MesajGonderenId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
    }
    private String aktifZaman(){
        Calendar zamanIcinTakvim = Calendar.getInstance();
        SimpleDateFormat aktifZamanFormati = new SimpleDateFormat("hh:mm:ss a");
        aktifZaman = aktifZamanFormati.format(zamanIcinTakvim.getTime());
        return aktifZaman;
    }
    public void veritabaniControl(final List<Mesajlar> mesajlarList, final mesajAdaptor mesajAdaptor){
        baslangic();
        mesajlarList.clear();
        reference.child("Mesajlar").child(MesajGonderenId).child(getMesajiAliciId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Mesajlar mesajlar = dataSnapshot.getValue(Mesajlar.class);
                mesajlarList.add(mesajlar);
                mesajAdaptor.notifyDataSetChanged();
                //scroll wiew ayarlama
                getOzelSohbetActivity().scrollViewayarlama();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Mesajlar mesaj = dataSnapshot.getValue(Mesajlar.class);
                for (int i = 0; i < mesajlarList.size(); i++) {
                    if (mesajlarList.get(i).getMesajID().equals(mesaj.getMesajID())) {
                        mesajlarList.remove(mesajlarList.get(i));
                        mesajAdaptor.notifyItemRemoved(i);
                        mesajAdaptor.notifyItemRangeChanged(i, mesajlarList.size());
                    }
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Boolean mesajGonder(String mesajText) throws ExecutionException, InterruptedException {
        baslangic();

        testMode=false;
        final CompletableFuture<Boolean> Result = new CompletableFuture<>();
        if (mesajText.trim().isEmpty()) {
            if (!testMode)
                Toast.makeText(getOzelSohbetActivity(), "Lütfen mesaj giriniz.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            String mesajGonderenRef = "Mesajlar/" + MesajGonderenId + "/" + getMesajiAliciId();
            String mesajAlanRef = "Mesajlar/" + getMesajiAliciId() + "/" + MesajGonderenId;
            DatabaseReference mesajID = reference.child("Mesajlar")
                    .child(MesajGonderenId).child(getMesajiAliciId()).push();
            String MesajID = mesajID.getKey();
            HashMap mesajBilgisiHaritasi = new HashMap();
            mesajBilgisiHaritasi.put("Mesaj", mesajText);
            mesajBilgisiHaritasi.put("Tipi", "Text");
            mesajBilgisiHaritasi.put("MesajID", MesajID);
            mesajBilgisiHaritasi.put("Kimden", MesajGonderenId);
            mesajBilgisiHaritasi.put("Kime", getMesajiAliciId());
            mesajBilgisiHaritasi.put("Zaman",aktifZaman());
            HashMap mesajBilgisiDetay = new HashMap();
            mesajBilgisiDetay.put(mesajGonderenRef + "/" + MesajID, mesajBilgisiHaritasi);
            mesajBilgisiDetay.put(mesajAlanRef + "/" + MesajID, mesajBilgisiHaritasi);

            reference.updateChildren(mesajBilgisiDetay).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        getOzelSohbetActivity().mesajTemizle();
                        Toast.makeText(getOzelSohbetActivity(), "Mesaj gönderildi.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getOzelSohbetActivity(), "Gönderilemedi.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        reference.child("Kullanicilar").child(getMesajiAliciId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //kullanicidurmunagöre verileri çekmek
                if (dataSnapshot.child("KullaniciDurumu").hasChild("Durum")) {
                    String durum = dataSnapshot.child("KullaniciDurumu").child("Durum").getValue().toString();
                    String tarih = dataSnapshot.child("KullaniciDurumu").child("Tarih").getValue().toString();
                    String zaman = dataSnapshot.child("KullaniciDurumu").child("Zaman").getValue().toString();
                    if (durum.equals("çevrimiçi")) {
                        getOzelSohbetActivity().cevrimIciYazma();
                    } else if (durum.equals("çevrimdişi")) {
                        getOzelSohbetActivity().enSonDurumu(tarih,zaman);
                    }

                } else {
                    getOzelSohbetActivity().cevrimDisiYazma();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (testMode)
            return Result.get();
        else
            return false;
    }


    public void resimDisindaGonderilenMesaj(final Uri dosyaUri, final String kontrol) {
        baslangic();

        StorageReference depolamaYolu= FirebaseStorage.getInstance().getReference().child("Dokuman Dosyalari");
        final String mesajGonderenRef = "Mesajlar/" + MesajGonderenId + "/" + getMesajiAliciId();
        final String mesajAlanRef = "Mesajlar/" + getMesajiAliciId() + "/" + MesajGonderenId;
        DatabaseReference mesajID = reference.child("Mesajlar")
                .child(MesajGonderenId).child(getMesajiAliciId()).push();
        final String MesajID = mesajID.getKey();
        final StorageReference dosyaYolu=depolamaYolu.child(mesajID+ "." + kontrol);
        yuklemeGorevi=dosyaYolu.putFile(dosyaUri);
        yuklemeGorevi.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }


                return dosyaYolu.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri indirmeUri=task.getResult();
                    myUri=indirmeUri.toString();
                    HashMap mesajBilgisiHaritasi = new HashMap();
                    mesajBilgisiHaritasi.put("Mesaj", myUri);
                    mesajBilgisiHaritasi.put("Ad", dosyaUri.getLastPathSegment());
                    mesajBilgisiHaritasi.put("Tipi", kontrol);
                    mesajBilgisiHaritasi.put("MesajID", MesajID);
                    mesajBilgisiHaritasi.put("Kimden", MesajGonderenId);
                    mesajBilgisiHaritasi.put("Kime", getMesajiAliciId());
                    mesajBilgisiHaritasi.put("Zaman",aktifZaman);
                    HashMap mesajBilgisiDetay = new HashMap();
                    mesajBilgisiDetay.put(mesajGonderenRef + "/" + MesajID, mesajBilgisiHaritasi);
                    mesajBilgisiDetay.put(mesajAlanRef + "/" + MesajID, mesajBilgisiHaritasi);

                    reference.updateChildren(mesajBilgisiDetay).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                getOzelSohbetActivity().mesajTemizle();
                                ozelSohbetActivity.yuklemeBardurdur();
                                Toast.makeText(getOzelSohbetActivity(), "Mesaj gönderildi.", Toast.LENGTH_SHORT).show();
                            } else {
                                ozelSohbetActivity.yuklemeBardurdur();
                                Toast.makeText(getOzelSohbetActivity(), "Gönderilemedi.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public void resimGondermeMesaji(final Uri dosyaUri, final String kontrol){
        baslangic();

        StorageReference depolamaYolu= FirebaseStorage.getInstance().getReference().child("Resim Dosyalari");
        final String mesajGonderenRef = "Mesajlar/" + MesajGonderenId + "/" + getMesajiAliciId();
        final String mesajAlanRef = "Mesajlar/" + getMesajiAliciId() + "/" + MesajGonderenId;
        DatabaseReference mesajID = reference.child("Mesajlar")
                .child(MesajGonderenId).child(getMesajiAliciId()).push();
        final String MesajID = mesajID.getKey();
        final StorageReference dosyaYolu=depolamaYolu.child(mesajID+"."+"jpg");
        yuklemeGorevi=dosyaYolu.putFile(dosyaUri);
        yuklemeGorevi.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return dosyaYolu.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri indirmeUri=task.getResult();
                    myUri=indirmeUri.toString();
                    HashMap mesajBilgisiHaritasi = new HashMap();
                    mesajBilgisiHaritasi.put("Mesaj", myUri);
                    mesajBilgisiHaritasi.put("Ad", dosyaUri.getLastPathSegment());
                    mesajBilgisiHaritasi.put("Tipi", kontrol);
                    mesajBilgisiHaritasi.put("MesajID", MesajID);
                    mesajBilgisiHaritasi.put("Kimden", MesajGonderenId);
                    mesajBilgisiHaritasi.put("Kime", getMesajiAliciId());
                    mesajBilgisiHaritasi.put("Zaman",aktifZaman);
                    HashMap mesajBilgisiDetay = new HashMap();
                    mesajBilgisiDetay.put(mesajGonderenRef + "/" + MesajID, mesajBilgisiHaritasi);
                    mesajBilgisiDetay.put(mesajAlanRef + "/" + MesajID, mesajBilgisiHaritasi);
                    reference.updateChildren(mesajBilgisiDetay).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                getOzelSohbetActivity().mesajTemizle();
                                ozelSohbetActivity.yuklemeBardurdur();
                                Toast.makeText(getOzelSohbetActivity(), "Mesaj gönderildi.", Toast.LENGTH_SHORT).show();
                            } else {
                                ozelSohbetActivity.yuklemeBardurdur();
                                Toast.makeText(getOzelSohbetActivity(), "Gönderilemedi.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public void SongorulmeyiGoster() {
        baslangic();

        reference.child("Kullanicilar").child(getMesajiAliciId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //kullanicidurmunagöre verileri çekmek
                if (dataSnapshot.child("KullaniciDurumu").hasChild("Durum")) {
                    String durum = dataSnapshot.child("KullaniciDurumu").child("Durum").getValue().toString();
                    String tarih = dataSnapshot.child("KullaniciDurumu").child("Tarih").getValue().toString();
                    String zaman = dataSnapshot.child("KullaniciDurumu").child("Zaman").getValue().toString();
                    if (durum.equals("çevrimiçi")) {
                        getOzelSohbetActivity().cevrimIciYazma();
                    } else if (durum.equals("çevrimdişi")) {
                        getOzelSohbetActivity().enSonDurumu(tarih,zaman);
                    }
                } else {
                    getOzelSohbetActivity().cevrimDisiYazma();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public String getMesajiAliciId() {
        return MesajiAliciId;
    }

    public void setMesajiAliciId(String mesajiAliciId) {
        MesajiAliciId = mesajiAliciId;
    }

    public OzelSohbetActivity getOzelSohbetActivity() {
        return ozelSohbetActivity;
    }

    public void setOzelSohbetActivity(OzelSohbetActivity ozelSohbetActivity) {
        this.ozelSohbetActivity = ozelSohbetActivity;
    }
}



