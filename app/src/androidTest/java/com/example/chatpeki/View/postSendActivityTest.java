package com.example.chatpeki.View;

import android.support.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class postSendActivityTest {
    @Rule
    public ActivityTestRule<PostSendActivity> post_sendActivityTestRule =  new ActivityTestRule<PostSendActivity>(PostSendActivity.class);;
    PostSendActivity postSend=null;
    FirebaseUser user ;
    String durumIcerik;

    @Before
    public void setUp() throws Exception {
        durumIcerik="Test Icerik";
    }

    @Test
    public void postYukle() throws ExecutionException, InterruptedException {
        postSend = post_sendActivityTestRule.getActivity();
        try {
            assertEquals(postSend.postYukle(durumIcerik),true);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {
        postSend=null;
        user =null;
        durumIcerik = null;
    }
}