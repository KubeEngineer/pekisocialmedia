package com.example.chatpeki.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DatabaseFacade {
    private DatabaseOku databaseOku;
    private DatabaseYaz databaseYaz;
    private  DatabaseAra databaseAra;

    private static DatabaseFacade databaseFacade;
    public static DatabaseFacade get() {
        if(databaseFacade == null)
            databaseFacade = new DatabaseFacade();
        return databaseFacade;
    }


    private DatabaseFacade () {
        databaseOku = new DatabaseOku();
        databaseAra = new DatabaseAra();
        databaseYaz = new DatabaseYaz();
    }

    public DatabaseOku oku() {
        if(databaseOku == null)
            databaseOku = new DatabaseOku();
        return databaseOku;
    }

    public DatabaseYaz yaz() {
        if(databaseYaz == null)
            databaseYaz = new DatabaseYaz();
        return databaseYaz;
    }

    public DatabaseAra ara() {
        if(databaseAra == null)
            databaseAra = new DatabaseAra();
        return databaseAra;
    }
    public static String UId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
