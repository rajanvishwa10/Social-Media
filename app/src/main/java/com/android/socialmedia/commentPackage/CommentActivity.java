package com.android.socialmedia.commentPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.socialmedia.notificationPackage.NotificationClass;
import com.android.socialmedia.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    String caption, username, image, profilepic, comment, currentUsername, imagedate;
    CircleImageView circleImageView;
    Toolbar toolbar;
    TextView textView, textView2;
    EditText editText;
    Button button;
    List<Comment> commentList;
    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    NotificationClass notificationClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        notificationClass = new NotificationClass();

        caption = getIntent().getStringExtra("caption");
        username = getIntent().getStringExtra("username");
        image = getIntent().getStringExtra("image");
        profilepic = getIntent().getStringExtra("profilepic");
        imagedate = getIntent().getStringExtra("imagedate");

        circleImageView = findViewById(R.id.profilepic);
        toolbar = findViewById(R.id.toolbar5);

        textView = findViewById(R.id.username);
        textView2 = findViewById(R.id.caption);

        editText = findViewById(R.id.comment);
        button = findViewById(R.id.send);

        textView.setText(username + " : ");
        textView2.setText(caption);

        commentList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUsername = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            if (profilepic.isEmpty()) {
                circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_24));
            } else {
                Glide.with(getApplicationContext()).load(profilepic).into(circleImageView);
            }
        } catch (NullPointerException e) {
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = editText.getText().toString().trim();
                //
                if (comment.length() > 0) {
                    setComment(comment.trim(), imagedate);
                }
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Images").
                orderByChild("Image").equalTo(image).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().child("userComment").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                commentList.clear();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Comment comment = snapshot1.getValue(Comment.class);
                                    commentList.add(comment);
                                }
                                commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                                recyclerView.setAdapter(commentAdapter);
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

    private void setComment(String comment, String imagedate) {
        final Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        final String formattedDate = df.format(c);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Images").
                orderByChild("Image").equalTo(image).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Map<String, Object> updates = new HashMap<String, Object>();
                    updates.put("comment", comment);
                    updates.put("commenter", currentUsername);
                    snapshot1.getRef().child("userComment").child(formattedDate).setValue(updates).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    editText.setText(null);
                                    if (!username.equals(currentUsername)) {
                                        notificationClass.setNotification(username, currentUsername, currentUsername + " commented : " + comment, image, caption, imagedate, getApplicationContext());
                                    }
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