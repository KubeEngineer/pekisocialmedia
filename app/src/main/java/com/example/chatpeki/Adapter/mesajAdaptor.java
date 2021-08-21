package com.example.chatpeki.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatpeki.Model.Mesajlar;
import com.example.chatpeki.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class mesajAdaptor extends RecyclerView.Adapter<mesajAdaptor.MesajlarViewHolder> {

    private List<Mesajlar> kullaniciMesajlariListesi;

    private FirebaseAuth mYetki;
    private DatabaseReference kullanicilar_yolu;
    private boolean mesaj;

//adaptor
    public mesajAdaptor (List<Mesajlar> kullaniciMesajlariListesi){
        this.kullaniciMesajlariListesi=kullaniciMesajlariListesi;
    }
    //viewHolder
    public class MesajlarViewHolder extends RecyclerView.ViewHolder {
        public TextView aliciMesajIcerik,gonderenMesajIcerik;
        public CircleImageView aliciKullaniciResmi;
        public ImageView mesajGonderenResim,mesajAlanResim,mesajGonderenDosya,mesajAlanDosya;


        public MesajlarViewHolder(@NonNull View itemView) {
            super(itemView);
            aliciMesajIcerik=itemView.findViewById(R.id.alici_mesaj);
            aliciKullaniciResmi=itemView.findViewById(R.id.mesaj_profil_resmi);
            gonderenMesajIcerik=itemView.findViewById(R.id.gonderen_mesaj) ;
            mesajGonderenResim=itemView.findViewById(R.id.mesaj_gonderen_image_view);
            mesajAlanResim=itemView.findViewById(R.id.mesaj_alan_image_view);
            mesajGonderenDosya=itemView.findViewById(R.id.mesaj_gonderen_pdf_view);
            mesajAlanDosya=itemView.findViewById(R.id.mesaj_alan_pdf_view);


        }
    }

    @NonNull
    @Override
    public MesajlarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mesajlar_layout,viewGroup,false);

        mYetki=FirebaseAuth.getInstance();

        return new MesajlarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MesajlarViewHolder mesajlarViewHolder, final int i) {
        String mesajGonderenId=mYetki.getCurrentUser().getUid();
        Mesajlar mesajlar=kullaniciMesajlariListesi.get(i);
        String mesajlarKimdenId=mesajlar.getKimden();
        String mesajTuruKimden=mesajlar.getTipi();
        //Veritabanı Yolu
        kullanicilar_yolu= FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(mesajlarKimdenId);
        kullanicilar_yolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Resim")){
                    String resmiAlici=dataSnapshot.child("Resim").getValue().toString();
                    Picasso.get().load(resmiAlici).placeholder(R.drawable.people).into(mesajlarViewHolder.aliciKullaniciResmi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        mesajlarViewHolder.aliciMesajIcerik.setVisibility(View.GONE);
        mesajlarViewHolder.aliciKullaniciResmi.setVisibility(View.GONE);
        mesajlarViewHolder.gonderenMesajIcerik.setVisibility(View.GONE);
        mesajlarViewHolder.mesajAlanResim.setVisibility(View.GONE);
        mesajlarViewHolder.mesajGonderenResim.setVisibility(View.GONE);
        mesajlarViewHolder.mesajAlanDosya.setVisibility(View.GONE);
        mesajlarViewHolder.mesajGonderenDosya.setVisibility(View.GONE);

        if(mesajTuruKimden.equals("Text")){


            if(mesajlarKimdenId.equals(mesajGonderenId)){

                mesajlarViewHolder.gonderenMesajIcerik.setVisibility(View.VISIBLE);
                mesajlarViewHolder.gonderenMesajIcerik.setText(mesajlar.getMesaj()+"\n"+mesajlar.getZaman());

            }
            else{


                mesajlarViewHolder.aliciKullaniciResmi.setVisibility(View.VISIBLE);
                mesajlarViewHolder.aliciMesajIcerik.setVisibility(View.VISIBLE);
                mesajlarViewHolder.aliciMesajIcerik.setText(mesajlar.getMesaj()+"\n"+mesajlar.getZaman());

            }
        }
        else if(mesajTuruKimden.equals("Resim")) {
            if(mesajlarKimdenId.equals(mesajGonderenId)){
                mesajlarViewHolder.mesajGonderenResim.setVisibility(View.VISIBLE);
                Picasso.get().load(mesajlar.getMesaj()).into(mesajlarViewHolder.mesajGonderenResim);
            }
            else{
                mesajlarViewHolder.aliciKullaniciResmi.setVisibility(View.VISIBLE);
                mesajlarViewHolder.mesajAlanResim.setVisibility(View.VISIBLE);
                Picasso.get().load(mesajlar.getMesaj()).into(mesajlarViewHolder.mesajAlanResim);
            }

        }
        else if(mesajTuruKimden.equals("pdf")||mesajTuruKimden.equals("docx")){
            if(mesajlarKimdenId.equals(mesajGonderenId)){
                mesajlarViewHolder.mesajGonderenDosya.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/pekimesajlasma-97d53.appspot.com/o/Resim%20Dosyalari%2Fhttps%3A%2Fpekimesajlasma-97d53.firebaseio.com%2FMesajlar%2Fpdfler.png?alt=media&token=2989c94e-9307-4ac2-b727-eeb29a3aaf0a")
                .into(mesajlarViewHolder.mesajGonderenDosya);
            }
            else{
                mesajlarViewHolder.aliciKullaniciResmi.setVisibility(View.VISIBLE);
                mesajlarViewHolder.mesajAlanDosya.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/pekimesajlasma-97d53.appspot.com/o/Resim%20Dosyalari%2Fhttps%3A%2Fpekimesajlasma-97d53.firebaseio.com%2FMesajlar%2Fpdfler.png?alt=media&token=2989c94e-9307-4ac2-b727-eeb29a3aaf0a")
                        .into(mesajlarViewHolder.mesajAlanDosya);
            }
            }

        if(mesajlarKimdenId.equals(mesajGonderenId)){
            mesajlarViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(kullaniciMesajlariListesi.get(i).getTipi().equals("pdf")||kullaniciMesajlariListesi.get(i).getTipi().equals("docx")){
                        CharSequence mesajSecenekleri []=new CharSequence[]{
                                "Benden sil",
                                "Mesajı geri al",
                                "Bu belgeyi indir yada görüntüle",
                                "Iptal"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                        builder.setTitle("Mesaj silinsinmi");
                        builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        gonderilenMesajSil(i,mesajlarViewHolder);
                                    }
                                    else if(which==1){
                                        MesajiHerkestenSil(i,mesajlarViewHolder);
                                    }
                                    else if(which==2){
                                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(kullaniciMesajlariListesi.get(i).getMesaj()));
                                        mesajlarViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==3){

                                    }
                            }
                        });
                        builder.show();
                    }
                    else if(kullaniciMesajlariListesi.get(i).getTipi().equals("Text")){
                        CharSequence mesajSecenekleri []=new CharSequence[]{
                                "Benden sil",
                                "Mesajı geri al",
                                "Iptal"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                        builder.setTitle("Mesaj silinsinmi");
                        builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    gonderilenMesajSil(i,mesajlarViewHolder);
                                }
                                else if(which==1){
                                    MesajiHerkestenSil(i,mesajlarViewHolder);
                                }
                                else if(which==2){

                                }
                            }
                        });
                        builder.show();
                    }
                    else if(kullaniciMesajlariListesi.get(i).getTipi().equals("Resim")){
                        CharSequence mesajSecenekleri []=new CharSequence[]{
                                "Benden sil",
                                "Mesajı geri al",
                                "Bu resmi görüntüle",
                                "Iptal"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                        builder.setTitle("Mesaj silinsinmi");
                        builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    gonderilenMesajSil(i,mesajlarViewHolder);
                                }
                                else if(which==1){
                                    MesajiHerkestenSil(i,mesajlarViewHolder);
                                }
                                else if(which==2){
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(kullaniciMesajlariListesi.get(i).getMesaj()));
                                    mesajlarViewHolder.itemView.getContext().startActivity(intent);
                                }
                                if(which==3){

                                }
                            }
                        });
                        builder.show();
                    }





                }
            });
        }
        if(mesajlarKimdenId.equals(mesajGonderenId)){
            mesajlarViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(kullaniciMesajlariListesi.get(i).getTipi().equals("pdf")||kullaniciMesajlariListesi.get(i).getTipi().equals("docx")){
                        CharSequence mesajSecenekleri []=new CharSequence[]{
                                "Benden sil",
                                "Mesajı geri al",
                                "Bu belgeyi indir yada görüntüle",
                                "Iptal"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                        builder.setTitle("Mesaj silinsinmi");
                        builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    gonderilenMesajSil(i,mesajlarViewHolder);
                                }
                                else if(which==1){
                                    MesajiHerkestenSil(i,mesajlarViewHolder);
                                }
                                else if(which==2){
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(kullaniciMesajlariListesi.get(i).getMesaj()));
                                    mesajlarViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(which==3){

                                }
                            }
                        });
                        builder.show();
                    }
                    else if(kullaniciMesajlariListesi.get(i).getTipi().equals("Text")){
                        CharSequence mesajSecenekleri []=new CharSequence[]{
                                "Benden sil",
                                "Mesajı geri al",
                                "Iptal"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                        builder.setTitle("Mesaj silinsinmi");
                        builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    gonderilenMesajSil(i,mesajlarViewHolder);
                                }
                                else if(which==1){
                                    MesajiHerkestenSil(i,mesajlarViewHolder);
                                }
                                else if(which==2){

                                }
                            }
                        });
                        builder.show();
                    }
                    else if(kullaniciMesajlariListesi.get(i).getTipi().equals("Resim")){
                        CharSequence mesajSecenekleri []=new CharSequence[]{
                                "Benden sil",
                                "Mesajı geri al",
                                "Bu resmi görüntüle",
                                "Iptal"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                        builder.setTitle("Mesaj silinsinmi");
                        builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    gonderilenMesajSil(i,mesajlarViewHolder);
                                }
                                else  if(which==1){
                                    MesajiHerkestenSil(i,mesajlarViewHolder);

                                }
                                else if(which==2){
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(kullaniciMesajlariListesi.get(i).getMesaj()));
                                    mesajlarViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else if(which==3){

                                }
                            }
                        });
                        builder.show();
                    }





                }
            });
        }
        else{
                mesajlarViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(kullaniciMesajlariListesi.get(i).getTipi().equals("pdf")||kullaniciMesajlariListesi.get(i).getTipi().equals("docx")){
                            CharSequence mesajSecenekleri []=new CharSequence[]{
                                    "Benden sil",
                                    "Bu belgeyi indir yada görüntüle",
                                    "Iptal"
                            };
                            AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                            builder.setTitle("Mesaj silinsinmi");
                            builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        alinanMesajSil(i,mesajlarViewHolder);
                                    }
                                    else if(which==1){
                                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(kullaniciMesajlariListesi.get(i).getMesaj()));
                                        mesajlarViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else  if(which==2){

                                    }
                                }
                            });
                            builder.show();
                        }
                        else if(kullaniciMesajlariListesi.get(i).getTipi().equals("Text")){
                            CharSequence mesajSecenekleri []=new CharSequence[]{
                                    "Benden sil",
                                    "Iptal"
                            };
                            AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                            builder.setTitle("Mesaj silinsinmi");
                            builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        alinanMesajSil(i,mesajlarViewHolder);
                                    }
                                    else if(which==1){

                                    }
                                }
                            });
                            builder.show();
                        }
                        else if(kullaniciMesajlariListesi.get(i).getTipi().equals("Resim")){
                            CharSequence mesajSecenekleri []=new CharSequence[]{
                                    "Benden sil",
                                    "Bu resmi görüntüle",
                                    "Iptal"
                            };
                            AlertDialog.Builder builder=new AlertDialog.Builder(mesajlarViewHolder.itemView.getContext());
                            builder.setTitle("Mesaj silinsinmi");
                            builder.setItems(mesajSecenekleri, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        alinanMesajSil(i,mesajlarViewHolder);
                                    }
                                    else if(which==1){
                                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(kullaniciMesajlariListesi.get(i).getMesaj()));
                                        mesajlarViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                    else if(which==2){

                                    }
                                }
                            });
                            builder.show();
                        }





                    }
                });
            }
        }



    @Override
    public int getItemCount() {

        return kullaniciMesajlariListesi.size();
    }

    private void gonderilenMesajSil(final int pozisyon,final MesajlarViewHolder holder){
        final DatabaseReference mesajyolu=FirebaseDatabase.getInstance().getReference();
        mesajyolu.child("Mesajlar")
        .child(kullaniciMesajlariListesi.get(pozisyon).getKimden())
        .child(kullaniciMesajlariListesi.get(pozisyon).getKime())
                .child(kullaniciMesajlariListesi.get(pozisyon).getMesajID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Silme işi başarılı.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(holder.itemView.getContext(), "Silinemedi!.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void alinanMesajSil(final int pozisyon,final MesajlarViewHolder holder){
        final DatabaseReference mesajyolu=FirebaseDatabase.getInstance().getReference();
        mesajyolu.child("Mesajlar")
                .child(kullaniciMesajlariListesi.get(pozisyon).getKime())
                .child(kullaniciMesajlariListesi.get(pozisyon).getKimden())
                .child(kullaniciMesajlariListesi.get(pozisyon).getMesajID()).
                removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Silme işi başarılı.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(holder.itemView.getContext(), "Silinemedi!.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void MesajiHerkestenSil(final int pozisyon,final MesajlarViewHolder holder){

        final DatabaseReference mesajyolu=FirebaseDatabase.getInstance().getReference();
        mesajyolu.child("Mesajlar")
                .child(kullaniciMesajlariListesi.get(pozisyon).getKime())
                .child(kullaniciMesajlariListesi.get(pozisyon).getKimden())
                .child(kullaniciMesajlariListesi.get(pozisyon).getMesajID()).
                removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mesajyolu.child("Mesajlar")
                            .child(kullaniciMesajlariListesi.get(pozisyon).getKimden())
                            .child(kullaniciMesajlariListesi.get(pozisyon).getKime())
                            .child(kullaniciMesajlariListesi.get(pozisyon).getMesajID()).
                            removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(holder.itemView.getContext(), "Silme işi başarılı.", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(holder.itemView.getContext(), "Silinemedi!.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

            }
        });

    }
}
