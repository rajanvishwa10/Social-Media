package com.android.socialmedia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private final Context context;
    private final List<MessageUser> userList;
    String lastmess = "default";

    public MessageListAdapter(Context context, List<MessageUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_profile, parent, false);
        return new MessageListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.ViewHolder holder, int position) {
        MessageUser user = userList.get(position);
        String sender = user.getId();
        holder.textView.setText(sender);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Users").orderByChild("Username").equalTo(sender);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String url = dataSnapshot.child("profileImage").getValue(String.class);

                        Glide.with(context).load(url).into(holder.circleImageView);
                    }
                } else {
                    Toast.makeText(context, "No user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    Chats chat = dataSnapshot1.getValue(Chats.class);
                    SharedPreferences sharedPreferences = context.getSharedPreferences("username", Context.MODE_PRIVATE);
                    String currentUsername = sharedPreferences.getString("username", "");
                    if (chat.getReceiver().equals(user.getId()) && chat.getSender().equals(currentUsername) ||
                            chat.getReceiver().equals(currentUsername) && chat.getSender().equals(user.getId())) {
                        if (chat.getType().equals("image")) {
                            lastmess = "Photo";
                        } else {
                            lastmess = chat.getMessage();
                        }
                        holder.textView2.setText(lastmess);
                        if (!chat.getSender().equals(currentUsername)) {
                            if (!chat.isIsseen()) {
                                count++;
                            }
                        }
                    }
                }

                if (count > 0) {
                    holder.textView2.setTypeface(holder.textView2.getTypeface(), Typeface.BOLD);
                    holder.countTextView.setVisibility(View.VISIBLE);
                    holder.countTextView.setText("" + count);
                } else {
                    holder.textView2.setTypeface(holder.textView2.getTypeface(), Typeface.NORMAL);
                    holder.countTextView.setVisibility(View.GONE);
                }

                if ("default".equals(lastmess)) {
                    holder.textView2.setVisibility(View.GONE);
                    holder.linearLayout.setVisibility(View.GONE);
                } else {
                    holder.textView2.setTypeface(holder.textView2.getTypeface(), Typeface.NORMAL);
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    holder.textView2.setVisibility(View.VISIBLE);
                    holder.textView2.setText(lastmess);
                    if(lastmess.equals("Photo")) {
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_24);
                        holder.textView2.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    }
                }
                lastmess = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        holder.textView2.setText(message);
//        Glide.with(context).load(profilePic).into(holder.circleImageView);
//
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserChatActivity.class);
                intent.putExtra("username", sender);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2, countTextView;
        CircleImageView circleImageView;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.username);
            textView2 = itemView.findViewById(R.id.fullname);
            countTextView = itemView.findViewById(R.id.unseenCount);
            circleImageView = itemView.findViewById(R.id.profilepic);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
