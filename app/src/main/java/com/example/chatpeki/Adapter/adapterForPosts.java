package com.example.chatpeki.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatpeki.Model.Bildirimler;
import com.example.chatpeki.View.DigerProfilActivity;
import com.example.chatpeki.View.PostSendActivity;
import com.example.chatpeki.View.YorumActivity;
import com.example.chatpeki.Dialog.postSilDialog;
import com.example.chatpeki.Fragment.FragmentProfil;
import com.example.chatpeki.Model.Kisiler;
import com.example.chatpeki.R;
import com.example.chatpeki.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class adapterForPosts extends RecyclerView.Adapter<adapterForPosts.ViewHolder> {
    public Context mContext;
    public List<Post> mpost;
    public FragmentManager fragmentManager;
    private FirebaseUser firebaseUser;
    private Kisiler kisi;
    private String mKullaniciId;


    public adapterForPosts(Context mContext, List<Post> mpost, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.mpost = mpost;
        this.fragmentManager = fragmentManager;
        mKullaniciId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }
    public adapterForPosts(Context mContext, List<Post> mpost) {
        this.mContext = mContext;
        this.mpost = mpost;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row, viewGroup, false);
        return new adapterForPosts.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Begeniler");
        final Post post = mpost.get(i);

        if (!post.getResim().equals("")) {
            Picasso.get().load(post.getResim()).placeholder(R.drawable.picture).into(viewHolder.postImage);
            viewHolder.postIcerik.setVisibility(View.VISIBLE);
            viewHolder.postIcerik.setText(post.getDurum_icerik());
        } else {
            viewHolder.postImage.setVisibility(View.GONE);
            viewHolder.postIcerikUst.setText(post.getDurum_icerik());
            viewHolder.postIcerikUst.setVisibility(View.VISIBLE);
            viewHolder.postIcerik.setVisibility(View.GONE);
        }
        viewHolder.postDate.setText(post.getTarih());


        begen(post.getGonderi_id(), viewHolder.likeImage);
        begeniSayisi(post.getGonderi_id(), viewHolder.likeCount);
        yorumSayisi(post.getGonderi_id(), viewHolder.commentCount);

        viewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getGonderen_id().equals(firebaseUser.getUid())) {
                    fragmentManager.beginTransaction().replace(R.id.Fragment_container1, new FragmentProfil()).commit();
                } else {
                    Intent intent = new Intent(mContext, DigerProfilActivity.class);
                    intent.putExtra("tiklananKullanici_idGoster", post.getGonderen_id());
                    mContext.startActivity(intent);
                }
            }
        });

        viewHolder.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, YorumActivity.class);
                intent.putExtra("post_id", post.getGonderi_id());
                intent.putExtra("post_resim", post.getResim());
                intent.putExtra("alan_id", post.getGonderen_id());
                intent.putExtra("gonderen_id", firebaseUser.getUid());
                mContext.startActivity(intent);
            }
        });

        viewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.likeImage.getTag().equals("begen")) {
                    FirebaseDatabase.getInstance().getReference("Kullanicilar")
                            .child(mKullaniciId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    kisi= dataSnapshot.getValue(Kisiler.class);
                                    if(kisi != null && !post.getGonderen_id().equals(mKullaniciId)) {
                                        String mesaj = kisi.getAd()+" İsimli kullanici gönderdiğin postu beğendi";
                                        Bildirimler bildirim = new Bildirimler(mesaj, mKullaniciId, post.gonderen_id, post.resim, post.gonderi_id);
                                        kisi.arkadaslaraBildir(bildirim);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    System.out.println("DatabaseError : "+databaseError.getMessage());
                                }
                            });
                    firebaseDatabase.child(post.getGonderi_id()).child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    firebaseDatabase.child(post.getGonderi_id()).child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });

        viewHolder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.detailButton);
                if (post.getGonderen_id().equals(firebaseUser.getUid()))
                    popupMenu.inflate(R.menu.post_menu);
                else
                    popupMenu.inflate(R.menu.post_menu_diger);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.postSil:
                                openDialog(post.getGonderi_id(), post.getResim(),viewHolder,i);
                                break;
                            case R.id.postDuzenle:
                                postGuncelle(post);
                                break;
                            case R.id.postBildir:
                                Toast.makeText(mContext, "Biliriminiz başarıyla iletilmiştir", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.postEngelle:
                                Toast.makeText(mContext, "Kullanıcı engelle", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        gondericiBilgisi(viewHolder.profileImage, viewHolder.profileName, post.getGonderen_id());
    }


    @Override
    public int getItemCount() {
        return mpost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage;
        public ImageView postImage;
        public TextView profileName;
        public TextView postIcerik;
        public TextView postIcerikUst;
        public TextView postDate;
        public TextView likeCount;
        public TextView commentCount;
        public ImageButton detailButton;
        public ImageButton commentImage;
        public ImageButton likeImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            postImage = (ImageView) itemView.findViewById(R.id.postImage);
            postIcerik = (TextView) itemView.findViewById(R.id.postIcerik);
            postIcerikUst = (TextView) itemView.findViewById(R.id.postDurumIcerikUst);
            profileName = (TextView) itemView.findViewById(R.id.profileName);
            postDate = (TextView) itemView.findViewById(R.id.postDate);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
            detailButton = (ImageButton) itemView.findViewById(R.id.detailImageButton);
            commentImage = (ImageButton) itemView.findViewById(R.id.commentImage);
            likeImage = (ImageButton) itemView.findViewById(R.id.likeImage);
        }

    }


    private void gondericiBilgisi(final ImageView profilResmi, final TextView profilIsmi, final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kisiler kisi = dataSnapshot.getValue(Kisiler.class);
                Picasso.get().load(kisi.getResim()).placeholder(R.drawable.people).into(profilResmi);
                profilIsmi.setText(kisi.getAd() + " " + kisi.getSoyad());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void openDialog(String postId, String url,ViewHolder viewHolder,int position) {
        postSilDialog postSilDialog = new postSilDialog();
        postSilDialog.setPostUrl(url);
        postSilDialog.setPostId(postId);
        postSilDialog.setViewHolder(viewHolder);
        postSilDialog.setAdapter(this);
        postSilDialog.setPosition(position);
        postSilDialog.show(fragmentManager, "Örnek Diyalog");
    }

    private void begen(String postId, final ImageView imageView) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Begeniler")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.begen1);
                    imageView.setTag("begenildi");
                } else {
                    imageView.setImageResource(R.drawable.begen0);
                    imageView.setTag("begen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void begeniSayisi(String postId, final TextView textView) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Begeniler")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textView.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void yorumSayisi(String post_id, final TextView textView) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Yorumlar")
                .child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textView.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postGuncelle(Post post) {
        Intent intent = new Intent(mContext, PostSendActivity.class);
        //intent.putExtra("postGonderenId", post.getGonderenId());
        intent.putExtra("postResim", post.getResim());
        intent.putExtra("postGonderiId", post.getGonderi_id());
        intent.putExtra("postDurumIcerik", post.getDurum_icerik());
        mContext.startActivity(intent);
    }
}
