package com.example.chatpeki.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatpeki.Adapter.BildirimAdapter;
import com.example.chatpeki.Adapter.arkadasIstekAdapter;
import com.example.chatpeki.Controller.BildirimlerController;
import com.example.chatpeki.Model.Bildirimler;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FragmentBildirimler extends Fragment {
    private RecyclerView IsteklerRecyclerListesi;
    private RecyclerView BildirimlerRecyclerListesi;
    private TextView arkadasIstekleriText,bildirimText;
    private BildirimlerController bildirimlerController=BildirimlerController.getInstance();
    private BildirimAdapter bildirimAdapter;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference bildirimYolu;
    private List<Bildirimler> bildirimler;

    View view;
    public FragmentBildirimler() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_bildirimler, container, false);
        IsteklerRecyclerListesi=view.findViewById(R.id.arkadasIstekListesi);
        BildirimlerRecyclerListesi=view.findViewById(R.id.bildirimListesi);
        arkadasIstekleriText = view.findViewById(R.id.arkadasIstekleriText);
        bildirimText = view.findViewById(R.id.bildirimText);
        bildirimler = new ArrayList<>();
        bildirimler.clear();
        bildirimlerController.setFragmentBildirimler(FragmentBildirimler.this);
        bildirimYolu = FirebaseDatabase.getInstance().getReference("Bildirim");
        bildirimleriGetir(FirebaseAuth.getInstance().getCurrentUser().getUid());
        BildirimlerRecyclerListesi.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        BildirimlerRecyclerListesi.setLayoutManager(linearLayoutManager);
        bildirimAdapter = new BildirimAdapter(bildirimler,getContext());
        BildirimlerRecyclerListesi.setAdapter(bildirimAdapter);
        IsteklerRecyclerListesi.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter=new arkadasIstekAdapter(bildirimlerController.istekleriDonder(),getContext(),arkadasIstekleriText,IsteklerRecyclerListesi);
        IsteklerRecyclerListesi.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    public void bildirimleriGetir(String id) {
        bildirimYolu.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bildirimler.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bildirimler bildirim = snapshot.getValue(Bildirimler.class);
                    bildirimler.add(bildirim);
                    System.out.println("/*************************************** "+bildirim.bildirim_icerik);
                }
                if(!bildirimler.isEmpty()) {
                    BildirimlerRecyclerListesi.setVisibility(View.VISIBLE);
                    bildirimText.setVisibility(View.GONE);
                } else {
                    BildirimlerRecyclerListesi.setVisibility(View.GONE);
                    bildirimText.setVisibility(View.VISIBLE);
                }
                bildirimAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
