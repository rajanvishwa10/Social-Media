package com.android.socialmedia;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.Dataset;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    FloatingActionButton floatingActionButton;
    EditText editText;
    List<User> userList;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        userList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        floatingActionButton = view.findViewById(R.id.search);
        editText = view.findViewById(R.id.searchEditText);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userList.clear();
                String text = editText.getText().toString().trim();
                search(text);
            }
        });
        return view;
    }

    private void search(String text) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = databaseReference.orderByChild("Username").startAt(text).endAt(text + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        userList.add(user);
                    }

                } else {
                    userList.clear();
                    Toast.makeText(getContext(), "No user", Toast.LENGTH_SHORT).show();
                }
                searchAdapter = new SearchAdapter(getContext(), userList);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        userList.clear();
    }
}