package com.example.chatpeki.View;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatpeki.Adapter.yorumAdapter;
import com.example.chatpeki.Controller.YorumController;
import com.example.chatpeki.GenelDinleyiciler.IDinleyici;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.Model.Yorum;
import com.example.chatpeki.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YorumActivity extends AppCompatActivity {
    private Toolbar yorumtoolbar;
    private RecyclerView yorumView;
    private EditText yorumText;
    private ImageView yorumGonderButon;
    private String post_id,post_resim,gonderen_id, alan_id;
    private Boolean testMode = true;
    private final KullaniciDurum durum = new KullaniciDurum();
    private yorumAdapter adapter;
    private YorumController yorumController=YorumController.getInstance();


    public void init() {
        yorumtoolbar = findViewById(R.id.yorumToolbar);
        yorumText    = findViewById(R.id.yorumIcerik);
        yorumView    = findViewById(R.id.yorumRecyclerView);
        yorumGonderButon    = findViewById(R.id.yorumGonder);
        Intent intent= getIntent();
        post_id     = intent.getStringExtra("post_id");
        gonderen_id = intent.getStringExtra("gonderen_id");
        alan_id = intent.getStringExtra("alan_id");
        post_resim  = intent.getStringExtra("post_resim");
        yorumController.setAlan_id(alan_id);
        yorumController.setGonderen_id(gonderen_id);
        yorumController.setPost_id(post_id);
        yorumController.setPost_resim(post_resim);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yorum);
        init();
        yorumView.getRecycledViewPool().setMaxRecycledViews(0,0);
        yorumView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        yorumView.setLayoutManager(linearLayoutManager);
        yorumlariGetir(this);
        ////////////////////////
        setSupportActionBar(yorumtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        yorumtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        yorumGonderButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMode = false;
                String yorum=yorumText.getText().toString();
                yorumEkle(yorum);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser() !=null) {
            durum.KullaniciDurumuGuncelle("çevrimiçi");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            durum.KullaniciDurumuGuncelle("çevrimdişi");
        }
    }
    public String yorumEkle(String yorum) {
        if(yorum.trim().isEmpty()) {
            String mesaj = "Boş yorum gönderemezsiniz!";
            if(!testMode)
                Toast.makeText(YorumActivity.this, "Boş yorum gönderemezsiniz!", Toast.LENGTH_SHORT).show();
            return mesaj;
        }else {
            String mesaj = "Yorum gönderildi!";
            yorumController.yorumEkle(yorum, yorumView);
            yorumText.setText("");
            return mesaj;
        }
    }
    private void yorumlariGetir(final Context context) {
        yorumController.yorumlariGetir(new IDinleyici<Yorum>() {
            @Override
            public void tamamlandi(List<Yorum> list) {
                adapter = new yorumAdapter(context,list);
                yorumView.setAdapter(adapter);
            }
        });
    }
}
