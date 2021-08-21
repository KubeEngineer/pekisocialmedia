package com.example.chatpeki.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatpeki.View.DigerProfilActivity;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class arkadaslarAdapter extends FirebaseRecyclerAdapter<Kisiler, arkadaslarAdapter.ViewHolder> {
    public List<Kisiler> kisilerList;
    public Context mContext;
    public TextView arkadasSayisi;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public arkadaslarAdapter(@NonNull FirebaseRecyclerOptions<Kisiler> options, Context mContext,TextView arkadasSayisi) {
        super(options);
        this.mContext = mContext;
        this.arkadasSayisi=arkadasSayisi;
    }


    @Override
    protected void onBindViewHolder(@NonNull final arkadaslarAdapter.ViewHolder holder, final int position, @NonNull final Kisiler model) {
        String tiklananKullaniciIdsi=getRef(position).getKey();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tiklananKullanici_idGoster = getRef(position).getKey();
                Intent profilActivitye = new Intent(mContext, DigerProfilActivity.class);
                profilActivitye.putExtra("tiklananKullanici_idGoster", tiklananKullanici_idGoster);
                mContext.startActivity(profilActivitye);
            }
        });
        arkadasSayisi.setText("Arkadaşlarım:"+getItemCount());
        DatabaseReference kullanicilarYolu= FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        kullanicilarYolu.child(tiklananKullaniciIdsi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Resim")){
                    String profilResmi=dataSnapshot.child("Resim").getValue().toString();
                    Picasso.get().load(profilResmi).placeholder(R.drawable.people).into(holder.profilresmi);
                }
                String ad=dataSnapshot.child("Ad").getValue().toString();
                String soyad=dataSnapshot.child("Soyad").getValue().toString();
                String kullaniciAdi=dataSnapshot.child("KullaniciAdi").getValue().toString();
                holder.kullaniciadi.setText(kullaniciAdi);
                holder.adSoyad.setText(ad+" "+soyad);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public arkadaslarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kullanici_gosterme,viewGroup,false);
        return new arkadaslarAdapter.ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView adSoyad,kullaniciadi;
        public CircleImageView profilresmi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adSoyad=itemView.findViewById(R.id.adSoyadProfil);
            kullaniciadi=itemView.findViewById(R.id.KullaniciAdiProfil);
            profilresmi=itemView.findViewById(R.id.kullanicilarprofilResmi);


        }
    }
}
