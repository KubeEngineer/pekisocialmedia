package com.example.chatpeki.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.chatpeki.View.AnasayfaActivity;
import com.example.chatpeki.View.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.CompletableFuture;

public class LoginController {

    private DatabaseReference kullaniciYolu;
    private FirebaseAuth mYetki;
    private FirebaseUser mevcutKullanici;

    private Boolean testMode=false;
    private String mesaj;
    private LoginActivity loginActivity;
    //Singleton
    private static LoginController loginController;
    public static LoginController getInstance() {
        if (loginController==null){
            loginController=new LoginController();
        }
        return loginController;
    }//singletonn//
    public void  baslangic(){
        mYetki = FirebaseAuth.getInstance();
        mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
        kullaniciYolu = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        if (mevcutKullanici != null) {
            Intent login = new Intent(getLoginActivity(), AnasayfaActivity.class);
            getLoginActivity().startActivity(login);
        }
        getLoginActivity().progresGoster();

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void kullaniciGirisKontrolu(String Eposta, String Sifre){
        baslangic();
        final CompletableFuture<String> mesajs = new CompletableFuture<>();
        mYetki.signInWithEmailAndPassword(Eposta, Sifre)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String aktifKullaniciID = mYetki.getCurrentUser().getUid();
                            //bildirim için benzersiz ıd
                            String cihazToken = FirebaseInstanceId.getInstance().getToken();
                            //token numarasını kullanıcı bilgilerine ekleme
                            kullaniciYolu.child(aktifKullaniciID).child("cihaz_token").setValue(cihazToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    getLoginActivity().startLockTask();
                                    if (task.isSuccessful()) {
                                        mesajs.complete(mesaj);
                                        if (!testMode) {
                                            Intent login = new Intent(getLoginActivity(), AnasayfaActivity.class);
                                            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            getLoginActivity().startActivity(login);
                                            getLoginActivity().finish();
                                            Toast.makeText(getLoginActivity(), "Başarı ile giriş yapıldı", Toast.LENGTH_SHORT).show();
                                            getLoginActivity().progresBitir();
                                        }
                                    }
                                }
                            });
                        } else {
                            mesaj = "Hata:\n" + task.getException().toString() + "\nKullanıcı adı veya şifre yanlış!.";
                            mesajs.complete("Giris Basarisiz");
                            if(!testMode) {
                                Toast.makeText(getLoginActivity(), mesaj, Toast.LENGTH_SHORT).show();
                                getLoginActivity().progresBitir();
                            }
                        }

                    }
                });


    }


    public LoginActivity getLoginActivity() {
        return loginActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }
}
