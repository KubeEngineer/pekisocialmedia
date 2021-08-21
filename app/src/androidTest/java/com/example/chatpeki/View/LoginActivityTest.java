package com.example.chatpeki.View;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static android.support.constraint.Constraints.TAG;
import static org.junit.Assert.*;

public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class);
    private LoginActivity loginActivity=null;
    String[] ePostalar;
    String[] sifreler ;
    String[] dogruMesajlar;

    @Before
    public void setUp() throws Exception {
        loginActivity = activityTestRule.getActivity();
        //Olusabilecek tum durumlar ve bunlar icin geri donen dogru mesajlar
        ePostalar = new String[]{"bunyamin@hotmail.com", "aswd@hotmail.com", "", "aswd@hotmail.com"};
        sifreler = new String[]{"12345678", "123456", "123456", ""};
        dogruMesajlar = new String[]{"Giriş Başarılı", "Giris Basarisiz", "E-posta boş olmaz", "Şifre girmelisiniz."};

    }
    @Test
    public void kullaniciGirisKontrolu() throws ExecutionException, InterruptedException {
        String mesaj;


            for (int i = 0; i < ePostalar.length; i++) {
                FirebaseAuth.getInstance().signOut();
                mesaj = loginActivity.KullaniciGirisKontrolu(ePostalar[i], sifreler[i]);
                assertEquals(dogruMesajlar[i],mesaj);
                Log.d(TAG, "kullaniciGirisKontrolu: ");
            }

    }

    @After
    public void tearDown() throws Exception {
        loginActivity=null;
        dogruMesajlar=null;
        ePostalar=null;
        sifreler=null;
    }


}