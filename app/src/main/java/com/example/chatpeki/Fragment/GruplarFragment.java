package com.example.chatpeki.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chatpeki.View.GrupSohbetActivity;
import com.example.chatpeki.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GruplarFragment extends Fragment {

    public GruplarFragment() {
        // Required empty public constructor
    }

    private View grupSekmesiView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> grup_listeleri=new ArrayList<>();
    //Firebase
    private DatabaseReference grupYolu;

    public void init() {
        //Firebase tanımlamaları
        grupYolu= FirebaseDatabase.getInstance().getReference().child("Gruplar");
        //tanımlamalar
        listView=grupSekmesiView.findViewById(R.id.grupListele);
        arrayAdapter=new ArrayAdapter<String>(getContext(),R.layout.grupliste_tasarimi,grup_listeleri);

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        grupSekmesiView = inflater.inflate(R.layout.fragment_gruplar,container,false);
            init();
            listView.setAdapter(arrayAdapter);

        //grupları alma komutları
        GruplariAlveGoster();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mevcutGrupAdi= parent.getItemAtPosition(position).toString();
                Intent  grupSohbetActivity=new Intent(getContext(), GrupSohbetActivity.class);
                grupSohbetActivity.putExtra("GrupAdi",mevcutGrupAdi);
                startActivity(grupSohbetActivity);
            }
        });
        return grupSekmesiView;
    }

    private void GruplariAlveGoster() {

        //veritabanından kodları çekme için
        grupYolu.addValueEventListener(new ValueEventListener() {
            //datasnapshot veri tabnındaki verileri temsil eder
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String>set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
            while (iterator.hasNext()){
                set.add(((DataSnapshot)iterator.next()).getKey());
            }
            grup_listeleri.clear();
            grup_listeleri.addAll(set);
            arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
