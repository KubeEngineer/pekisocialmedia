package com.example.chatpeki.Controller;

import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Fragment.FragmentBildirimler;
import com.example.chatpeki.Model.Kisiler;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BildirimlerController {

    private DatabaseFacade DF = DatabaseFacade.get();
    private FragmentBildirimler fragmentBildirimler;
    private static BildirimlerController bildirimlerController;
    public static  BildirimlerController getInstance() {
       if(bildirimlerController==null)
           bildirimlerController=new BildirimlerController();
        return bildirimlerController;
    }

    public FirebaseRecyclerOptions<Kisiler> istekleriDonder(){
        String path = "Arkadaslik Istegi/"+DatabaseFacade.UId();
        return DF.oku().seceneklerGetir(path,Kisiler.class);
    }

    public FragmentBildirimler getFragmentBildirimler() {
        return fragmentBildirimler;
    }

    public void setFragmentBildirimler(FragmentBildirimler fragmentBildirimler) {
        this.fragmentBildirimler = fragmentBildirimler;
    }
}
