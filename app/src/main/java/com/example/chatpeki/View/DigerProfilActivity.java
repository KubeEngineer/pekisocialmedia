package com.example.chatpeki.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chatpeki.Adapter.adapterForPosts;
import com.example.chatpeki.Controller.DigerProfilController;
import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.Model.Post;
import com.example.chatpeki.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DigerProfilActivity extends AppCompatActivity {
    private String alinanKullaniciid, aktif_Durum, aktifKullanicid;
    private CircleImageView digerKullaniciResmi;
    private RecyclerView digerKullanıcıPostList;
    private TextView DigerprofildekiProfilIsmi;
    private Button IstekAtmaButton;
    private Button DigerArkadaslarProfil;
    private Button DigerpostlarProfil;
    private Button IstekDegerlendirmeButton;
    private final KullaniciDurum durum = new KullaniciDurum();
    //singleton
    private DigerProfilController digerProfilController=DigerProfilController.GetInstance();


    private void init() {
        alinanKullaniciid = getIntent().getExtras().get("tiklananKullanici_idGoster").toString();
        digerProfilController.setAlinanKullaniciid(alinanKullaniciid);
        digerProfilController.setDigerProfilActivity(DigerProfilActivity.this);
        aktif_Durum = "yeni";
        ViewIcinInit();
        aktifKullanicid = DatabaseFacade.UId();

    }

    private void ViewIcinInit(){
        digerKullaniciResmi = findViewById(R.id.DigerprofilResmi1);
        DigerArkadaslarProfil = findViewById(R.id.DigerarkadaslarProfilButon);
        DigerpostlarProfil = findViewById(R.id.DigerpostlarProfilButon);
        DigerprofildekiProfilIsmi = findViewById(R.id.DigerprofildekiProfilIsmi);
        digerKullanıcıPostList = findViewById(R.id.DigerprofildekiPostList);
        IstekAtmaButton = findViewById(R.id.IstekAtmaButton);
        IstekDegerlendirmeButton = findViewById(R.id.IstekDegerlendirmeButonu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diger_profil);
        init();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DigerProfilActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        digerKullanıcıPostList.setHasFixedSize(true);
        digerKullanıcıPostList.setLayoutManager(linearLayoutManager);
        digerProfilController.KullaniciBilgisiAl();
        digerProfilController.postlariGetir();
        digerProfilController.arkadasSayisi();
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

    public void talepDurumuGonderildiViewAyari(){

        aktif_Durum = "talep_gönderildi";
        IstekAtmaButton.setVisibility(View.VISIBLE);
        IstekAtmaButton.setText("Arkadaşlık isteğini iptal et.");

    }
    public void talepDurumuAlindiViewAyari(){
        aktif_Durum = "talep_alindi";
        IstekDegerlendirmeButton.setVisibility(View.VISIBLE);
        IstekDegerlendirmeButton.setEnabled(true);
        IstekAtmaButton.setText("Arkadaşlık isteğini kabul et.");
        IstekDegerlendirmeButton.setText("Arkadaşlık isteğini iptal et.");
        IstekDegerlendirmeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                digerProfilController.ArkadaslikIstekIptal();
                IstekDegerlendirmeButton.setVisibility(View.GONE);
            }
        });
    }
    public void arkadasDurumu(){
        IstekAtmaButton.setEnabled(true);
        aktif_Durum = "arkadaşlar";
        IstekAtmaButton.setText("Arkadaşlıktan çıkar.");
        digerKullanıcıPostList.setVisibility(View.VISIBLE);
        IstekDegerlendirmeButton.setVisibility(View.INVISIBLE);
        IstekDegerlendirmeButton.setEnabled(false);
    }
    private void arkadaslikTalepleriniYonet() {
        digerProfilController.arkadaslikYonet();
        if (alinanKullaniciid.equals(aktifKullanicid)) {
            IstekAtmaButton.setVisibility(View.INVISIBLE);
        } else {
            IstekAtmaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aktif_Durum.equals("yeni")) {
                        digerProfilController.arkadaslikIstegiGonder();
                    }
                    if (aktif_Durum.equals("talep_gönderildi")) {
                        digerProfilController.ArkadaslikIstekIptal();
                    }
                    if (aktif_Durum.equals("talep_alindi")) {
                        digerProfilController.ArkadaslikIstekKabul();
                    }
                    if (aktif_Durum.equals("arkadaşlar")) {
                        digerProfilController.veriTabaniKontrolArkadasliktanCıkart();
                    }
                }
            });

        }


    }

    public void veriTabanindakileriVieweAktarResimli(String kullaniciResmi,String kullaniciAdi,String kullaniciSoyadi){
        Picasso.get().load(kullaniciResmi).placeholder(R.drawable.people).into(digerKullaniciResmi);
        DigerprofildekiProfilIsmi.setText(kullaniciAdi + " " + kullaniciSoyadi);
        arkadaslikTalepleriniYonet();
    }
    public void veriTabanindakileriVieweAktarResimsiz(String kullaniciAdi,String kullaniciSoyadi){
        DigerprofildekiProfilIsmi.setText(kullaniciAdi + " " + kullaniciSoyadi);
        arkadaslikTalepleriniYonet();
    }


    public void arkadasliktanCikartinViewAyari(){
        IstekAtmaButton.setEnabled(true);
        aktif_Durum = "yeni";
        IstekAtmaButton.setText("Arkadaşlık istegi gönder.");

    }

    public void arkadasIstekKabulViewAyari(){
        IstekAtmaButton.setEnabled(true);
        aktif_Durum = "arkadaşlar";
        digerKullanıcıPostList.setVisibility(View.VISIBLE);
        IstekAtmaButton.setText("Arkadaşlıktan çıkar.");
        IstekDegerlendirmeButton.setVisibility(View.GONE);
        IstekDegerlendirmeButton.setEnabled(false);
    }

    public void arkadasIstekIptalViewAyari(){
        IstekAtmaButton.setEnabled(true);
        aktif_Durum = "yeni";
        IstekAtmaButton.setText("Arkadaşlık istegi gönder.");
        IstekDegerlendirmeButton.setVisibility(View.GONE);
        IstekDegerlendirmeButton.setEnabled(false);
    }


    public void arkadasIstegiGonderViewAyarlama(){
        IstekAtmaButton.setEnabled(true);
        aktif_Durum = "talep_gönderildi";
        IstekAtmaButton.setText("Arkadaşlık isteği iptal.");
    }


    public void postlariListesiDondur(adapterForPosts postAdapter){
        digerKullanıcıPostList.setAdapter(postAdapter);
    }

    public void butonaGoreArkadasKontrolu(){
        if(!IstekAtmaButton.getText().toString().equals("Arkadaşlıktan çıkar."))
            digerKullanıcıPostList.setVisibility(View.GONE);
        else
            digerKullanıcıPostList.setVisibility(View.VISIBLE);
    }

    public void digerProfilArkadasSayisiViewAyar(int arkadas_sayisi){
        DigerArkadaslarProfil.setText(arkadas_sayisi+"\nArkadaş");
    }
    public void digerProfilPostSayisiViewAyari(List<Post> postList){
        DigerpostlarProfil.setText(postList.size()+"\nPost");
    }

}
