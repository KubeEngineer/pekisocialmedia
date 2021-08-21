package com.example.chatpeki.Controller;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IYazmaDinleyici;
import com.example.chatpeki.View.SohbetActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GrupController {
    private FirebaseUser mevcutKullanici;
    private DatabaseReference kullanicilarReference;
    private SohbetActivity sohbetActivity;
    private DatabaseFacade DF = DatabaseFacade.get();

    private static  GrupController grupController;
    public static GrupController getInstance() {
        if(grupController==null){
            grupController=new GrupController();
        }
       return grupController;
    }

    public void grubuFirebaseEkle(final String grupAdi){
        String path = "Gruplar/"+grupAdi;
        DF.yaz().yaz(path, "", new IYazmaDinleyici() {
            @Override
            public void tamamlandi() {
                Toast.makeText(getSohbetActivity(), grupAdi+"adlı grup başarı ile oluşturuldu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public SohbetActivity getSohbetActivity() {
        return sohbetActivity;
    }

    public void setSohbetActivity(SohbetActivity sohbetActivity) {
        this.sohbetActivity = sohbetActivity;
    }
}
