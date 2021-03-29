package com.android.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatActivity extends AppCompatActivity {
    CardView cardView;
    EditText editText;
    LinearLayout imageLinearLayout, videoLinearLayout, docLinearLayout;
    String username, currentUsername;
    Toolbar toolbar;
    DatabaseReference myRef;
    CircleImageView circleImageView;
    MessageAdapter messageAdapter;
    List<Chats> chats;
    RecyclerView recyclerView;
    ValueEventListener valueEventListener;
    DatabaseReference databaseReference;
    ImageButton imageButton, imageButton2;
    String url = "https://fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        requestQueue = Volley.newRequestQueue(this);

        cardView = findViewById(R.id.cardView);
        imageButton = findViewById(R.id.imageButton1);
        imageButton2 = findViewById(R.id.imageButton2);

        imageLinearLayout = findViewById(R.id.image);
        videoLinearLayout = findViewById(R.id.video);
        docLinearLayout = findViewById(R.id.documents);

        recyclerView = findViewById(R.id.recycler);
        chats = new ArrayList<>();
        circleImageView = findViewById(R.id.circleImageView);

        editText = findViewById(R.id.sendmess);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.INVISIBLE);
                imageButton.setVisibility(View.VISIBLE);
                imageButton2.setVisibility(View.GONE);
            }
        });
        toolbar = findViewById(R.id.toolbar);
        username = getIntent().getStringExtra("username");
        toolbar.setTitle(username);
        read(username);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("username", username);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUsername = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("username", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", currentUsername);
                    editor.apply();
                    readMessages(currentUsername, username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });

        imageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        videoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideo();
            }
        });
        docLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent.createChooser(intent, "Select file"), 4);
            }
        });
        seenMessage(username);
    }

    private void SelectImage() {
        final CharSequence[] items = {"Gallery", "Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select file"), 1);
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void selectVideo() {
        final CharSequence[] items = {"Gallery", "Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Video");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(intent, 2);
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("video/*");
                    startActivityForResult(intent.createChooser(intent, "Select file"), 3);
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void read(String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Users").orderByChild("Username").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String url = dataSnapshot.child("profileImage").getValue(String.class);
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

    public void sendmess(View view) {
        String message = editText.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Can't send Empty Messages", Toast.LENGTH_SHORT).show();
        } else {
            sendMessage(currentUsername, username, message);
        }
    }

    private void sendMessage(String currentUsername, String username, String message) {
        final Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy h:mm:ss a", Locale.getDefault());
        final String formattedDate = df.format(c);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages");
        //.child(Sender + " " + Receiver)
        //.child(Sender + " " + Receiver + " " + formattedDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy h:mm:ss:SSS", Locale.getDefault());
        String date = dateFormat.format(c);
        //System.out.println(date);

        Map<String, Object> messages = new HashMap<>();
        messages.put("Sender", currentUsername);
        messages.put("Receiver", username);
        messages.put("Message", message);
        messages.put("Date", formattedDate);
        messages.put("isseen", false);
        databaseReference.child(formattedDate).setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    sendNotification(message, username, currentUsername);
                    final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(currentUsername).child(username);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("date", date);
                    databaseReference1.updateChildren(hashMap);

                    final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(username).child(currentUsername);

                    databaseReference2.updateChildren(hashMap);
                    editText.setText(null);
                } else {
                    System.out.println(task);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(currentUsername).child(username);

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    databaseReference1.child("id").setValue(username);
                    databaseReference1.child("date").setValue(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(username).child(currentUsername);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    databaseReference2.child("id").setValue(currentUsername);
                    databaseReference2.child("date").setValue(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendNotification(String message, String receiver, String sender) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/topics/" + receiver);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("title", sender);
            jsonObject1.put("body", sender + " : " + message);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("type","message");

            jsonObject.put("notification", jsonObject1);
            jsonObject.put("data", jsonObject2);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("response" + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("content-type", "application/json");
                    map.put("authorization", "key = AAAAYtXPhzM:APA91bFbazWBK9wl73zbOu1nDLsTxINZVMSYl-l74vdaqREPATUkzzCZLFJPJvoDlDaWsx30bauUCiPdz4P4Mx2XKcVmhXIVUQHVS-irRdSqdI9SS5PovOoTWeby1Du1t3nK0Ep7PGxA");

                    return map;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void seenMessage(final String username) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Messages");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    if (chat.getReceiver().equals(currentUsername) && chat.getSender().equals(username)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                        System.out.println("isseen");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages(final String currentUsername, final String username) {
        myRef = FirebaseDatabase.getInstance().getReference("Messages");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chats.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Chats chat = snapshot1.getValue(Chats.class);
                        if (chat.getSender().equals(currentUsername) && chat.getReceiver().equals(username) ||
                                chat.getSender().equals(username) && chat.getReceiver().equals(currentUsername)) {

                            chats.add(chat);

                        }
                        messageAdapter = new MessageAdapter(UserChatActivity.this, chats);
                        recyclerView.setAdapter(messageAdapter);
                        messageAdapter.notifyDataSetChanged();
                    }
                    //System.out.println("snap"+snapshot.getChildren());

                } else {
                    Toast.makeText(getApplicationContext(), "Cant get messages", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserChatActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        databaseReference.removeEventListener(valueEventListener);
    }


    public void attachDocument(View view) {
        imageButton2.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
    }

    public void visibleCardview(View view) {
        imageButton.setVisibility(View.VISIBLE);
        imageButton2.setVisibility(View.GONE);
        cardView.setVisibility(View.INVISIBLE);
    }
}