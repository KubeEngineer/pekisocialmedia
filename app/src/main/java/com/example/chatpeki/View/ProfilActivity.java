package com.example.chatpeki.View;


import android.app.ProgressDialog;

import android.content.Intent;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatpeki.Controller.ProfilController;
import com.example.chatpeki.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends AppCompatActivity {
    private Button profil_bilgilerini_güncelle;
    private EditText kullanici_adi_güncelle, sifre_guncelle, ad_guncelle ,soyad_guncelle;
    private TextView eposta_güncelle;
    private CircleImageView kullanıcıProfilResmi;
    private ProgressDialog yukleniyorDialog;


    private boolean resim_secme=false;
    //singleton
    private ProfilController profilController=ProfilController.getInstance();
    Uri resimUri;

    public  void init() {
        profil_bilgilerini_güncelle = findViewById(R.id.profil_kaydet);
        kullanici_adi_güncelle = findViewById(R.id.kullanici_adi_guncelle);
        eposta_güncelle = findViewById(R.id.eposta_guncelle);
        sifre_guncelle = findViewById(R.id.sifre_guncelle);
        ad_guncelle = findViewById(R.id.ad_guncelle);
        soyad_guncelle = findViewById(R.id.soyad_guncelle);
        kullanıcıProfilResmi = findViewById(R.id.profile_resmi_ayarla);
        profilController.setProfilActivity(ProfilActivity.this);
        yukleniyorDialog=new ProgressDialog(ProfilActivity.this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        init();
        kullanıcıProfilResmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Crop(Kırpma aktivity açma
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(ProfilActivity.this);
            }
        });
        profilController.kullaniciBilgisiAl();
        profil_bilgilerini_güncelle.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AyarlariGuncelle();
                    }
                }
        );
    }
    private void AyarlariGuncelle() {
        String adDegis = ad_guncelle.getText().toString();
        String soyadDegis = soyad_guncelle.getText().toString();
        String kullaniciadiniDegis = kullanici_adi_güncelle.getText().toString();
        String epostaniDegis = eposta_güncelle.getText().toString();
        String sifreniDegis = sifre_guncelle.getText().toString();
        if (TextUtils.isEmpty(adDegis)) {
            Toast.makeText(ProfilActivity.this, "Lütfen adınızı yazınız!", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(soyadDegis)) {
            Toast.makeText(ProfilActivity.this, "Lütfen soyadınızı yazınız!", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(kullaniciadiniDegis)) {
            Toast.makeText(ProfilActivity.this, "Lütfen kullanıcı adınızı yazınız!", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(sifreniDegis)) {
            Toast.makeText(ProfilActivity.this, "Lütfen şifrenizi yazınız!", Toast.LENGTH_LONG).show();

        } if (resim_secme){

            profilController.veriTabaninaResimYukle(adDegis,soyadDegis,kullaniciadiniDegis,epostaniDegis,sifreniDegis);
        }
        else {
            Toast.makeText(ProfilActivity.this, "Lütfen Resim Seçiniz.", Toast.LENGTH_LONG).show();

        }


    }

    public void progresGoster(){
        yukleniyorDialog.setTitle("Bilgi Aktarma");
        yukleniyorDialog.setMessage("Lütfen bekleyin..");
        yukleniyorDialog.setCanceledOnTouchOutside(false);
        yukleniyorDialog.show();
    }
    public  void progressDurdur(){
        yukleniyorDialog.dismiss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK) {
            //resim seçiliyorsa
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            resimUri=result.getUri();
            kullanıcıProfilResmi.setImageURI(resimUri);
            resim_secme=true;
            profilController.setResimUri(resimUri);
        }
        else {
            //resim seçilmiyorsa
            Toast.makeText(this, "Resim seçilemedi.", Toast.LENGTH_LONG).show();
        }
    }
    public void kullaniciBilgisiniYansitResimli(String adAl, String soyadAl, String epostaAl, String kullaniciadiAl, String sifreAl, String profilResmiAl){
        kullanici_adi_güncelle.setText(kullaniciadiAl);
        eposta_güncelle.setText(epostaAl);
        sifre_guncelle.setText(sifreAl);
        ad_guncelle.setText(adAl);
        soyad_guncelle.setText(soyadAl);
        Picasso.get().load(profilResmiAl).placeholder(R.drawable.people).into(kullanıcıProfilResmi);
    }
    public void kullaniciBilgisiniYansitResimsiz(String adAl, String soyadAl, String epostaAl, String kullaniciadiAl, String sifreAl){
        ad_guncelle.setText(adAl);
        soyad_guncelle.setText(soyadAl);
        kullanici_adi_güncelle.setText(kullaniciadiAl);
        eposta_güncelle.setText(epostaAl);
        sifre_guncelle.setText(sifreAl);
    }

}
