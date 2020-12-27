package com.android.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText editText, editText2, editText3;
    String name, username, bio;
    Button button;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editText = findViewById(R.id.name);
        editText2 = findViewById(R.id.username);
        editText3 = findViewById(R.id.bio);

        button = findViewById(R.id.update);

        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Updating . . . ");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        read();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                Map<String, Object> updates = new HashMap<String, Object>();

                updates.put("FullName", editText.getText().toString().trim());
                updates.put("Username", editText2.getText().toString().trim());
                updates.put("Bio", editText3.getText().toString().trim());

                ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.cancel();
                        Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void read() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("FullName").getValue(String.class);
                    username = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    bio = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Bio").getValue(String.class);
//                    long post = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("post").getValue(Integer.class);
//                    long followers = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers").getValue(Integer.class);
//                    long followings = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").getValue(Integer.class);
//                    String url = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profileImage").getValue(String.class);

                    editText.setText(name);
                    editText2.setText(username);
                    try {
                        editText3.setText(bio);
                    } catch (NullPointerException e) {
                        editText3.setText(bio);
                    }

//                    try {
//                        if (url.isEmpty()) {
//                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_24));
//                        } else {
//                            Glide.with(getContext()).load(url).into(imageView);
//                        }
//                    } catch (NullPointerException e) {
//                    }


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