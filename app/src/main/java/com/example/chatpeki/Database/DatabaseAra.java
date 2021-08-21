package com.example.chatpeki.Database;

import android.support.annotation.NonNull;

import com.example.chatpeki.Database.Dinleyiciler.IAramaDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaListeDinleyici;
import com.example.chatpeki.Model.Kisiler;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseAra {
    public <T> void ListeGetir(String path, String siralamaPath, String terim, final Class<T> tClass, final IAramaDinleyici dinleyici){
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        final Query query=reference.orderByChild(siralamaPath)
                .startAt(terim).endAt(terim+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<T> list =new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    T t = snapshot.getValue(tClass);
                    assert t !=null;
                    list.add(t);
                }
                ;
                dinleyici.tamamlandi(list,
                        new FirebaseRecyclerOptions
                                .Builder<T>()
                                .setQuery(query,tClass)
                                .build());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
