package com.example.chatpeki.Model;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Arkadas implements IObserver {
    private Kisiler kisi;

    public Arkadas(Kisiler kisi) {
        this.kisi = kisi;
    }



    @Override
    public void bildir(final Bildirimler bildirim) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bildirim")
                .child(kisi.getUid()).child(bildirim.post_id);
        reference.setValue(bildirim.toMap());
    }

    public Kisiler getKisi() {
        return kisi;
    }

    public void setKisi(Kisiler kisi) {
        this.kisi = kisi;
    }
}
