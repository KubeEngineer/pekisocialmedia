package com.example.chatpeki.Controller;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaNesneDinleyici;
import com.example.chatpeki.Model.Bildirimler;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.Post;
import com.example.chatpeki.View.PostSendActivity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PostController {

    private String mevcutKullaniciId;

    private StorageReference PostResimleriYolu;
    private DatabaseReference veriYolu;
    private DatabaseFacade DF = DatabaseFacade.get();
    private StorageTask yuklemeGorevi;
    private String gonderiId ;
    private Uri resimUri;
    private boolean testMode = true;
    private PostSendActivity postSendActivity;
    //singleton
    private static PostController postController;
    public static PostController getInstance() {
        if(postController==null){
            postController=new PostController();
        }
        return postController;
    }
    public void baslangic(){
        mevcutKullaniciId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        veriYolu = FirebaseDatabase.getInstance().getReference();
        PostResimleriYolu = FirebaseStorage.getInstance().getReference().child("PostResimleri");
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public void postYukle(final String gonderiId, final String resim) {
        baslangic();
        getPostSendActivity().progresGosterme();
        DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Postlar");
        String myUri = resim;
        String durumIcerik = getPostSendActivity().postIcerikViewAyar();
        Calendar tarihIcinTakvim = Calendar.getInstance();
        SimpleDateFormat aktifTarihFormati = new SimpleDateFormat("MMM dd, yyyy");
        String tarih = aktifTarihFormati.format(tarihIcinTakvim.getTime());
        Post p = new Post(durumIcerik, mevcutKullaniciId, gonderiId, resim, tarih);
        Map<String, Object> postHaritasi = p.toMap();
        veriYolu.child(gonderiId).updateChildren(postHaritasi);
        Toast.makeText(getPostSendActivity(), "Başarıyla Post Güncellendi.", Toast.LENGTH_LONG).show();
        getPostSendActivity().progresBitir();
        getPostSendActivity().anasayfaYonlendirme();

    }

    private String dosyaUzantisiAl(Uri uri) {
        ContentResolver contentResolver = getPostSendActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean postYukle(final String durum) throws ExecutionException, InterruptedException {
        baslangic();
        final CompletableFuture<Boolean> Result = new CompletableFuture<>();
        if(!testMode) {
            getPostSendActivity().progresGosterme();
        }
        setGonderiId(veriYolu.push().getKey());

        if (getResimUri() != null) {
            resimliYukleme(durum);
        } //Eger resim yuklenmisse burası gerceklestırılecek
        else {
            resimsizYukleme(durum);
        }

        if (testMode)
            return Result.get();
        else
            return Result.getNow(true);
    }
    public void resimsizYukleme(String durum){
        baslangic();
        DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Postlar").push();
        String durumIcerik = durum;
        String gonderiId=veriYolu.getKey();
        Calendar tarihIcinTakvim = Calendar.getInstance();
        SimpleDateFormat aktifTarihFormati = new SimpleDateFormat("MMM dd, yyyy");
        String tarih = aktifTarihFormati.format(tarihIcinTakvim.getTime());
        Post p = new Post(durumIcerik,mevcutKullaniciId,gonderiId,"",tarih);
        Map<String, Object> postHaritasi = p.toMap();
        veriYolu.setValue(postHaritasi);
        bildirimGonder(p.getGonderi_id(),"",mevcutKullaniciId);
        if(!testMode) {
            Toast.makeText(getPostSendActivity(), "Başarıyla Post Gönderildi.", Toast.LENGTH_LONG).show();
            getPostSendActivity().progresBitir();
            getPostSendActivity().anasayfaYonlendirme();
        }
    }
    public  void resimliYukleme(final String durum){
        baslangic();
        final StorageReference resimYolu = PostResimleriYolu.child(getGonderiId() + "." + dosyaUzantisiAl(getResimUri()));
        yuklemeGorevi = resimYolu.putFile(getResimUri());
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
                    DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Postlar").push();
                    String gonderiId=veriYolu.getKey();
                    String myUri = indirmeUrisi.toString();
                    String durumIcerik = durum;

                    Calendar tarihIcinTakvim = Calendar.getInstance();
                    SimpleDateFormat aktifTarihFormati = new SimpleDateFormat("MMM dd, yyyy");

                    String tarih = aktifTarihFormati.format(tarihIcinTakvim.getTime());
                    Post p = new Post(durumIcerik,mevcutKullaniciId,gonderiId,myUri,tarih);
                    Map<String, Object> postHaritasi = p.toMap();
                    veriYolu.setValue(postHaritasi);
                    bildirimGonder(p.getGonderi_id(),myUri,mevcutKullaniciId);

                    if(!testMode) {
                        Toast.makeText(getPostSendActivity(), "Başarıyla Post Gönderildi.", Toast.LENGTH_LONG).show();
                        getPostSendActivity().progresBitir();
                        getPostSendActivity().anasayfaYonlendirme();
                    }
                } else {
                    String mesaj = task.getException().toString();

                    if(!testMode) {
                        Toast.makeText(getPostSendActivity(), "Hata:" + mesaj + "", Toast.LENGTH_LONG).show();
                        getPostSendActivity().progresBitir();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(!testMode) {
                    Toast.makeText(getPostSendActivity(), "Hata:" + e.getMessage() + "", Toast.LENGTH_LONG).show();
                    getPostSendActivity().progresBitir();
                }
            }
        });
    }


    public void kullaniciBilgisiGetirme() {
        baslangic();
        veriYolu.child("Kullanicilar").child(mevcutKullaniciId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getPostSendActivity().kullaniciProfilViewAyarlama(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void bildirimGonder(final String post_id, final String post_resim, final String gonderen_id) {
        baslangic();
        String path = "Kullanicilar/"+mevcutKullaniciId;
        DF.oku().nesneGetir(path, Kisiler.class, new IOkumaNesneDinleyici<Kisiler>() {
            @Override
            public void tamamlandi(Kisiler object) {
                String mesaj = object.getAd() + " isimli arkadaşın yeni bir durum paylaştı !";
                Bildirimler bildirim = new Bildirimler(mesaj, gonderen_id, null, post_resim, post_id);
                object.arkadaslaraBildir(bildirim);
            }
        });
    }

    public PostSendActivity getPostSendActivity() {
        return postSendActivity;
    }

    public void setPostSendActivity(PostSendActivity postSendActivity) {
        this.postSendActivity = postSendActivity;
    }

    public String getGonderiId() {
        return gonderiId;
    }

    public void setGonderiId(String gonderiId) {
        this.gonderiId = gonderiId;
    }

    public Uri getResimUri() {
        return resimUri;
    }

    public void setResimUri(Uri resimUri) {
        this.resimUri = resimUri;
    }
}
