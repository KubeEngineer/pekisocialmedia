package com.example.chatpeki.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatpeki.Adapter.aramaAdapter;
import com.example.chatpeki.Controller.AramaController;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AramaActivity extends AppCompatActivity {

    private EditText search;
    private ImageView aramaView;
    private AramaController aramaController=AramaController.GetInstance();
    private RecyclerView RecyclerListesi;
    private FirebaseRecyclerAdapter adapter;
    private final KullaniciDurum durum = new KullaniciDurum();

    public void init(){
        aramaController.setAramaActivity(AramaActivity.this);
        RecyclerListesi = (RecyclerView) findViewById(R.id.arkadasBulListesi);
        RecyclerListesi.setHasFixedSize(true);
        search=findViewById(R.id.search_bar);
        aramaView=findViewById(R.id.aramaview);
        RecyclerListesi.setLayoutManager(new LinearLayoutManager(AramaActivity.this));
        RecyclerListesi.getRecycledViewPool().setMaxRecycledViews(0,0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arama_aktivity);
        init();
        aramaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFonk(search.getText().toString());
            }
        });
    }
    private void searchFonk(String s) {
        aramaController.firebaseArama(s);
    }

    public class AramadaViewHolder extends RecyclerView.ViewHolder {
        public TextView adSoyad,kullaniciadi;
        public CircleImageView profilResmi;
        public ImageButton imageButton;

        public AramadaViewHolder(@NonNull View itemView) {
            super(itemView);
            adSoyad=itemView.findViewById(R.id.adSoyadProfil);
            kullaniciadi=itemView.findViewById(R.id.KullaniciAdiProfil);
            profilResmi=itemView.findViewById(R.id.kullanicilarprofilResmi);
            imageButton=itemView.findViewById(R.id.arkadasEkle);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
       kullaniciOku();
    }

    private void kullaniciOku(){
        aramaController.kullaniciOkuma();
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
    public RecyclerView getRecyclerListesi() {
        return RecyclerListesi;
    }
    public void setRecyclerListesi(RecyclerView recyclerListesi) {
        RecyclerListesi = recyclerListesi;
    }

    public FirebaseRecyclerAdapter getAdapter() {
        return adapter;
    }
    public void setAdapter(FirebaseRecyclerAdapter adapter) {
        this.adapter = adapter;
    }
}

