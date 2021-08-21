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

import com.example.chatpeki.Controller.LoginController;
import com.example.chatpeki.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private Button girisyapButonu;
    private TextView kayit;
    private TextView sifremiunuttum;
    private EditText e_posta;
    private EditText sifre;
    private String mesaj;
    private Boolean testMode;
    private FirebaseUser mevcutKullanici;
    private ProgressDialog yukleniyorDialog;

    //Singleton
    private LoginController loginController=LoginController.getInstance();

    public void init() {
        testMode=false;
        girisyapButonu =findViewById(R.id.girisyap);
        kayit = findViewById(R.id.kayit_sayfasi);
        sifremiunuttum = findViewById(R.id.sifremiunuttum);
        e_posta = findViewById(R.id.epostaText);
        sifre = findViewById(R.id.sifreText);
        mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
        loginController.setLoginActivity(LoginActivity.this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        sifremiunuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sifremiunuttum = new Intent(LoginActivity.this, AnasayfaActivity.class);
                startActivity(sifremiunuttum);
            }
        });
        girisyapButonu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String ePosta = e_posta.getText().toString();
                String sifree = sifre.getText().toString();
                yukleniyorDialog = new ProgressDialog(LoginActivity.this);
                testMode = false;

                try {
                    KullaniciGirisKontrolu(ePosta, sifree);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String KullaniciGirisKontrolu(String ePosta, String sifre) throws ExecutionException, InterruptedException {
        final CompletableFuture<String> mesajs = new CompletableFuture<>();
        String epostaa = ePosta;
        String sifree = sifre;
        if (TextUtils.isEmpty(epostaa)) {
            mesaj = "E-posta boş olmaz";
            mesajs.complete(mesaj);
            if(!testMode)
                Toast.makeText(LoginActivity.this, mesaj, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sifree)) {
            mesaj = "Şifre girmelisiniz.";
            mesajs.complete(mesaj);
            if(!testMode)
                Toast.makeText(LoginActivity.this, mesaj, Toast.LENGTH_SHORT).show();
        } else {
            loginController.kullaniciGirisKontrolu(epostaa,sifree);
        }
        if(testMode)
            return mesajs.get();
        else
            return "";
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mevcutKullanici != null) {
            kullaniciyiAnasayfayaGonder();
        }
    }
    private void kullaniciyiAnasayfayaGonder() {
        Intent anasayfayaIntent = new Intent(LoginActivity.this, AnasayfaActivity.class);
        startActivity(anasayfayaIntent);
    }

    public void progresGoster(){
        yukleniyorDialog.setTitle("Giriş yapılıyor");
        yukleniyorDialog.setMessage("Lütfen bekleyin...");
        yukleniyorDialog.setCanceledOnTouchOutside(true);
        yukleniyorDialog.show();

    }
    public void progresBitir(){
        yukleniyorDialog.dismiss();
    }


}
