package com.example.chatpeki.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatpeki.Controller.KayitolController;
import com.example.chatpeki.R;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class Kayit_olActivity extends AppCompatActivity {
    private Button kayitolButonu;
    private TextView hesabimZatenvar;
    private Boolean testMode = true;
    private EditText ad;
    private EditText soyad;
    private EditText eposta;
    private EditText kullanici_adi;
    private EditText sifre;
    private ProgressDialog yukleniyorDialog;

    //singleton
    KayitolController kayitolController=KayitolController.getInstance();;
    //singleton

    public void init() {
        kayitolButonu = findViewById(R.id.kayit_Oll);
        hesabimZatenvar = findViewById(R.id.hesabimZatenvar);
        ad = findViewById(R.id.ad);
        soyad = findViewById(R.id.soyad);
        eposta = findViewById(R.id.eposta);
        kullanici_adi = findViewById(R.id.kullanici);
        sifre = findViewById(R.id.sifree);
        yukleniyorDialog = new ProgressDialog(Kayit_olActivity.this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);
        init();
        kayitolController.setKayitolAktivity(Kayit_olActivity.this);
        kayitolButonu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String name = ad.getText().toString();
                String surName = soyad.getText().toString();
                String e_posta = eposta.getText().toString();
                String kullanici = kullanici_adi.getText().toString();
                String sifree = sifre.getText().toString();
                testMode = false;
                progresGoster();
                yeniHesapOlustur(name, surName, e_posta, kullanici, sifree);
            }
        });
        hesabimZatenvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kayit_ol = new Intent(Kayit_olActivity.this, LoginActivity.class);
                startActivity(kayit_ol);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String yeniHesapOlustur(final String name, final String surName, final String e_posta, final String kullanici, final String sifree) {
        String mesaj;
        final CompletableFuture<String> dMesaj = new CompletableFuture<>();
        if (TextUtils.isEmpty(name)) {
            mesaj = "Ad kısmı boş olmaz";
            if (!testMode)
                Toast.makeText(this, mesaj, Toast.LENGTH_SHORT).show();
            return mesaj;
        } else if (TextUtils.isEmpty(surName)) {
            mesaj = "soyad kısmı boş olmaz";
            if (!testMode)
                Toast.makeText(this, mesaj, Toast.LENGTH_SHORT).show();
            return mesaj;
        } else if (TextUtils.isEmpty(e_posta)) {
            mesaj = "E-posta boş olmaz";
            if (!testMode)
                Toast.makeText(this, mesaj, Toast.LENGTH_SHORT).show();
            return mesaj;
        } else if (TextUtils.isEmpty(kullanici)) {
            mesaj = "Kullanıcı adı boş olmaz";
            if (!testMode)
                Toast.makeText(this, mesaj, Toast.LENGTH_SHORT).show();
            return mesaj;
        } else if (TextUtils.isEmpty(sifree)) {
            mesaj = "Şifre girmelisiniz.";
            if (!testMode)
                Toast.makeText(this, mesaj, Toast.LENGTH_SHORT).show();
            return mesaj;
        } else {


            kayitolController.yeniHesapOlustur(name, surName, e_posta, kullanici, sifree);
        }
        if(testMode) {
            try {
                return dMesaj.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    public void progresGoster(){
        yukleniyorDialog.setTitle("Yeni hesap oluşuruluyor");
        yukleniyorDialog.setMessage("Lütfen bekleyin...");
        yukleniyorDialog.setCanceledOnTouchOutside(true);
        yukleniyorDialog.show();

    }
    public void progresBitir(){
        yukleniyorDialog.dismiss();
    }

}