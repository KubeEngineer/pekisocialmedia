package com.example.chatpeki.Controller;



import com.example.chatpeki.Adapter.aramaAdapter;
import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IAramaDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaListeDinleyici;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.View.AramaActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;

public class AramaController {

    private ArrayList<Kisiler> arrayList=new ArrayList<>();
    private FirebaseRecyclerOptions<Kisiler> secenekler;
    private FirebaseRecyclerAdapter adapter;
    private AramaActivity aramaActivity;
    private DatabaseFacade DF=DatabaseFacade.get();
    private String mevcutKullaniciId= FirebaseAuth.getInstance().getCurrentUser().getUid();


    //singleton
    private static  AramaController aramaController;
    public static AramaController GetInstance(){
        if(aramaController==null){
            aramaController= new AramaController();
        }
        return aramaController;
    }


    public void  kullaniciOkuma(){
        final String path="Kullanicilar";
        DF.oku().ListeGetir(path, Kisiler.class, new IOkumaListeDinleyici<Kisiler>() {
            @Override
            public void tamamlandi(ArrayList<Kisiler> objectList) {
                arrayList = objectList;
                secenekler= DF.oku().seceneklerGetir(path,Kisiler.class);
                adapter=new aramaAdapter(secenekler,getAramaActivity(),mevcutKullaniciId);
                getAramaActivity().getRecyclerListesi().setAdapter(adapter);
                adapter.startListening();
            }
        });
    }


    public void firebaseArama(String s){
        String path = "Kullanicilar";
        String siraPath = "KullaniciAdi";
        DF.ara().ListeGetir(path, siraPath, s, Kisiler.class, new IAramaDinleyici<Kisiler>() {
            @Override
            public void tamamlandi(ArrayList<Kisiler> objectList, FirebaseRecyclerOptions<Kisiler> firebaseRecyclerOptions) {
                arrayList.clear();
                for(Kisiler k : objectList) {
                    if(!k.getUid().equals(DatabaseFacade.UId()))
                        arrayList.add(k);
                }
                secenekler= firebaseRecyclerOptions;
                adapter=new aramaAdapter(secenekler, getAramaActivity(),mevcutKullaniciId);
                getAramaActivity().getRecyclerListesi().setAdapter(adapter);
                adapter.startListening();
            }
        });
    }

    public AramaActivity getAramaActivity() {
        return aramaActivity;
    }

    public void setAramaActivity(AramaActivity aramaActivity) {
        this.aramaActivity = aramaActivity;
    }


}
