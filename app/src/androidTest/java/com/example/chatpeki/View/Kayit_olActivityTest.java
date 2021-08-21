package com.example.chatpeki.View;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class Kayit_olActivityTest {
    @Rule
    public ActivityTestRule<Kayit_olActivity> activityTestRule = new ActivityTestRule<>(Kayit_olActivity.class);
    private Kayit_olActivity kayit_olActivity = null;
    String[] adlar;
    String[] soyAdlar;
    String[] ePostalar;
    String[] kullaniciAdlar;
    String[] sifreler;
    String[] dogruMesajlar;

    @Before
    public void setUp() throws Exception {
        adlar = new String[]{"", "Test Ad", "Test Ad", "Test Ad", "Test Ad", "Test Ad", "Test Ad","Test Ad"};
        soyAdlar = new String[]{"Test Soyad", "", "Test Soyad", "Test Soyad", "Test Soyad", "Test Soyad", "Test Soyad","Test Soyad"};
        ePostalar = new String[]{"test1@posta.com", "test1@posta.com", "", "test1@posta.com", "test1@posta.com", "test1@posta.com", "testposta.com","test1@posta.com"};
        kullaniciAdlar = new String[]{"Test_kullanici", "Test_kullanici", "Test_kullanici", "", "Test_kullanici", "Test_kullanici", "Test_kullanici","Test_kullanici"};
        sifreler = new String[]{"123456", "123456", "123456", "123456", "", "12345", "123456","123456"};
        dogruMesajlar = new String[]{"Ad kısmı boş olmaz", "soyad kısmı boş olmaz", "E-posta boş olmaz", "Kullanıcı adı boş olmaz",
                "Şifre girmelisiniz.", "Bilgilerinizi kontrol ediniz.", "Bilgilerinizi kontrol ediniz.","Basari ile kayit olundu"
        };
        kayit_olActivity = activityTestRule.getActivity();
    }

    @Test
    public void yeniHesapOlustur() {
        String mesaj;


        for (int i = 0; i < ePostalar.length; i++) {
            FirebaseAuth.getInstance().signOut();
            mesaj = kayit_olActivity.yeniHesapOlustur(adlar[i], soyAdlar[i], ePostalar[i], kullaniciAdlar[i], sifreler[i]);
            assertEquals(dogruMesajlar[i], mesaj);
            Log.d("My Tag", (i+1)+".mesaj "+mesaj);
        }
    }

    @After
    public void tearDown() throws Exception {
        adlar = null;
        soyAdlar = null;
        ePostalar = null;
        kullaniciAdlar = null;
        sifreler = null;
        dogruMesajlar = null;
        kayit_olActivity = null;
    }


}