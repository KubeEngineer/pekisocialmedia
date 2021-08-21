package com.example.chatpeki.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatpeki.Database.DatabaseFacade;
import com.example.chatpeki.View.AnasayfaActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class aramaAdapter extends FirebaseRecyclerAdapter<Kisiler,aramaAdapter.ViewHolder> {
    public List<Kisiler> kisilerList;
    public List<String> arkadasList;
    public Context mContext;

    public String mevcutKullaniciId;


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public aramaAdapter(@NonNull FirebaseRecyclerOptions<Kisiler> options,Context mContext,String mevcutKullaniciId) {
        super(options);
        this.mevcutKullaniciId=mevcutKullaniciId;
        this.mContext = mContext;
        kontrolArkadaslar();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final Kisiler model) {
        final DatabaseReference ArkadaslikIstekYolu = FirebaseDatabase.getInstance().getReference().child("Arkadaslik Istegi");
        final DatabaseReference BildirimYolu = FirebaseDatabase.getInstance().getReference().child("Bildirimler");
        boolean var = false;
        if(!model.getUid().equals(mevcutKullaniciId)){
        holder.adSoyad.setText(model.getAd() + " " + model.getSoyad());
        holder.kullaniciadi.setText(model.getKullaniciAdi());
        Picasso.get().load(model.getResim()).placeholder(R.drawable.people).into(holder.profilResmi);
        }
        else{
            holder.profilResmi.setVisibility(View.GONE);
            holder.kullaniciadi.setVisibility(View.GONE);
            holder.adSoyad.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.GONE);

        }
        //aramada arkadaslar ve digerleri ayirt ediliyor.
        for (String s:arkadasList) {
            if(model.getUid().equals(s)) {
                var=true;
                break;
            }
        }

        if (!var) {
            holder.imageButton.setVisibility(View.VISIBLE);
            holder.imageButton.setEnabled(true);
        }
        //tıklandiğinda
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tiklananKullanici_idGoster = getRef(position).getKey();
                Intent profilActivitye = new Intent(mContext, DigerProfilActivity.class);
                profilActivitye.putExtra("tiklananKullanici_idGoster", tiklananKullanici_idGoster);
                mContext.startActivity(profilActivitye);
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tiklananKullanici_idGoster = getRef(position).getKey();
                ArkadaslikIstekYolu.child(mevcutKullaniciId).child(tiklananKullanici_idGoster)
                        .child("talep_turu").setValue("gönderildi")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //istegi alan
                                    ArkadaslikIstekYolu.child(tiklananKullanici_idGoster).child(mevcutKullaniciId).
                                            child("talep_turu").setValue("alindi")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        //Bildirim için
                                                        HashMap<String,String> arkadasBildirimMap=new HashMap<>();
                                                        arkadasBildirimMap.put("kimden",mevcutKullaniciId);
                                                        arkadasBildirimMap.put("tur","talep");
                                                        //Bildirim veritabanı yolu
                                                        BildirimYolu.child(tiklananKullanici_idGoster).push().setValue(arkadasBildirimMap);
                                                        Toast.makeText(mContext, "İstek Yollandi", Toast.LENGTH_SHORT).show();
                                                    }
                                                }


                                            });
                                }
                            }
                        });
            }
        });
    }

    private  void  kontrolArkadaslar() {
        arkadasList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Arkadaslar")
                .child(mevcutKullaniciId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arkadasList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    arkadasList.add(snapshot.getKey());
                    System.out.println(snapshot.getKey());
                }
                arkadasList.add(mevcutKullaniciId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view1=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kullanici_gosterme,viewGroup,false);
        return new aramaAdapter.ViewHolder(view1);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView adSoyad,kullaniciadi;
        public CircleImageView profilResmi;
        public ImageButton imageButton;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adSoyad=itemView.findViewById(R.id.adSoyadProfil);
            kullaniciadi=itemView.findViewById(R.id.KullaniciAdiProfil);
            profilResmi=itemView.findViewById(R.id.kullanicilarprofilResmi);
            imageButton=itemView.findViewById(R.id.arkadasEkle);
            linearLayout=itemView.findViewById(R.id.herSatirLiner);
        }
    }
}
