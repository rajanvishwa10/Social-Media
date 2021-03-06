package com.android.socialmedia.homePackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.android.socialmedia.R;
import com.android.socialmedia.commentPackage.CommentActivity;
import com.android.socialmedia.likePackage.ListActivity;
import com.android.socialmedia.notificationPackage.NotificationClass;
import com.android.socialmedia.profilePackage.ProfileActivity2;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainpageImageAdapter extends RecyclerView.Adapter<MainpageImageAdapter.ViewHolder> {

    private final Context context;
    private final List<mainpageImagelist> userList;
    String currentUsername;
    int likes;
    String url, date;
    NotificationClass notificationClass;

    public MainpageImageAdapter(Context context, List<mainpageImagelist> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MainpageImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mainpage_image, parent, false);
        return new MainpageImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainpageImageAdapter.ViewHolder holder, int position) {
        notificationClass = new NotificationClass();
        mainpageImagelist comment = userList.get(position);
        String username = comment.getUsername();
        String image = comment.getImage();
        String caption = comment.getCaption();
        date = comment.getData();

        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users");
        myRef2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUsername = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(currentUsername).child("following");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String followingUsername = dataSnapshot.child("followingUsername").getValue(String.class);
                                    if (followingUsername.equals(comment.getUsername())) {
                                        holder.constraintLayout.setVisibility(View.VISIBLE);
                                        break;
                                    } else if (comment.getUsername().equals(currentUsername)) {
                                        holder.constraintLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.constraintLayout.setVisibility(View.GONE);
                                    }
                                }
                            }else{
                                holder.constraintLayout.setVisibility(View.GONE);
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

        try {
            String[] dateSplit = date.split("\\s+");
            SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
            Date newDate = spf.parse(dateSplit[0]);
            spf = new SimpleDateFormat("dd, MMM");
            date = spf.format(newDate);
            holder.date.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (caption.length() > 0) {
            holder.linearLayout3.setVisibility(View.VISIBLE);
            holder.caption.setText(Html.fromHtml("<medium><font color='black'>" + username + " : " + "</font></medium>"
                    + caption));
        } else {
            holder.linearLayout3.setVisibility(View.GONE);
        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUsername = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    url = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profileImage").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Images").
                orderByChild("Image").equalTo(image).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    likes = dataSnapshot.child("likes").getValue(Integer.class);
                    holder.textView2.setText(likes + " likes");
                    dataSnapshot.getRef().child("userComment").limitToLast(1).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    String commenter = snapshot1.child("commenter").getValue(String.class);
                                    String comment = snapshot1.child("comment").getValue(String.class);
                                    if (comment.length() > 0) {
                                        holder.commentlinearlayout.setVisibility(View.VISIBLE);
                                        holder.commenterUsername.setText(Html.fromHtml("<medium><font color='black'>" + commenter + " : " + "</font></medium>"
                                                + comment));
                                    } else {
                                        holder.commentlinearlayout.setVisibility(View.GONE);
                                    }

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    Query query = databaseReference.orderByChild("Username").equalTo(commenter);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    try {
                                                        String profile = dataSnapshot.child("profileImage").
                                                                getValue(String.class);
                                                        Glide.with(context).load(profile).into(holder.commenterProfilepic);

                                                        holder.commentlinearlayout.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(context, CommentActivity.class);
                                                                intent.putExtra("username", username);
                                                                intent.putExtra("image", image);
                                                                intent.putExtra("caption", caption);
                                                                intent.putExtra("profilepic", profile);
                                                                intent.putExtra("imagedate", date);
                                                                context.startActivity(intent);
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                            } else {
                                holder.commentlinearlayout.setVisibility(View.GONE);
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

        holder.textView.setText(username);
        holder.textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("string", "Likes");
                intent.putExtra("username", username);
                intent.putExtra("image", image);
                context.startActivity(intent);
            }
        });
        Glide.with(context).load(comment.getImage()).placeholder(R.drawable.ic_placeholder).into(holder.imageView);
        Zoomy.Builder builder = new Zoomy.Builder((Activity) holder.imageView.getContext()).target(holder.imageView);
        builder.register();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = databaseReference.orderByChild("Username").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            String dp = dataSnapshot.child("profileImage").
                                    getValue(String.class);
                            Glide.with(context).load(dp).into(holder.circleImageView);
                            Glide.with(context).load(dp).into(holder.circleImageViewDp);
                            holder.linearLayout3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, CommentActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("image", image);
                                    intent.putExtra("caption", caption);
                                    intent.putExtra("profilepic", dp);
                                    context.startActivity(intent);
                                }
                            });


                        } catch (Exception e) {
                            holder.circleImageViewDp.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_person_24));
                            holder.circleImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_person_24));
                        }

                    }
                } else {
                    System.out.println(false);
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
                Intent intent = new Intent(context, ProfileActivity2.class);
                intent.putExtra("username", comment.getUsername());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
        databaseReference2.child("Images").
                orderByChild("Image").equalTo(image).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().child("userLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    String likerUsername = snapshot1.child("likerUsername").getValue(String.class);
                                    if (likerUsername.equals(currentUsername)) {
                                        holder.like.setVisibility(View.GONE);
                                        holder.unlike.setVisibility(View.VISIBLE);
                                        holder.unlike.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                holder.like.setVisibility(View.VISIBLE);
                                                holder.unlike.setVisibility(View.GONE);
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Images").
                                                        orderByChild("Image").equalTo(image).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                            int likeCount = snapshot1.child("likes").getValue(Integer.class);
                                                            snapshot1.getRef().child("likes").setValue(likeCount - 1).addOnSuccessListener(
                                                                    new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                                            databaseReference1.child("Images").
                                                                                    orderByChild("Image").equalTo(image).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                                                        holder.textView2.setText(dataSnapshot2.child("likes").getValue(Integer.class) + " likes");
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
                                                            );
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                            }
                                        });
                                        break;
                                    } else {
                                        holder.like.setVisibility(View.VISIBLE);
                                        holder.unlike.setVisibility(View.GONE);
                                    }

                                }
                            } else {
                                System.out.println("snapshot not exists");
                                holder.like.setVisibility(View.VISIBLE);
                                holder.unlike.setVisibility(View.GONE);
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


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.like.setVisibility(View.GONE);
                holder.unlike.setVisibility(View.VISIBLE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Images").
                        orderByChild("Image").equalTo(image).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            int likeCount = snapshot1.child("likes").getValue(Integer.class);
                            snapshot1.getRef().child("likes").setValue(likeCount + 1);
                            Map<String, Object> updates = new HashMap<String, Object>();
                            updates.put("likerUsername", currentUsername);
                            updates.put("like", likeCount + 1);

                            snapshot1.getRef().child("userLiked").child(currentUsername).setValue(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (!username.equals(currentUsername)) {
                                                notificationClass.setNotification(username, currentUsername, currentUsername + " liked your photo", image, caption, date, context);
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
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("image", image);
                intent.putExtra("caption", caption);
                intent.putExtra("profilepic", url);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2, commenterUsername, date, caption;
        CircleImageView circleImageView, commenterProfilepic, circleImageViewDp;
        LinearLayout linearLayout, commentlinearlayout, linearLayout3;
        ImageView imageView;
        ConstraintLayout constraintLayout;
        ImageButton like, unlike, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.username);
            textView2 = itemView.findViewById(R.id.likescount);
            circleImageView = itemView.findViewById(R.id.profilepic);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            linearLayout3 = itemView.findViewById(R.id.linearLayout3);
            constraintLayout = itemView.findViewById(R.id.imageContainer);
            imageView = itemView.findViewById(R.id.imageView);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
            unlike = itemView.findViewById(R.id.unlike);
            commentlinearlayout = itemView.findViewById(R.id.commentLayout);
            commenterProfilepic = itemView.findViewById(R.id.Commenterprofilepic);
            commenterUsername = itemView.findViewById(R.id.Commenterusername);
            circleImageViewDp = itemView.findViewById(R.id.dp);
            caption = itemView.findViewById(R.id.caption);
        }
    }
}
