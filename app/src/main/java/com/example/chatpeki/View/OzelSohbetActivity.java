package com.example.chatpeki.View;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatpeki.Adapter.mesajAdaptor;
import com.example.chatpeki.Controller.OzelSohbetController;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.Model.Mesajlar;
import com.example.chatpeki.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class OzelSohbetActivity extends AppCompatActivity {

    private String MesajiAliciId, adsoyadMesajiAlici, resim_mesaji_alici, kullanici_adiprofil;
    private TextView kullanici_adi, kullanici_adSoyad, kullaniciSonGorulmesi;
    private CircleImageView kullanici_profilResim;
    private ImageView geriyeDonme;
    private ImageButton gonder,dosya_gondermebtn;

    private EditText ozelMesajgirdisi;
    private String kontrol="";

    private Uri dosyaUri;
    private final KullaniciDurum durum = new KullaniciDurum();
    private final List<Mesajlar> mesajlarList = new ArrayList<>();
    private ProgressDialog yuklemeBar;

    private mesajAdaptor mesajAdaptor;
    private RecyclerView kullaniciMesajlariListesi;

    //singleton
    private OzelSohbetController ozelSohbetController=OzelSohbetController.getInstance();

    private void init() {
            viewIcinInit();
            fragmenttenBilgigetirme();
    }
    private void viewIcinInit(){
        kullanici_adi = findViewById(R.id.ozel_profil_kullanici_adi);
        kullanici_adSoyad = findViewById(R.id.ozel_profil_adi_soyad);
        kullanici_profilResim = findViewById(R.id.ozelProfilResmi);
        geriyeDonme = findViewById(R.id.geriyeDonme);
        ozelMesajgirdisi = findViewById(R.id.ozel_mesaj_girdisi);
        gonder = findViewById(R.id.mesajGonderme_butonu_sohbet);
        kullaniciSonGorulmesi = findViewById(R.id.kullanicisonGörülmeBilgisi);
        kullaniciMesajlariListesi = findViewById(R.id.kullanicilarin_ozel_mesajlari_listesi);
        dosya_gondermebtn=findViewById(R.id.dosyaGonderme_butonu_sohbet);
        yuklemeBar=new ProgressDialog(OzelSohbetActivity.this);
    }




    public void fragmenttenBilgigetirme(){
        if (getIntent().getExtras() != null) {
            MesajiAliciId = getIntent().getExtras().get("kullanici_id_ziyaret").toString();
            adsoyadMesajiAlici = getIntent().getExtras().get("kullanici_adiSoyadi_ziyaret").toString();
            resim_mesaji_alici = getIntent().getExtras().get("resim_ziyaret").toString();
            kullanici_adiprofil = getIntent().getExtras().get("kullanici_adi").toString();
            ozelSohbetController.setMesajiAliciId(MesajiAliciId);
            ozelSohbetController.setOzelSohbetActivity(OzelSohbetActivity.this);
        }
    }

    public  void baslangicAyarlamasi(){

     init();
     mesajAdaptor = new mesajAdaptor(mesajlarList);
     kullaniciMesajlariListesi.setAdapter(mesajAdaptor);
     kullaniciMesajlariListesi.setLayoutManager(new LinearLayoutManager(this));
     kullanici_adSoyad.setText(adsoyadMesajiAlici);
     kullanici_adi.setText(kullanici_adiprofil);
     Picasso.get().load(resim_mesaji_alici).placeholder(R.drawable.people).into(kullanici_profilResim);

     geriyeDonme.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent geriye = new Intent(OzelSohbetActivity.this, SohbetActivity.class);
             startActivity(geriye);
         }
     });

     gonder.setOnClickListener(new View.OnClickListener() {
         @RequiresApi(api = Build.VERSION_CODES.N)
         @Override
         public void onClick(View v) {
             try {
                 ozelSohbetController.mesajGonder(ozelMesajgirdisi.getText().toString());
             } catch (ExecutionException e) {
                 e.printStackTrace();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
     });
     dosya_gondermebtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             CharSequence secenekler[]=new CharSequence[]{"resim","pdf","word"};
             AlertDialog.Builder builder=new AlertDialog.Builder(OzelSohbetActivity.this);
             builder.setTitle("Dosya Seç");
             builder.setItems(secenekler, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     if(which==0){
                         kontrol="Resim";
                         Intent intent=new Intent();
                         intent.setAction(Intent.ACTION_GET_CONTENT);
                         intent.setType("image/*");
                         startActivityForResult(intent.createChooser(intent,"Resim seçin"),438);
                     }
                     if(which==1){
                         kontrol="pdf";
                         Intent intent=new Intent();
                         intent.setAction(Intent.ACTION_GET_CONTENT);
                         intent.setType("application/pdf");
                         startActivityForResult(intent.createChooser(intent,"Pdf seçin"),438);
                     }
                     if(which==2){
                         kontrol="docx";
                         Intent intent=new Intent();
                         intent.setAction(Intent.ACTION_GET_CONTENT);
                         intent.setType("application/msword");
                         startActivityForResult(intent.createChooser(intent,"Word dosyası seçin"),438);
                     }
                 }
             });
             builder.show();
         }
     });
     ozelSohbetController.SongorulmeyiGoster();
 }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ozel_sohbet);
        baslangicAyarlamasi();
    }
    public void scrollViewayarlama(){
        kullaniciMesajlariListesi.smoothScrollToPosition(kullaniciMesajlariListesi.getAdapter().getItemCount());
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        ozelSohbetController.veritabaniControl(mesajlarList, mesajAdaptor);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
           yuklemeBarGoruntule();
                dosyaUri=data.getData();
                if(!kontrol.equals("Resim")){
                    ozelSohbetController.resimDisindaGonderilenMesaj(dosyaUri,kontrol);
                }
                else if (kontrol.equals("Resim")){
                    ozelSohbetController.resimGondermeMesaji(dosyaUri,kontrol);
                }
                else{
                    yuklemeBardurdur();
                    Toast.makeText(this, "Hata:Oge seçilemedi!", Toast.LENGTH_SHORT).show();
                }
        }
    }
    public void cevrimIciYazma(){
        kullaniciSonGorulmesi.setText("Çevrimiçi");
    }
    public void cevrimDisiYazma(){
        kullaniciSonGorulmesi.setText("Çevrimdışı");
    }
    public void enSonDurumu(String tarih,String zaman){
        kullaniciSonGorulmesi.setText("Son görülme: " + tarih + " " + zaman);
    }

    public void mesajTemizle(){
        ozelMesajgirdisi.setText("");
    }

    public void yuklemeBarGoruntule(){
        yuklemeBar.setTitle("Dosya yükleniyor");
        yuklemeBar.setMessage("Lütfen bekleyin");
        yuklemeBar.setCanceledOnTouchOutside(false);
        yuklemeBar.show();
    }
    public void yuklemeBardurdur(){
        yuklemeBar.dismiss();
    }


}

