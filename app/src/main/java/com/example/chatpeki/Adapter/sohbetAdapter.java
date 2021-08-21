package com.example.chatpeki.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatpeki.View.OzelSohbetActivity;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class sohbetAdapter extends FirebaseRecyclerAdapter<Kisiler,sohbetAdapter.ViewHolder> {
    private DatabaseReference kullanici_yolu;
    private Context mContext;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public sohbetAdapter(@NonNull FirebaseRecyclerOptions<Kisiler> options, Context mContext) {
        super(options);
        kullanici_yolu = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Kisiler model) {
        final String kullanici_idleri = getRef(position).getKey();
        final String[] resim_Al = {"Varsayilan Resim"};
        kullanici_yolu.child(kullanici_idleri).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("Resim")) {
                        resim_Al[0] = dataSnapshot.child("Resim").getValue().toString();
                        Picasso.get().load(resim_Al[0]).placeholder(R.drawable.people).into(holder.profilResmi);

                    }
                    //kullanicidurmunagöre verileri çekmek
                    if(dataSnapshot.child("KullaniciDurumu").hasChild("Durum")){
                        String durum=dataSnapshot.child("KullaniciDurumu").child("Durum").getValue().toString();
                        String tarih=dataSnapshot.child("KullaniciDurumu").child("Tarih").getValue().toString();
                        String zaman=dataSnapshot.child("KullaniciDurumu").child("Zaman").getValue().toString();
                        if(durum.equals("çevrimiçi")){
                            holder.durumImage.setVisibility(View.VISIBLE);
                        }
                        else if(durum.equals("çevrimdişi")){
                            holder.durumImage.setVisibility(View.INVISIBLE);
                        }

                    } else{
                        holder.durumImage.setVisibility(View.INVISIBLE);
                    }
                    final String ad_Al = dataSnapshot.child("Ad").getValue().toString();
                    final String soyad_Al = dataSnapshot.child("Soyad").getValue().toString();
                    final String kullaniciAdi_Al = dataSnapshot.child("KullaniciAdi").getValue().toString();
                    holder.adSoyad.setText(ad_Al + " " + soyad_Al);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //sohbet activiye git
                            Intent sohbeteGit = new Intent(mContext, OzelSohbetActivity.class);
                            sohbeteGit.putExtra("kullanici_id_ziyaret", kullanici_idleri);
                            sohbeteGit.putExtra("resim_ziyaret", resim_Al[0]);
                            sohbeteGit.putExtra("kullanici_adiSoyadi_ziyaret", ad_Al + " " + soyad_Al);
                            sohbeteGit.putExtra("kullanici_adi", kullaniciAdi_Al);
                            mContext.startActivity(sohbeteGit);

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sohbetlerdeki_kullanici_gosterme, viewGroup, false);
        return new sohbetAdapter.ViewHolder(view);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView durumImage;
        CircleImageView profilResmi;
        TextView adSoyad;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilResmi = itemView.findViewById(R.id.kullanicilarprofilResmi_mesajlarda);
            adSoyad = itemView.findViewById(R.id.adSoyadProfil_mesajlarda);
            durumImage = itemView.findViewById(R.id.onlinedurumu);
        }
    }
}
