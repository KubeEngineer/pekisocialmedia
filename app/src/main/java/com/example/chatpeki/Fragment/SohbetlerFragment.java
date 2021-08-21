package com.example.chatpeki.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatpeki.Adapter.sohbetAdapter;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SohbetlerFragment extends Fragment {

    View ozel_sohbetler;
    private RecyclerView sohbetlerListesi;
    private DatabaseReference sohbet_yolu;
    private String aktif_kullanici_id;

    public SohbetlerFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ozel_sohbetler = inflater.inflate(R.layout.fragment_sohbetler, container, false);
        sohbetlerListesi = ozel_sohbetler.findViewById(R.id.sohbetlerListesi);
        sohbetlerListesi.setLayoutManager(new LinearLayoutManager(getContext()));
        aktif_kullanici_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sohbet_yolu = FirebaseDatabase.getInstance().getReference().child("Arkadaslar").child(aktif_kullanici_id);
        return ozel_sohbetler;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Kisiler> secenekler = new FirebaseRecyclerOptions.Builder<Kisiler>()
                .setQuery(sohbet_yolu, Kisiler.class).build();
        FirebaseRecyclerAdapter adapter = new sohbetAdapter(secenekler,getContext());
        sohbetlerListesi.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }


}
