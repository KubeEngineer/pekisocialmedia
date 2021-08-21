package com.example.chatpeki.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.chatpeki.Controller.ProfilFragmenController;
import com.example.chatpeki.GenelDinleyiciler.IDinleyici;
import com.example.chatpeki.View.BenimArkadaslarimActivity;
import com.example.chatpeki.View.ProfilActivity;
import com.example.chatpeki.Adapter.adapterForPosts;
import com.example.chatpeki.R;
import com.example.chatpeki.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentProfil extends Fragment {
    private Button profilDuzenle;
    private TextView profildekiProfilIsmi;
    private CircleImageView kullaniciProfilResmi;
    private Button arkadaslarProfilButonu;
    private Button postlarProfilButonu;

    private RecyclerView postView;
    private adapterForPosts postAdapter;
    private  List<Post> postList;
    private ProgressDialog yukleniyorDialog;

    private DatabaseReference veriYolu;
    private String mevcutKullaniciId;


    private ProfilFragmenController profilFragmenController=ProfilFragmenController.getInstance();
    private View view;
    public FragmentProfil() {
    }
    public void init() {
        profildekiProfilIsmi=view.findViewById(R.id.profildekiProfilIsmi);
        postView = view.findViewById(R.id.profildekiPostList);
        profilDuzenle = view.findViewById(R.id.profilDuzenlemeButton);
        arkadaslarProfilButonu=view.findViewById(R.id.arkadaslarProfilButonu);
        postlarProfilButonu=view.findViewById(R.id.postlarProfilButonu);
        kullaniciProfilResmi =view.findViewById(R.id.profilResmi1);
        postView = view.findViewById(R.id.profildekiPostList);
        profilFragmenController.setFragmentProfil(FragmentProfil.this);
        mevcutKullaniciId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        veriYolu= FirebaseDatabase.getInstance().getReference();
        postList = new ArrayList<>();
        yukleniyorDialog = new ProgressDialog(getContext());
        progresGoster();
    }





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profil_fragment, container, false);
        init();
        postView.setHasFixedSize(true);
        postView.getRecycledViewPool().setMaxRecycledViews(0,0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postView.setLayoutManager(linearLayoutManager);

        profilDuzenle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profilDuzenleyeGit=new Intent(getActivity(), ProfilActivity.class);
                        startActivity(profilDuzenleyeGit);
                    }
                }
        );
        arkadaslarProfilButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent benimArkadaslarima=new Intent(getActivity(), BenimArkadaslarimActivity.class);
                startActivity(benimArkadaslarima);
            }
        });

        profilFragmenController.kullaniciBilgisiAl();
        profilFragmenController.arkadasSayisi();
        profilFragmenController.postlariGetir(new IDinleyici() {
            @Override
            public void tamamlandi(List list) {
                postAdapter = new adapterForPosts(getContext(),list,getFragmentManager());
                profilFragmenController.setPostAdapter(postAdapter);
                postView.setAdapter(postAdapter);
                postAdapter.notifyDataSetChanged();
                progresBitir();
            }
        });

        return view;
    }

    public void postSayisiView(List<Post> postList){
        this.postList = postList;
        postAdapter.notifyDataSetChanged();
        postlarProfilButonu.setText(postList.size()+"\n"+"Post");
    }

    public void arkadasSayisiViewAyar(int arkadas_sayisi){
        arkadaslarProfilButonu.setText(arkadas_sayisi+"\nArkadaş");
    }
    public  void profildekiKullaniciBilgisiResimli(String AdSoyad,String profilResmi){
        profildekiProfilIsmi.setText(AdSoyad);
        Picasso.get().load(profilResmi).into(kullaniciProfilResmi);
    }
    public  void profildekiKullaniciBilgisiResimsiz(String AdSoyad){
        profildekiProfilIsmi.setText(AdSoyad);
    }




    public void progresGoster(){
        yukleniyorDialog.setMessage("Lütfen bekleyin...");
        yukleniyorDialog.setCanceledOnTouchOutside(true);
        yukleniyorDialog.show();

    }
    public void progresBitir(){
        yukleniyorDialog.dismiss();
    }

}
