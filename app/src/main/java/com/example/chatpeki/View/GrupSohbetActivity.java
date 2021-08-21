package com.example.chatpeki.View;

import android.content.Intent;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatpeki.Controller.GrupSohbetController;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.R;
import com.google.firebase.auth.FirebaseAuth;


public class GrupSohbetActivity extends AppCompatActivity {
    private ImageButton mesajGonderButtonu;
    private Toolbar mToolbar;
    private EditText kullanici_mesaji_girdisi;
    private LinearLayout mesaj;
    private ScrollView scrollView;
    private TextView mesaj_gonderen, mesaj_icerik, mesaj_tarih, grup_baslik;

    private String mevcutGrupadi;
    private final KullaniciDurum durum = new KullaniciDurum();
    //singleton
    private GrupSohbetController grupSohbetController=GrupSohbetController.getInstance();
    public void init() {
        grupSohbetController.setGrupSohbetActivity(GrupSohbetActivity.this);
        mToolbar = findViewById(R.id.grup_chat_bar_layout1);
        grup_baslik = findViewById(R.id.grup_baslik);
        mesajGonderButtonu = findViewById(R.id.mesajGonderme_butonu);
        kullanici_mesaji_girdisi = findViewById(R.id.grup_girdi_mesaji);
        mesaj = findViewById(R.id.grup_chat_bar_layout);
        scrollView = findViewById(R.id.my_scrolView);
        mevcutGrupadi = getIntent().getExtras().get("GrupAdi").toString();
        grupSohbetController.setMevcutGrupadi(mevcutGrupadi);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_sohbet);
        init();
        grup_baslik.setText(mevcutGrupadi);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        kullaniciBilgisiAl();
        Toast.makeText(this, mevcutGrupadi, Toast.LENGTH_LONG).show();
        mesajGonderButtonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mesajiGonder();
                kullanici_mesaji_girdisi.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        grupSohbetController.veriKontrolEtme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            durum.KullaniciDurumuGuncelle("çevrimiçi");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            durum.KullaniciDurumuGuncelle("çevrimdişi");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent geriDon = new Intent(GrupSohbetActivity.this, SohbetActivity.class);
                NavUtils.navigateUpTo(this, geriDon);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public LayoutInflater  layoutInflater(){
        LayoutInflater layoutInflater = getLayoutInflater();
        return layoutInflater;
    }
    public  void scrollViewGoster(){
        scrollView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }
        );
    }
    public void mesajGondereniYansitma(LayoutInflater layoutInflater,String sohbetmesaji,String sohbetzamani){
        View yeniMesaj = layoutInflater.inflate(R.layout.mesaj_row_kullanici, mesaj, false);
        mesaj_icerik = yeniMesaj.findViewById(R.id.mesaj_icerik);
        mesaj_tarih = yeniMesaj.findViewById(R.id.mesaj_tarihi);
        mesaj_icerik.setText(sohbetmesaji);
        mesaj_tarih.setText(/*sohbettarihi+"\n"+*/sohbetzamani);
        mesaj.addView(yeniMesaj);
    }
    public  void mesajAlaniYansitma(LayoutInflater layoutInflater,String sohbetmesaji,String sohbetzamani,String sohbetadi,String sohbetSoyadi){
        View yeniMesaj = layoutInflater.inflate(R.layout.mesaj_row_diger_kullanici, mesaj, false);
        mesaj_icerik = yeniMesaj.findViewById(R.id.mesaj_icerik);
        mesaj_tarih = yeniMesaj.findViewById(R.id.mesaj_tarihi);
        mesaj_gonderen = yeniMesaj.findViewById(R.id.mesaj_gonderen);
        mesaj_gonderen.setText(sohbetadi + " " + sohbetSoyadi);
        mesaj_icerik.setText(sohbetmesaji);
        mesaj_tarih.setText(/*sohbettarihi+"\n"+*/sohbetzamani);
        mesaj.addView(yeniMesaj);

    }

    private void mesajiGonder() {
        String mesaj = kullanici_mesaji_girdisi.getText().toString();
        grupSohbetController.mesajiGonder(mesaj);
    }
    private void kullaniciBilgisiAl() {
       grupSohbetController.veritabanindanKullaniciBilgisiAl();
    }
}
