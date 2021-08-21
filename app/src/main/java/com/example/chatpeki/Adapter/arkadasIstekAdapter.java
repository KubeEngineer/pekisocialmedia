package com.example.chatpeki.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatpeki.View.DigerProfilActivity;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class arkadasIstekAdapter extends FirebaseRecyclerAdapter <Kisiler,arkadasIstekAdapter.ViewHolder> {
    private Context mContext;
    private DatabaseReference arkadasIstekYolu,kullanicilarYolu,ArkadaslarYolu;
    private TextView textView;
    private RecyclerView recyclerView;
    private String aktifKullaniciId;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public arkadasIstekAdapter(@NonNull FirebaseRecyclerOptions<Kisiler> options, Context mContext,TextView textView,RecyclerView recyclerView) {
        super(options);
        this.mContext = mContext;
        this.textView = textView;
        this.recyclerView = recyclerView;
        arkadasIstekYolu= FirebaseDatabase.getInstance().getReference().child("Arkadaslik Istegi");
        kullanicilarYolu= FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        ArkadaslarYolu= FirebaseDatabase.getInstance().getReference().child("Arkadaslar");
        aktifKullaniciId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull Kisiler model) {

        //taleplerin hepsini alma
        final  String kullanici_id_listesi=getRef(position).getKey();
        DatabaseReference istekTuruAl=getRef(position).child("talep_turu").getRef();
               /* if(){
                    holder.itemView.setVisibility(View.INVISIBLE);
                }*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tiklananKullanici_idGoster = getRef(position).getKey();
                Intent profilActivitye = new Intent(mContext, DigerProfilActivity.class);
                profilActivitye.putExtra("tiklananKullanici_idGoster", tiklananKullanici_idGoster);
                mContext.startActivity(profilActivitye);
            }
        });
        istekTuruAl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String tur=dataSnapshot.getValue().toString();
                    if(tur.equals("alindi")){

                        kullanicilarYolu.child(kullanici_id_listesi).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("Resim")){
                                    //veri tabanından resim çekip değişkenlere aktarma
                                    final String istekProfilResmi=dataSnapshot.child("Resim").getValue().toString();
                                    //çekilen resimi programa aktarma,
                                    Picasso.get().load(istekProfilResmi).into(holder.profilResmi);
                                }
                                //veri tabanından verileri çekip değişkenlere aktarma
                                final String istekAdi=dataSnapshot.child("Ad").getValue().toString();
                                final String istekSoyadi=dataSnapshot.child("Soyad").getValue().toString();
                                final String istekKullaniciAdi=dataSnapshot.child("KullaniciAdi").getValue().toString();
                                //çekilen verileri programa aktarma,
                                holder.kullaniciadi.setText("Seninle arkadaş olmak istiyor.");
                                holder.adSoyad.setText(istekAdi+" "+istekSoyadi);
                                holder.kabulImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ArkadaslarYolu.child(aktifKullaniciId).child(kullanici_id_listesi)
                                                .child("Arkadaslar").setValue("Kaydedildi").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    ArkadaslarYolu.child(kullanici_id_listesi)
                                                            .child(aktifKullaniciId).child("Arkadaslar").setValue("Kaydedildi")
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        arkadasIstekYolu.child(aktifKullaniciId).child(kullanici_id_listesi)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    arkadasIstekYolu.child(kullanici_id_listesi).child(aktifKullaniciId)
                                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            Toast.makeText(mContext, "Artık arkadaşsınız.", Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                                    }
                                });

                                holder.iptalImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        arkadasIstekYolu.child(aktifKullaniciId).child(kullanici_id_listesi)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    arkadasIstekYolu.child(kullanici_id_listesi).child(aktifKullaniciId)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(mContext, "Arkadaşlık isteği silindi.", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                });




                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else if(tur.equals("gönderildi")){
                        ImageView talep_gonderme_butonu=holder.itemView.findViewById(R.id.istekKabulEtmeButonu);
                        holder.itemView.findViewById(R.id.istekKabulEtmeButonu).setVisibility(View.INVISIBLE);

                        kullanicilarYolu.child(kullanici_id_listesi).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("Resim")){
                                    //veri tabanından resim çekip değişkenlere aktarma
                                    final String istekProfilResmi=dataSnapshot.child("Resim").getValue().toString();
                                    //çekilen resimi programa aktarma,
                                    Picasso.get().load(istekProfilResmi).into(holder.profilResmi);
                                }
                                //veri tabanından verileri çekip değişkenlere aktarma
                                final String istekAdi=dataSnapshot.child("Ad").getValue().toString();
                                final String istekSoyadi=dataSnapshot.child("Soyad").getValue().toString();
                                final String istekKullaniciAdi=dataSnapshot.child("KullaniciAdi").getValue().toString();
                                //çekilen verileri programa aktarma,
                                holder.kullaniciadi.setText("İstek attin");
                                holder.adSoyad.setText(istekAdi+" "+istekSoyadi);

                                holder.iptalImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        arkadasIstekYolu.child(aktifKullaniciId).child(kullanici_id_listesi)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    arkadasIstekYolu.child(kullanici_id_listesi).child(aktifKullaniciId)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(mContext, "Arkadaşlık isteği silindi.", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                });




                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if(getItemCount() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.isteklerdeki_kullanici,viewGroup,false);
        return new ViewHolder(view);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView adSoyad,kullaniciadi;
        public CircleImageView profilResmi;
        public ImageView kabulImage,iptalImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adSoyad=itemView.findViewById(R.id.adSoyadProfil);
            kullaniciadi=itemView.findViewById(R.id.KullaniciAdiProfil);
            profilResmi=itemView.findViewById(R.id.kullanicilarprofilResmi);
            kabulImage=itemView.findViewById(R.id.istekKabulEtmeButonu);
            iptalImage=itemView.findViewById(R.id.istekReddetmeButonu);

        }
    }
}
