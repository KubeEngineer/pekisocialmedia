package com.example.chatpeki.Database;

import android.support.annotation.NonNull;

import com.example.chatpeki.Database.Dinleyiciler.IYazmaDinleyici;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DatabaseYaz {
    public void yaz (String path, String value, final IYazmaDinleyici dinleyici) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            dinleyici.tamamlandi();
                        }
                    }
                });
    }
    public void yaz (String path, HashMap value, final IYazmaDinleyici dinleyici) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.push().setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    dinleyici.tamamlandi();
                }
            }
        });
    }
    public void sil (String path, final IYazmaDinleyici dinleyici) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    dinleyici.tamamlandi();
                }
            }
        });
    }


}
