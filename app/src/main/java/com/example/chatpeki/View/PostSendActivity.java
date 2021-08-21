package com.example.chatpeki.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatpeki.Controller.PostController;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.concurrent.ExecutionException;

public class PostSendActivity extends AppCompatActivity {

    private FloatingActionButton postAddImageButton;
    private ImageView postImage;
    private ImageView postProfilImage;
    private TextView profilName;
    private android.support.v7.widget.Toolbar toolbar;
    private Button postGonderButton;
    private EditText durumICerikEditText;
    private final KullaniciDurum durum = new KullaniciDurum();

    private StorageTask yuklemeGorevi;
    private boolean testMode = true;
    private ProgressDialog yukleniyorDialog;
    private String gonderiId;
    private String durumIcerik;
    private String resim;
    private Uri resimUri;
    private PostController postController=new PostController();
    @SuppressLint("RestrictedApi")
    public void init() {
        postAddImageButton = findViewById(R.id.postAddImageButton);
        postImage = findViewById(R.id.postGondermeResmi);
        postProfilImage = findViewById(R.id.postProfilImageView);
        profilName = (TextView) findViewById(R.id.postProfilName);
        toolbar = findViewById(R.id.postToolBar);
        postGonderButton = findViewById(R.id.postGonderButton);
        durumICerikEditText = findViewById(R.id.postDurumIcerik);
        yukleniyorDialog = new ProgressDialog(PostSendActivity.this);
        if (getIntent().getExtras() != null) {
            resim = getIntent().getExtras().get("postResim").toString();
            gonderiId = getIntent().getExtras().get("postGonderiId").toString();
            durumIcerik = getIntent().getExtras().get("postDurumIcerik").toString();
            durumICerikEditText.setText(durumIcerik);
            postAddImageButton.setVisibility(View.GONE);
            postController.setGonderiId(gonderiId);
        } else {
            postAddImageButton.setVisibility(View.VISIBLE);
        }
        postController.setPostSendActivity(PostSendActivity.this);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_send);
        init();
       postController.kullaniciBilgisiGetirme();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        postAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Crop(Kırpma aktivity açma)
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(PostSendActivity.this);
            }
        });
        postGonderButton.setOnClickListener(
                new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(durumICerikEditText.getText().toString()) && gonderiId != null) {//durum icerigi bos olamaz
                            postController.setTestMode(false);
                            postController.postYukle(gonderiId, resim);
                        } else if (!durumICerikEditText.getText().toString().trim().isEmpty() && gonderiId == null) {
                            postController.setTestMode(false);
                            try {
                                postController.postYukle(durumICerikEditText.getText().toString());
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else
                            Toast.makeText(PostSendActivity.this, "Lütfen durumunuzu boş geçmeyin.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    public  void kullaniciProfilViewAyarlama(@NonNull DataSnapshot dataSnapshot){
        Kisiler kisi = dataSnapshot.getValue(Kisiler.class);
        Picasso.get().load(kisi.getResim()).placeholder(R.drawable.people).into(postProfilImage);
        profilName.setText(kisi.getAd() + " " + kisi.getSoyad());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //resim seçiliyorsa
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resimUri = result.getUri();
            postImage.setVisibility(View.VISIBLE);
            postImage.setImageURI(resimUri);
            postController.setResimUri(resimUri);
        } else {
            //resim seçilmiyorsa
            Toast.makeText(PostSendActivity.this, "Resim seçilemedi.", Toast.LENGTH_LONG).show();
        }

    }


    public String postIcerikViewAyar(){
       return durumICerikEditText.getText().toString();
    }
    public void progresGosterme(){
        yukleniyorDialog.setTitle("Bilgi Aktarma");
        yukleniyorDialog.setMessage("Lütfen bekleyin..");
        yukleniyorDialog.setCanceledOnTouchOutside(false);
        yukleniyorDialog.show();
    }
    public void progresBitir(){
        yukleniyorDialog.dismiss();
    }
    public void anasayfaYonlendirme(){
        Intent anaSayfa = new Intent(PostSendActivity.this, AnasayfaActivity.class);
        startActivity(anaSayfa);
    }



}

