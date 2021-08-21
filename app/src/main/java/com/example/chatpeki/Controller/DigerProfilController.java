package com.example.chatpeki.Controller;

import android.support.annotation.NonNull;
import com.example.chatpeki.Adapter.adapterForPosts;
import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaListeDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaNesneDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IOkumaSayiDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.ISoruDinleyici;
import com.example.chatpeki.Database.Dinleyiciler.IYazmaDinleyici;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.Post;
import com.example.chatpeki.View.DigerProfilActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DigerProfilController {
    private DatabaseReference KullaniciYolu;
    private DatabaseFacade DF = DatabaseFacade.get();
    private  String alinanKullaniciid;


    private DatabaseReference ArkadaslikIstekYolu,
            ArkadaslarYolu,
            BildirimYolu;
    private String aktifKullanicid;

    private List<Post> postList=new ArrayList<>();
    private adapterForPosts postAdapter;
    private DigerProfilActivity digerProfilActivity;
    //singleton
    private static DigerProfilController digerProfilController;
    public static DigerProfilController GetInstance() {
        if(digerProfilController==null){
            digerProfilController= new DigerProfilController();
        }

        return digerProfilController;
    }
    public void baslangic(){
        KullaniciYolu = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        ArkadaslikIstekYolu = FirebaseDatabase.getInstance().getReference().child("Arkadaslik Istegi");
        ArkadaslarYolu = FirebaseDatabase.getInstance().getReference().child("Arkadaslar");
        BildirimYolu = FirebaseDatabase.getInstance().getReference().child("Bildirimler");
        aktifKullanicid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void KullaniciBilgisiAl() {
        baslangic();
        String path = "Kullanicilar/"+ getAlinanKullaniciid();
        DF.oku().nesneGetir(path, Kisiler.class, new IOkumaNesneDinleyici<Kisiler>() {
            @Override
            public void tamamlandi(Kisiler object) {
                if(object != null && object.getResim() != null) {
                    getDigerProfilActivity().veriTabanindakileriVieweAktarResimli(
                            object.getResim(),
                            object.getAd(),
                            object.getSoyad()
                    );
                } else {
                    getDigerProfilActivity().veriTabanindakileriVieweAktarResimsiz(
                            object.getAd(),
                            object.getSoyad()
                    );
                }
            }
        });
    }




    public void veriTabaniKontrolArkadasliktanCıkart() {
        baslangic();
        String path = "Arkadaslar/"+DatabaseFacade.UId()+"/"+getAlinanKullaniciid();
        final String path2= "Arkadaslar/"+getAlinanKullaniciid()+"/"+DatabaseFacade.UId();
        //Arkadaşı sil
        DF.yaz().sil(path, new IYazmaDinleyici() {
            @Override
            public void tamamlandi() {
                DF.yaz().sil(path2, new IYazmaDinleyici() {
                    @Override
                    public void tamamlandi() {
                        getDigerProfilActivity().arkadasliktanCikartinViewAyari();
                    }
                });
            }
        });
    }


    public void ArkadaslikIstekKabul() {
        baslangic();
        String eklePath = "Arkadaslar/"+DatabaseFacade.UId()+"/"+getAlinanKullaniciid()+"/Arkadaslar";
        final String eklePath2 = "Arkadaslar/"+getAlinanKullaniciid()+"/"+DatabaseFacade.UId()+"/Arkadaslar";
        final String silPath = "Arkadaslik Istegi/"+DatabaseFacade.UId()+"/"+getAlinanKullaniciid();
        final String silPath2 = "Arkadaslik Istegi/"+getAlinanKullaniciid()+"/"+DatabaseFacade.UId();

        DF.yaz().yaz(eklePath, "Kaydedildi", new IYazmaDinleyici() {
            @Override
            public void tamamlandi() {
                DF.yaz().yaz(eklePath2, "Kaydedildi", new IYazmaDinleyici() {
                    @Override
                    public void tamamlandi() {
                        DF.yaz().sil(silPath, new IYazmaDinleyici() {
                            @Override
                            public void tamamlandi() {
                                DF.yaz().sil(silPath2, new IYazmaDinleyici() {
                                    @Override
                                    public void tamamlandi() {
                                        getDigerProfilActivity().arkadasIstekKabulViewAyari();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void ArkadaslikIstekIptal() {
        baslangic();
        String silPath = "Arkadaslik Istegi/"+DatabaseFacade.UId()+"/"+getAlinanKullaniciid();
        final String silPath2 = "Arkadaslik Istegi/"+getAlinanKullaniciid()+"/"+DatabaseFacade.UId();

        //Talebi gönderenden sil
        DF.yaz().sil(silPath, new IYazmaDinleyici() {
            @Override
            public void tamamlandi() {
                DF.yaz().sil(silPath2, new IYazmaDinleyici() {
                    @Override
                    public void tamamlandi() {
                        getDigerProfilActivity().arkadasIstekIptalViewAyari();
                    }
                });
            }
        });
    }

    public void arkadaslikIstegiGonder() {
        baslangic();
        //isteği gönderen
        final String eklePath="Arkadaslik Istegi/"+DatabaseFacade.UId()+"/"+getAlinanKullaniciid()+"/talep_turu";
        final String eklePath2="Arkadaslik Istegi/"+getAlinanKullaniciid()+"/"+DatabaseFacade.UId()+"/talep_turu";
        final String eklePath3="Bildirimler/"+getAlinanKullaniciid();

        DF.yaz().yaz(eklePath, "gönderildi", new IYazmaDinleyici() {
            @Override
            public void tamamlandi() {
                DF.yaz().yaz(eklePath2, "alindi", new IYazmaDinleyici() {
                    @Override
                    public void tamamlandi() {
                        HashMap<String,String> arkadasBildirimMap=new HashMap<>();
                        arkadasBildirimMap.put("kimden",aktifKullanicid);
                        arkadasBildirimMap.put("tur","talep");
                        DF.yaz().yaz(eklePath3, arkadasBildirimMap, new IYazmaDinleyici() {
                            @Override
                            public void tamamlandi() {
                                getDigerProfilActivity().arkadasIstegiGonderViewAyarlama();
                            }
                        });
                    }
                });
            }
        });
    }


    public void  arkadasSayisi() {
        baslangic();
        String path = "Arkadaslar/"+getAlinanKullaniciid();
        DF.oku().cocukSayisi(path, new IOkumaSayiDinleyici() {
            @Override
            public void tamamlandi(int miktar) {
                getDigerProfilActivity().digerProfilArkadasSayisiViewAyar(miktar);
            }
        });
    }



    public void arkadaslikYonet(){
        baslangic();
        final String path = "Arkadaslik Istegi/"+DatabaseFacade.UId();
        final String path2 = "Arkadaslar/"+DatabaseFacade.UId();
        DF.oku().varMiChild(path, getAlinanKullaniciid(), new ISoruDinleyici() {
            @Override
            public void tamamlandi(DataSnapshot snapshot, Boolean sonuc) {
                if(sonuc) {
                    String talep_turu = snapshot.child("talep_turu").getValue().toString();
                    if (talep_turu.equals("gönderildi")) {
                        getDigerProfilActivity().talepDurumuGonderildiViewAyari();
                    } else {
                        getDigerProfilActivity().talepDurumuAlindiViewAyari();
                    }
                } else {
                    DF.oku().varMiChild(path2, getAlinanKullaniciid(), new ISoruDinleyici() {
                        @Override
                        public void tamamlandi(DataSnapshot snapshot, Boolean sonuc) {
                            if(sonuc)
                                getDigerProfilActivity().arkadasDurumu();
                        }
                    });
                }
            }
        });
    }



    public void postlariGetir() {
        baslangic();
        String path = "Postlar";
        DF.oku().ListeGetir(path, Post.class, new IOkumaListeDinleyici<Post>() {
            @Override
            public void tamamlandi(ArrayList<Post> objectList) {
                postList.clear();
                for(Post p:objectList) {
                    if(p.getGonderen_id().equals(getAlinanKullaniciid()))
                        postList.add(p);
                }
                getDigerProfilActivity().digerProfilPostSayisiViewAyari(postList);
                postAdapter = new adapterForPosts(getDigerProfilActivity(), postList,getDigerProfilActivity().getSupportFragmentManager());
                postAdapter.notifyDataSetChanged();
                getDigerProfilActivity().postlariListesiDondur(postAdapter);
            }
        });
        getDigerProfilActivity().butonaGoreArkadasKontrolu();
    }


    public DigerProfilActivity getDigerProfilActivity() {
        return digerProfilActivity;
    }

    public void setDigerProfilActivity(DigerProfilActivity digerProfilActivity) {
        this.digerProfilActivity = digerProfilActivity;
    }

    public String getAlinanKullaniciid() {
        return alinanKullaniciid;
    }

    public void setAlinanKullaniciid(String alinanKullaniciid) {
        this.alinanKullaniciid = alinanKullaniciid;
    }
}
