package com.example.chatpeki.View;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class OzelSohbetActivityTest {
    String d[] = {"dasdd", "asddsasd"};

    @Rule
    public ActivityTestRule<OzelSohbetActivity> activityTestRule = new ActivityTestRule<OzelSohbetActivity>(OzelSohbetActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent yeni = new Intent(targetContext, OzelSohbetActivity.class);
            yeni.putExtra("kullanici_id_ziyaret","Test_Id");
            yeni.putExtra("resim_ziyaret","Test Resim");
            yeni.putExtra("kullanici_adiSoyadi_ziyaret","Test Ad" +" "+"Test Soyad");
            yeni.putExtra("kullanici_adi","Test kullaniciAdi");
            return yeni;
        }
    };

    OzelSohbetActivity ozelSohbetActivity = null;
    String testMesaj = null;
    CompletableFuture<Boolean> test = new CompletableFuture<>();
    CompletableFuture<Boolean> test2 = new CompletableFuture<>();

    @Before
    public void setUp() throws Exception {
        testMesaj = "Test Mesaji";
        ozelSohbetActivity = activityTestRule.getActivity();
        Log.w("My Tag", "Bas");
    }

    @Test
    public void mesajGonder() throws ExecutionException, InterruptedException {
        try {
            assertEquals(ozelSohbetActivity.mesajGonder(""), false);
            assertEquals(ozelSohbetActivity.mesajGonder(testMesaj), true);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        Log.w("My Tag", "son");
    }


}