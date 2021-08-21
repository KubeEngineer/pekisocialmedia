package com.example.chatpeki.Database.Dinleyiciler;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public interface IAramaDinleyici<E> {
    void tamamlandi(ArrayList<E> objectList, FirebaseRecyclerOptions<E> firebaseRecyclerOptions);
}
