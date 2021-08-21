package com.example.chatpeki.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.Model.Yorum;
import com.example.chatpeki.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class yorumAdapter extends RecyclerView.Adapter<yorumAdapter.ViewHolder>{
    private Context mcontext;
    private List<Yorum> mYorum;

    /*private FirebaseUser firebaseUser;*/

    public yorumAdapter(Context mcontext, List<Yorum> mYorum) {
        this.mcontext = mcontext;
        this.mYorum = mYorum;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.yorum_row,viewGroup,false);
        return new yorumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
       /* firebaseUser = FirebaseAuth.getInstance().getCurrentUser();*/

        Yorum yorum = mYorum.get(i);
        viewHolder.yorumIcerik.setText(yorum.getYorum());

        getKullaniciBilgi(viewHolder.profilResmi,viewHolder.profilIsmi,yorum.getGonderen_id());
    }

    @Override
    public int getItemCount() {
        return mYorum.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profilResmi;
        public TextView profilIsmi;
        public TextView yorumIcerik;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilResmi = itemView.findViewById(R.id.yorumProfilResmi);
            profilIsmi  = itemView.findViewById(R.id.yorumProfilIsmi);
            yorumIcerik = itemView.findViewById(R.id.yorumText);

        }
    }

    private void getKullaniciBilgi(final ImageView profilResmi, final TextView profilIsmi, String gonderen_id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(gonderen_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kisiler kisi = dataSnapshot.getValue(Kisiler.class);
                Picasso.get().load(kisi.getResim()).placeholder(R.drawable.people).into(profilResmi);
                profilIsmi.setText(kisi.getAd()+" "+kisi.getSoyad());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
