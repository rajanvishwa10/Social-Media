package com.android.socialmedia;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final Context context;
    private final List<User> userList;

    public SearchAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_profile, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        String username = user.getUsername();
        String profilePic = user.getProfileImage();
        String fullname = user.getFullName();

        holder.textView.setText(username);
        holder.textView2.setText(fullname);
        Glide.with(context).load(profilePic).into(holder.circleImageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userList.clear();
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("username", username);
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
