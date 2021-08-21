package com.example.chatpeki.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.chatpeki.Adapter.arkadaslarAdapter;
import com.example.chatpeki.Controller.BenimArkadaslarimController;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BenimArkadaslarimActivity extends AppCompatActivity {

    private RecyclerView arkadaslariminListesi;
    private TextView arkadasSayisi;

    //singelton
    private BenimArkadaslarimController benimArkadaslarimController=BenimArkadaslarimController.getInstance();


    private FirebaseRecyclerAdapter adapter;
    private final KullaniciDurum durum = new KullaniciDurum();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benim_arkadaslarim);
        arkadaslariminListesi=findViewById(R.id.arkadaslarimin_listesi);
        arkadasSayisi=findViewById(R.id.ArkadaslarveSayilari);
        arkadaslariminListesi.setLayoutManager(new LinearLayoutManager(BenimArkadaslarimActivity.this));
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter=new arkadaslarAdapter(benimArkadaslarimController.arkadasListesi(),this,arkadasSayisi);
        arkadaslariminListesi.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
    @Override
    protected void onResume() {
        super.onResume();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
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
}
