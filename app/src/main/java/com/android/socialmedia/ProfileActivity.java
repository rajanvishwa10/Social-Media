package com.android.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView textView, textView2, textView3, textView4, textView5;
    CircleImageView circleImageView;
    String userName;
    int following, followers;
    Button button, button2;
    List<ImageList> imageList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    GalleryImageAdapter galleryImageAdapter;
    RelativeLayout followerRelative, followingRelative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String username = getIntent().getStringExtra("username");
        read(username);

        textView = findViewById(R.id.username);
        textView2 = findViewById(R.id.name);
        textView3 = findViewById(R.id.post);
        textView4 = findViewById(R.id.followers);
        textView5 = findViewById(R.id.following);
        circleImageView = findViewById(R.id.image);
        button = findViewById(R.id.follow);
        button2 = findViewById(R.id.unfollow);
        textView.setText(username);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        recyclerView = findViewById(R.id.recyclerView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        followerRelative = findViewById(R.id.followerRelative);
        followingRelative = findViewById(R.id.followingRelative);
        followerRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("string","Followers");
                startActivity(intent);
            }
        });
        followingRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("string","Following");
                startActivity(intent);
            }
        });

        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    following = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").getValue(Integer.class);
                    userName = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    if (userName.equals(username)) {
                        button.setVisibility(View.GONE);
                        button2.setVisibility(View.GONE);
                    }
                    DatabaseReference databaseReference1 = database.getReference().child(userName).child("following");
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    String followingUsername = dataSnapshot1.child("followingUsername").getValue(String.class);
                                    System.out.println(followingUsername);
                                    if (followingUsername.equals(username)) {
                                        button.setVisibility(View.GONE);
                                        button2.setVisibility(View.VISIBLE);
                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                button.setVisibility(View.VISIBLE);
                                                button2.setVisibility(View.GONE);
                                                Toast.makeText(ProfileActivity.this, "Unfollow", Toast.LENGTH_SHORT).show();
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                        .child(userName)
                                                        .child("following");
                                                Query query = ref.orderByChild("followingUsername").equalTo(followingUsername);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                            dataSnapshot2.getRef().removeValue();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                        .child(username).child("followers");
                                                Query query2 = reference.orderByChild("followerUsername").equalTo(userName);
                                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                            dataSnapshot2.getRef().removeValue();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").
                                                        child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                Map<String, Object> update = new HashMap<>();
                                                update.put("following", following - 1);
                                                databaseReference1.updateChildren(update);

                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Users").orderByChild("Username").equalTo(username)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                                                    snapshot1.getRef().child("followers").setValue(followers-1);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Failed to read value
            }
        });


        imageList = new ArrayList<>();
        imageList.clear();
        layoutManager = new GridLayoutManager(ProfileActivity.this, 3);

        recyclerView.setLayoutManager(layoutManager);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                final String formattedDate = df.format(c);
                button.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(userName)
                        .child("following")
                        .child(formattedDate);

                Map<String, Object> updates = new HashMap<>();

                updates.put("followingUsername", username);
                ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Following", Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(username).child("followers").child(formattedDate);
                Map<String, Object> updates2 = new HashMap<>();

                updates2.put("followerUsername", userName);
                reference.updateChildren(updates2);

                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String, Object> update = new HashMap<>();
                update.put("following", following + 1);
                databaseReference1.updateChildren(update);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Users").orderByChild("Username").equalTo(username)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                    snapshot1.getRef().child("followers").setValue(followers+1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }
        });
    }

    private void read1(String username){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("Images")
                .child(username);
        Query query = databaseReference.orderByChild("username").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ImageList imageList1 = dataSnapshot.getValue(ImageList.class);
                        imageList.add(imageList1);
                        Collections.reverse(imageList);
                        System.out.println("Images = " + dataSnapshot.child("Image").getValue(String.class));
                    }
                    galleryImageAdapter = new GalleryImageAdapter(ProfileActivity.this, imageList, username);
                    recyclerView.setAdapter(galleryImageAdapter);
                } else {
                    System.out.println(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void read(String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Users").orderByChild("Username").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String fullName = dataSnapshot.child("FullName").getValue(String.class);
                        long post = dataSnapshot.child("post").getValue(Integer.class);
                        followers = dataSnapshot.child("followers").getValue(Integer.class);
                        long followings = dataSnapshot.child("following").getValue(Integer.class);
                        String url = dataSnapshot.child("profileImage").getValue(String.class);

                        textView2.setText(fullName);
                        textView3.setText("" + post);
                        textView4.setText("" + followers);
                        textView5.setText("" + followings);

                        Glide.with(getApplicationContext()).load(url).into(circleImageView);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void back(View view) {
        back();
    }

    private void back() {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageList.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageList.clear();
        read1(getIntent().getStringExtra("username"));
    }
}