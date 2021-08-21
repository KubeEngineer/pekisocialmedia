package com.example.chatpeki.Controller;

import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.View.BenimArkadaslarimActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BenimArkadaslarimController {
    private DatabaseReference arkadaslarYolu;

    private String aktifKullaniciId;
    private DatabaseFacade DF = DatabaseFacade.get();
    private DatabaseReference kullanicilarYolu;
    //singelton
    private static BenimArkadaslarimController benimArkadaslarimController;

    public static BenimArkadaslarimController getInstance() {
        if (benimArkadaslarimController == null) {
            benimArkadaslarimController = new BenimArkadaslarimController();
        }

        return benimArkadaslarimController;
    }
    public FirebaseRecyclerOptions arkadasListesi() {
        String path = "Arkadaslar/" + DatabaseFacade.UId();
        return DF.oku().seceneklerGetir(path, Kisiler.class);
    }
}
