package com.example.chatpeki.Database;

import android.support.annotation.NonNull;

import com.example.chatpeki.Database.Dinleyiciler.IOkumaListeDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaNesneDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaSayiDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaSoruDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.ISoruDinleyici;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseOku {
    public <T> void ListeGetir(String path, final Class<T> tClass, final IOkumaListeDinleyici dinleyici){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<T> list =new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add((T) snapshot.getValue(tClass));
                }
                dinleyici.tamamlandi(list);
                reference.keepSynced(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Database Error ! : "+databaseError.getMessage());
            }
        });
    }

    public <T> void nesneGetir(String path, final Class<T> tClass, final IOkumaNesneDinleyici dinleyici){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dinleyici.tamamlandi(dataSnapshot.getValue(tClass));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Database Error ! : "+databaseError.getMessage());
            }
        });
    }

    public void cocukSayisi(String path, final IOkumaSayiDinleyici sayiDinleyici) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sayiDinleyici.tamamlandi((int) dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void varMiChild(String path, final String child, final ISoruDinleyici dinleyici) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(child))
                    dinleyici.tamamlandi(dataSnapshot.child(child),true);
                else
                    dinleyici.tamamlandi(null,false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Database Error ! : "+databaseError.getMessage());
            }
        });
    }

    public void varMiValue(String path, final String value, final IOkumaSoruDinleyici dinleyici) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().equals(value))
                    dinleyici.tamamlandi(true);
                else
                    dinleyici.tamamlandi(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Database Error ! : "+databaseError.getMessage());
            }
        });
    }

    public <T> FirebaseRecyclerOptions seceneklerGetir(String path, Class<T> tClass) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        return new FirebaseRecyclerOptions.Builder<T>()
                .setQuery(reference,tClass).build();
    }
}
