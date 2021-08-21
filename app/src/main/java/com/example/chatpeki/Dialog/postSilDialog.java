package com.example.chatpeki.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;

import com.example.chatpeki.Adapter.adapterForPosts;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class postSilDialog extends AppCompatDialogFragment {
    DatabaseReference postYolu;
    DatabaseReference begeniYolu;
    DatabaseReference yorumYolu;
    StorageReference postResimYolu;
    private String postId;
    private String postUrl;
    private int position;
    private adapterForPosts adapter;
    private adapterForPosts.ViewHolder viewHolder;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {


        postYolu = FirebaseDatabase.getInstance().getReference("Postlar").child(postId);
        begeniYolu = FirebaseDatabase.getInstance().getReference("Begeniler").child(postId);
        yorumYolu = FirebaseDatabase.getInstance().getReference("Yorumlar").child(postId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Post Silme").setMessage("Bu postu silmek istediğinize emin misiniz?")
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "İyi seçim", Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!postUrl.equals("")) {
                    postResimYolu = FirebaseStorage.getInstance().getReferenceFromUrl(postUrl);
                    postYolu.removeValue();
                    begeniYolu.removeValue();
                    yorumYolu.removeValue();
                    postResimYolu.delete();
                } else {
                    postYolu.removeValue();
                    begeniYolu.removeValue();
                    yorumYolu.removeValue();
                }
                /*FragmentAnaSayfa fragment = (FragmentAnaSayfa)
                        getFragmentManager().findFragmentById(R.id.Fragment_container1);

                getFragmentManager().beginTransaction()
                        .detach(fragment)
                        .attach(fragment)
                        .commit();*/


            }
        });

        return builder.create();
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setViewHolder(adapterForPosts.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public void setAdapter(adapterForPosts adapter) {
        this.adapter = adapter;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
