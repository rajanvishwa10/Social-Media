package com.android.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadImageActivity extends AppCompatActivity {
    TextView textView, textView2, textView3, textView4, commenterUsername;
    LinearLayout linearLayout;
    CircleImageView circleImageView, commenterProfilepic;
    ImageButton button, button2, button3;
    String username, Image, currentUsername, url;
    int likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        commenterProfilepic = findViewById(R.id.Commenterprofilepic);
        commenterUsername = findViewById(R.id.Commenterusername);
        linearLayout = findViewById(R.id.commentLayout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUsername = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Username").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageView imageView = findViewById(R.id.imageView);

        circleImageView = findViewById(R.id.profilepic);
        textView = findViewById(R.id.username);
        textView2 = findViewById(R.id.username2);
        textView3 = findViewById(R.id.date);
        textView4 = findViewById(R.id.likescount);

        button = findViewById(R.id.like);
        button2 = findViewById(R.id.unlike);
        button3 = findViewById(R.id.comment);

        username = getIntent().getStringExtra("username");

        TextView textView1 = findViewById(R.id.caption);

        Image = getIntent().getStringExtra("image");

        textView.setText(username);
        textView2.setText(username + " : ");

        String caption = getIntent().getStringExtra("caption");

        Glide.with(this).load(Image).into(imageView);
        Zoomy.Builder builder = new Zoomy.Builder(this).target(imageView);
        builder.register();
        textView1.setText(caption);
        String date = getIntent().getStringExtra("date");
        String[] dateSplit = date.split("\\s+");
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
            Date newDate = spf.parse(dateSplit[0]);
            spf = new SimpleDateFormat("dd, MMM");
            date = spf.format(newDate);
            textView3.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        read(username);
        read2(username);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like();
            }
        });
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadImageActivity.this, ListActivity.class);
                intent.putExtra("string", "Likes");
                intent.putExtra("username", username);
                intent.putExtra("image", Image);
                startActivity(intent);
            }

        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("image", Image);
                intent.putExtra("caption", caption);
                intent.putExtra("profilepic", url);
                startActivity(intent);
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Images").child(username).
                orderByChild("Image").equalTo(Image).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().child("userComment").limitToLast(1).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    String commenter = snapshot1.child("commenter").getValue(String.class);
                                    String comment = snapshot1.child("comment").getValue(String.class);
                                    commenterUsername.setText(Html.fromHtml("<medium><font color='black'>" + commenter + " : " + "</font></medium>"
                                            + comment));
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    Query query = databaseReference.orderByChild("Username").equalTo(commenter);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Glide.with(getApplicationContext()).load(dataSnapshot.child("profileImage").
                                                            getValue(String.class)).into(commenterProfilepic);
                                                    linearLayout.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                                                            intent.putExtra("username", username);
                                                            intent.putExtra("image", Image);
                                                            intent.putExtra("caption", caption);
                                                            intent.putExtra("profilepic", url);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void read2(String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Images").child(username).
                orderByChild("Image").equalTo(Image).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    likes = dataSnapshot.child("likes").getValue(Integer.class);
                    textView4.setText(likes+" likes");
                    dataSnapshot.getRef().child("userLiked").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    String likerUsername = snapshot1.child("likerUsername").getValue(String.class);

                                    System.out.println(likerUsername);
                                    if (likerUsername.equals(currentUsername)) {
                                        button.setVisibility(View.GONE);
                                        button2.setVisibility(View.VISIBLE);
                                        button2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                unlike();
                                            }
                                        });
                                        break;
                                    } else {
                                        button.setVisibility(View.VISIBLE);
                                        button2.setVisibility(View.GONE);
                                    }

                                }
                            } else {
                                System.out.println("snapshot not exists");
                                button.setVisibility(View.VISIBLE);
                                button2.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void read(String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("Users");
        databaseReference.orderByChild("Username").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        url = dataSnapshot.child("profileImage").getValue(String.class);
                        try {
                            if (url.isEmpty()) {
                                circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_24));
                            } else {
                                Glide.with(getApplicationContext()).load(url).into(circleImageView);
                            }
                        } catch (NullPointerException e) {
                        }
                    }
                } else {
                    System.out.println("No data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void like() {
        Toast.makeText(this, "Liked", Toast.LENGTH_SHORT).show();
        button.setVisibility(View.GONE);
        button2.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Images").child(username).
                orderByChild("Image").equalTo(Image).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    snapshot1.getRef().child("likes").setValue(likes + 1);
                    int like = likes + 1;
                    Map<String, Object> updates = new HashMap<String, Object>();
                    updates.put("likerUsername", currentUsername);
                    updates.put("like", like);
                    snapshot1.getRef().child("userLiked").child(currentUsername).setValue(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void unlike() {
        button.setVisibility(View.VISIBLE);
        button2.setVisibility(View.GONE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Images").child(username).
                orderByChild("Image").equalTo(Image).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    snapshot1.getRef().child("likes").setValue(likes - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference1.child("Images").child(username).
                orderByChild("Image").equalTo(Image).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                    dataSnapshot2.getRef().child("userLiked")
                            .orderByChild("likerUsername").equalTo(currentUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}