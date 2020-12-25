package com.android.socialmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadImageActivity extends AppCompatActivity {
    TextView textView, textView2, textView3;
    CircleImageView circleImageView;

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

        ImageView imageView = findViewById(R.id.imageView);
        circleImageView = findViewById(R.id.profilepic);
        textView = findViewById(R.id.username);
        textView2 = findViewById(R.id.username2);
        textView3 = findViewById(R.id.date);

        TextView textView1 = findViewById(R.id.caption);

        Glide.with(this).load(getIntent().getStringExtra("image")).into(imageView);
        textView1.setText(getIntent().getStringExtra("caption"));
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

        read();
    }

    private void read() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    String url = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profileImage").getValue(String.class);

                    textView.setText(username);
                    textView2.setText(username + " : ");
                    try {
                        if (url.isEmpty()) {
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_24));
                        } else {
                            Glide.with(UploadImageActivity.this).load(url).into(circleImageView);
                        }
                    } catch (NullPointerException e) {
                    }


                } else {
                    Toast.makeText(UploadImageActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Failed to read value
                // Toasty.error(ProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}