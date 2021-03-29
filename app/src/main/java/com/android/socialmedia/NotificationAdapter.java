package com.android.socialmedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<Notification> notificationList;
    String dp, userName;
    NotificationClass notificationClass;
    int following, followers;

    NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_notification, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        notificationClass = new NotificationClass();
        Notification notification = notificationList.get(position);
        String username = notification.getUsername();
        String senderUsername = notification.getSenderUsername();
        String imagedate = notification.getImageDate();
        String image = notification.getImageUrl();
        String date = notification.getDate();
        String caption = notification.getCaption();
        String message = notification.getMessage();
        String type = notification.getType();
        read(senderUsername);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = databaseReference.orderByChild("Username").equalTo(senderUsername);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dp = dataSnapshot.child("profileImage").
                                getValue(String.class);
                        try {
                            Glide.with(context).load(dp).into(holder.circleImageView);

                            Glide.with(context).load(dp).into(holder.circleImageView2);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (type.equals("like")) {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.linearLayout2.setVisibility(View.GONE);
        } else {
            holder.linearLayout2.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
            holder.username2.setText(senderUsername);
            holder.message2.setText(message);
            holder.linearLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("username", senderUsername);
                    context.startActivity(intent);
                }
            });
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
            myRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        following = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").getValue(Integer.class);
                        userName = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(userName).child("following");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                        String followingUsername = dataSnapshot1.child("followingUsername").getValue(String.class);
                                        System.out.println(followingUsername);
                                        if (followingUsername.equals(senderUsername)) {
                                            holder.follow.setVisibility(View.GONE);
                                            holder.unfollow.setVisibility(View.VISIBLE);
                                            holder.unfollow.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    holder.follow.setVisibility(View.VISIBLE);
                                                    holder.unfollow.setVisibility(View.GONE);
                                                    Toast.makeText(context, "Unfollow", Toast.LENGTH_SHORT).show();
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
                                                            .child(senderUsername).child("followers");
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
                                                    databaseReference.child("Users").orderByChild("Username").equalTo(senderUsername)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                        snapshot1.getRef().child("followers").setValue(followers - 1);
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
                        Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //Failed to read value
                }
            });
            holder.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.unfollow.setVisibility(View.VISIBLE);
                    holder.follow.setVisibility(View.GONE);
                    final Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    final String formattedDate = df.format(c);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child(userName)
                            .child("following")
                            .child(formattedDate);

                    Map<String, Object> updates = new HashMap<>();

                    updates.put("followingUsername", senderUsername);
                    ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Following", Toast.LENGTH_SHORT).show();
                            //notificationClass.setNotification(senderUsername, username, username + " started following you", context);
                        }
                    });

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child(senderUsername).child("followers").child(formattedDate);
                    Map<String, Object> updates2 = new HashMap<>();

                    updates2.put("followerUsername", userName);
                    reference.updateChildren(updates2);

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map<String, Object> update = new HashMap<>();
                    update.put("following", following + 1);
                    databaseReference1.updateChildren(update);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Users").orderByChild("Username").equalTo(senderUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        snapshot1.getRef().child("followers").setValue(followers + 1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            });

        }

        holder.username.setText(senderUsername);
        holder.message.setText(message);
        Glide.with(context).load(image).into(holder.imageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UploadImageActivity.class);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) context, holder.imageView, holder.imageView.getTransitionName());
                intent.putExtra("image", image);
                intent.putExtra("caption", caption);
                intent.putExtra("date", imagedate);
                intent.putExtra("username", username);
                intent.putExtra("date", date);
                context.startActivity(intent, activityOptionsCompat.toBundle());
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
                        followers = dataSnapshot.child("followers").getValue(Integer.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView, circleImageView2;
        TextView username, username2, message, message2;
        LinearLayout linearLayout, linearLayout2;
        ImageView imageView;
        Button follow, unfollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilepic);
            circleImageView2 = itemView.findViewById(R.id.profilepic2);
            username = itemView.findViewById(R.id.username);
            username2 = itemView.findViewById(R.id.username2);
            follow = itemView.findViewById(R.id.follow);
            unfollow = itemView.findViewById(R.id.unfollow);
            message = itemView.findViewById(R.id.message);
            message2 = itemView.findViewById(R.id.message2);
            imageView = itemView.findViewById(R.id.image);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            linearLayout2 = itemView.findViewById(R.id.linearLayout2);
        }
    }
}
