package com.example.chatpeki.Test;

import com.example.chatpeki.View.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginControlTest {
    private final String[] ePostalar = {"bunyamin@hotmail.com", "aswd@hotmail.com", "", "aswd@hotmail.com"};
    private final String[] sifreler = {"12345678", "123456", "123456", ""};
    private final String[] dogruMesajlar = {"Giris Basarili","Giris Basarisiz","E-posta boş olmaz", "Şifre girmelisiniz."};

    private Boolean Sonuc = false;
    private FirebaseUser firebaseUser;

    public LoginControlTest(LoginActivity loginActivity) {
        String mesaj;


        for (int i = 0; i < ePostalar.length; i++) {
            FirebaseAuth.getInstance().signOut();
            mesaj = "loginActivity.KullaniciGirisKontrolu(ePostalar[i], sifreler[i])";
            if (mesaj == null) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    mesaj ="Giris Basarili";
                } else {
                    mesaj ="Giris Basarisiz";
                }
            }
            if(dogruMesajlar[i].equals(mesaj)) {
                Sonuc = true;
            }else {
                Sonuc = false;
                mesajYaz(i+".Siradaki mesaj hatali---"+mesaj);
                break;
            }
        }

        if(Sonuc) {
            mesajYaz("Test Basari Ile Tamamlandi");
        }else {
            mesajYaz("Test Basarisiz Oldu");
        }
    }


    public void mesajYaz(String mesaj) {
        System.out.println("\n------------------------\n" + mesaj + "\n----------------------------------------------");
    }
}
