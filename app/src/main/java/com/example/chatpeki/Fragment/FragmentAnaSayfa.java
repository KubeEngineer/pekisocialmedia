package com.example.chatpeki.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chatpeki.R;
import com.example.chatpeki.Adapter.adapterForPosts;
import com.example.chatpeki.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentAnaSayfa extends Fragment {
    private RecyclerView postView;
    private List<Post> postList = new ArrayList<Post>();
    private List<String> arkadasList;
    private View view;
    private adapterForPosts postAdapter;
    private ProgressBar progressBar;

    public FragmentAnaSayfa() {
    }

    public void init() {
        postView = view.findViewById(R.id.PostView);
        postView = (RecyclerView) view.findViewById(R.id.PostView);
        progressBar = view.findViewById(R.id.progressBar);
        postList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ana_sayfa_fragment, container, false);
        init();
        postView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postView.setLayoutManager(linearLayoutManager);
        postView.getRecycledViewPool().setMaxRecycledViews(0,0);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        postList.clear();
        kontrolArkadaslar();
        postAdapter = new adapterForPosts(getContext(), postList, getFragmentManager());
        postView.setAdapter(postAdapter);
    }

    private void kontrolArkadaslar() {
        arkadasList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Arkadaslar")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arkadasList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    arkadasList.add(snapshot.getKey());
                    System.out.println(snapshot.getKey());
                }
                arkadasList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                postlariGetir();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void postlariGetir() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Postlar");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Post post = dataSnapshot.getValue(Post.class);
                for (String id : arkadasList) {
                    if (post.getGonderen_id().equals(id)) {
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                Post post = dataSnapshot.getValue(Post.class);
                for (int i = 0; i < postList.size(); i++) {
                    if (postList.get(i).getGonderi_id().equals(post.getGonderi_id())) {
                        postList.remove(postList.get(i));
                        postAdapter.notifyItemRemoved(i);
                        postAdapter.notifyItemRangeChanged(i, postList.size());
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
