package com.example.chatpeki.View;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class yorumActivityTest {
    @Rule
    public ActivityTestRule<YorumActivity> activityTestRule = new ActivityTestRule<YorumActivity>(YorumActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent yeni = new Intent(targetContext, YorumActivity.class);
            yeni.putExtra("post_id","Test_Post_Id");
            yeni.putExtra("gonderen_id","Test_Gonderen_Id");

            return yeni;
        }
    };
    private YorumActivity yorumActivity=null;
    private String testYorum[]=null;
    private String beklenenMesaj[]=null;
    @Before
    public void setUp() throws Exception {
        yorumActivity = activityTestRule.getActivity();
        testYorum = new String[] {"","Test Yorum"};
        beklenenMesaj = new String[] {"Boş yorum gönderemezsiniz!","Yorum gönderildi!"};
    }
    @After
    public void tearDown() throws Exception {
        yorumActivity=null;
        testYorum = null;
        beklenenMesaj=null;
    }
    @Test
    public void yorumEkle() {
        for(int i = 0; i < testYorum.length ; i++) {
            String mesaj = yorumActivity.yorumEkle(testYorum[i]);
            assertEquals(beklenenMesaj[i],mesaj);
        }
    }
}