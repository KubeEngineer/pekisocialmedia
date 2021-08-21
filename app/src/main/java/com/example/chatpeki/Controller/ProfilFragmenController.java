package com.example.chatpeki.Controller;



import com.example.chatpeki.Adapter.adapterForPosts;
import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaListeDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaNesneDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaSayiDinleyici;
import com.example.chatpeki.Fragment.FragmentProfil;
import com.example.chatpeki.GenelDinleyiciler.IDinleyici;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.Post;


import java.util.ArrayList;
import java.util.List;

public class ProfilFragmenController {
    private DatabaseFacade DF = DatabaseFacade.get();
    private String mevcutKullaniciId;
    private FragmentProfil fragmentProfil;
    private List<Post> postList;
    private adapterForPosts postAdapter;
    private static ProfilFragmenController profilFragmenController;
    public static ProfilFragmenController getInstance() {
        if(profilFragmenController==null){
            profilFragmenController=new ProfilFragmenController();
        }
        return profilFragmenController;
    }
    public ProfilFragmenController() {
        mevcutKullaniciId= DatabaseFacade.UId();
        postList =new ArrayList<>();
    }


    public void kullaniciBilgisiAl() {
        String path = "Kullanicilar/"+mevcutKullaniciId;
        DF.oku().nesneGetir(path, Kisiler.class, new IOkumaNesneDinleyici<Kisiler>() {
            @Override
            public void tamamlandi(Kisiler object) {
                if(object != null && object.getResim()!=null) {
                    String adSoyad = object.getAd()+" "+object.getSoyad();
                    getFragmentProfil().profildekiKullaniciBilgisiResimli(adSoyad,object.getResim());
                }else if(object != null) {
                    String adSoyad = object.getAd()+" "+object.getSoyad();
                    getFragmentProfil().profildekiKullaniciBilgisiResimsiz(adSoyad);
                }
            }
        });
    }

    public  void postlariGetir(final IDinleyici dinleyici) {
        String path = "Postlar";
        DF.oku().ListeGetir(path, Post.class, new IOkumaListeDinleyici<Post>() {
            @Override
            public void tamamlandi(ArrayList<Post> objectList) {
                postList.clear();
                for(int i=0;i<objectList.size();i++) {
                    if(objectList.get(i).getGonderen_id().equals(mevcutKullaniciId))
                        postList.add(objectList.get(i));
                    System.out.println("--------------------------"+objectList.get(i).getGonderen_id()+"///"+mevcutKullaniciId);
                }
                dinleyici.tamamlandi(postList);
                getFragmentProfil().postSayisiView(postList);
            }
        });
    }
    public void  arkadasSayisi() {
        String path = "Arkadaslar/"+mevcutKullaniciId;
        DF.oku().cocukSayisi(path, new IOkumaSayiDinleyici() {
            @Override
            public void tamamlandi(int miktar) {
                getFragmentProfil().arkadasSayisiViewAyar(miktar);
            }
        });
    }

    public FragmentProfil getFragmentProfil() {
        return fragmentProfil;
    }

    public void setFragmentProfil(FragmentProfil fragmentProfil) {
        this.fragmentProfil = fragmentProfil;
    }

    public adapterForPosts getPostAdapter() {
        return postAdapter;
    }
    public void setPostAdapter(adapterForPosts postAdapter) {
        this.postAdapter = postAdapter;
    }
}
