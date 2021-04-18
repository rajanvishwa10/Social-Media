package com.android.socialmedia.likePackage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.socialmedia.profilePackage.ProfileActivity;
import com.android.socialmedia.R;
import com.android.socialmedia.followPackage.followList;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    private final Context context;
    private final List<followList> userList;

    public LikeAdapter(Context context, List<followList> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follow_list, parent, false);
        return new LikeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeAdapter.ViewHolder holder, int position) {
        followList followList = userList.get(position);
        holder.textView.setText(followList.getLikerUsername());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = databaseReference.orderByChild("Username").equalTo(followList.getLikerUsername());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        holder.textView2.setText(dataSnapshot.child("FullName").getValue(String.class));
                        Glide.with(context).load(dataSnapshot.child("profileImage").
                                getValue(String.class)).into(holder.circleImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userList.clear();
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("username", followList.getLikerUsername());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2;
        CircleImageView circleImageView;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.username);
            textView2 = itemView.findViewById(R.id.fullname);
            circleImageView = itemView.findViewById(R.id.profilepic);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
