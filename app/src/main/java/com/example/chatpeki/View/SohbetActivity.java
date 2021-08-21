package com.example.chatpeki.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatpeki.Controller.GrupController;
import com.example.chatpeki.Fragment.GruplarFragment;
import com.example.chatpeki.Fragment.SohbetlerFragment;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SohbetActivity extends AppCompatActivity {


    private final KullaniciDurum durum = new KullaniciDurum();

    private BottomNavigationView bottomNavigationView;
    private GrupController grupController=GrupController.getInstance();

    public void init(){
        bottomNavigationView = findViewById(R.id.bottom_bar1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sohbet);
        init();
        grupController.setSohbetActivity(SohbetActivity.this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container2,new SohbetlerFragment()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sohbet_menu,menu);
        return true;

    }

    //Bottom Bar Menu Item Selected alt bardaki itemler secildiginde getirilecek fragment
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.mesajlaragit:
                            invalidateOptionsMenu();
                            selectedFragment = new SohbetlerFragment();//fragment java nesnesi olusturmak yeterli
                            break;
                        case R.id.gruplaraGit:
                            invalidateOptionsMenu();
                            selectedFragment = new GruplarFragment();//fragment java nesnesi olusturmak yeterli

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container2,selectedFragment).commit();
                    return true;
                }
            };


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                Intent geriDon=new Intent(SohbetActivity.this, AnasayfaActivity.class);
                NavUtils.navigateUpTo(this,geriDon);
                return true;
            case R.id.grupOlustur:
                yeniGrupKur();
                return true;

            case R.id.cikisyap:
                Intent cikis_yapmaya=new Intent(SohbetActivity.this,MainActivity.class);
                startActivity(cikis_yapmaya);
                FirebaseAuth.getInstance().signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void yeniGrupKur() {
        AlertDialog.Builder builder=new AlertDialog.Builder(SohbetActivity.this,R.style.AlertDialog);
        builder.setTitle("Grup Adi Girin:");
        final EditText grupAdiAlani=new EditText(SohbetActivity.this);
        grupAdiAlani.setHint("Örnek:Corona Virüsü");
        builder.setView(grupAdiAlani);
        builder.setPositiveButton("Oluştur", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            String grupAdi=grupAdiAlani.getText().toString();
            if(TextUtils.isEmpty(grupAdi)){
                Toast.makeText(SohbetActivity.this,"Grup adı boş bırakılmaz!",Toast.LENGTH_LONG).show();
            }
            else
            {
                grupKaydet(grupAdi);
            }
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });

    builder.show();
    }

    private void grupKaydet(String grupAdi) {
     grupController.grubuFirebaseEkle(grupAdi);
    }

}
