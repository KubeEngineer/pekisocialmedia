package com.example.chatpeki.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.chatpeki.Fragment.FragmentAnaSayfa;
import com.example.chatpeki.Fragment.FragmentBildirimler;
import com.example.chatpeki.Fragment.FragmentProfil;
import com.example.chatpeki.Model.KullaniciDurum;
import com.example.chatpeki.R;
import com.google.firebase.auth.FirebaseAuth;
public class AnasayfaActivity extends AppCompatActivity {
    private int topMenu=R.menu.top_menu;
    private BottomNavigationView bottomNavigationView;
    private final KullaniciDurum durum = new KullaniciDurum();
    public void init(){
        bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container1,new FragmentAnaSayfa()).commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anasayfa);
        init();
    }
    //Bottom Bar Menu Item Selected alt bardaki itemler secildiginde getirilecek fragment
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_mainPage:
                            topMenu=R.menu.top_menu;
                            invalidateOptionsMenu();
                            selectedFragment = new FragmentAnaSayfa();//fragment java nesnesi olusturmak yeterli
                            break;
                        case R.id.nav_ara:
                            topMenu=R.menu.top_menu;
                            invalidateOptionsMenu();
                            Intent aramaya = new Intent(AnasayfaActivity.this, AramaActivity.class);
                            startActivity(aramaya);//fragment java nesnesi olusturmak yeterli
                            return true;
                        case R.id.nav_add:
                            Intent postSend = new Intent(AnasayfaActivity.this, PostSendActivity.class);
                            startActivity(postSend);//fragment java nesnesi olusturmak yeterli
                            return true;
                        case R.id.nav_notifications:
                            topMenu=R.menu.top_menu;
                            invalidateOptionsMenu();
                            selectedFragment = new FragmentBildirimler();//fragment java nesnesi olusturmak yeterli
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new FragmentProfil();
                            topMenu=R.menu.profil_menu;
                            invalidateOptionsMenu();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container1,selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(topMenu,menu);
        return true;
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_message:
                Intent mesaja=new Intent(AnasayfaActivity.this, SohbetActivity.class);
                startActivity(mesaja);
                return true;
            case R.id.cikis_yap:
                durum.KullaniciDurumuGuncelle("çevrimdişi");
                FirebaseAuth.getInstance().signOut();
                Intent toLogin=new Intent(AnasayfaActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toLogin);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }






}
