package com.example.chatpeki.Database.Dinleyiciler;

import com.google.firebase.database.DataSnapshot;

public interface ISoruDinleyici {
    void tamamlandi(DataSnapshot snapshot,Boolean sonuc);
}
