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

import com.example.chatpeki.Model.Bildirimler;
import com.example.chatpeki.R;
import com.example.chatpeki.View.AnasayfaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BildirimAdapter extends RecyclerView.Adapter<BildirimAdapter.ViewHolder> {
    public List<Bildirimler> bildirimler;
    public Context mContext;
    private String mKullaniciId;

    public BildirimAdapter(List<Bildirimler> bildirimler, Context mContext) {
        this.bildirimler = bildirimler;
        this.mContext = mContext;
        mKullaniciId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        System.out.println("burayasdasdas");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bildirim_satir,viewGroup,false);
        return new BildirimAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Bildirimler bildirim = bildirimler.get(i);
        if(!bildirim.bildirim_resim.equals("")) {
            Picasso.get().load(bildirim.getBildirim_resim()).placeholder(R.drawable.picture).into(viewHolder.bildirim_post_image);
            viewHolder.bildirimIcerik.setText(bildirim.bildirim_icerik);
            System.out.println("/*//*/*/*///*/*/*/*/*/*/"+bildirim.bildirim_icerik);
        } else {
            viewHolder.bildirim_post_image.setVisibility(View.GONE);
            viewHolder.bildirimIcerik.setVisibility(View.GONE);
            viewHolder.bildirimIcerikResimsiz.setVisibility(View.VISIBLE);
            viewHolder.bildirimIcerikResimsiz.setText(bildirim.bildirim_icerik);
            System.out.println("/*//*/*/*///*/*/*/*/*/*/"+bildirim.bildirim_resim);
        }
        viewHolder.bildirimIcerik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AnasayfaActivity.class);
                intent.putExtra("post_id",bildirim.post_id);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bildirimler.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView bildirimIcerik,bildirimIcerikResimsiz;
        public ImageView bildirim_post_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bildirimIcerik = itemView.findViewById(R.id.bildirimIcerik);
            bildirimIcerikResimsiz = itemView.findViewById(R.id.bildirimIcerikResimsiz);
            bildirim_post_image = itemView.findViewById(R.id.bildirim_post_image);
        }
    }
}
