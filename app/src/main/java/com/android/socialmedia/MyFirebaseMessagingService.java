package com.android.socialmedia;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Bitmap bitmapImage;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String type = remoteMessage.getData().get("type");
        String image = remoteMessage.getData().get("image");

        Glide.with(getApplicationContext()).asBitmap().load(image).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                System.out.println("bitmap" + resource);
                bitmapImage = resource;
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "CHAT", "MessageChannel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }


        Intent intent = null;
        try {
            switch (type) {
                case "like":
                case "follow":
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.rajanvishwakarma.com/profile?data=noti"));
                    break;
                case "message":
                    intent = new Intent(this, UserChatActivity.class);
                    intent.putExtra("username", title);
                    break;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Users").orderByChild("Username").equalTo(title);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = "";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    imageUrl = dataSnapshot.child("profileImage").getValue(String.class);
                    System.out.println(imageUrl);
                    if (imageUrl.length() > 1) {
                        break;
                    }
                }

                Picasso.get().load(imageUrl).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHAT")
                                .setContentTitle(title)
                                .setContentText(body)
                                .setSmallIcon(R.drawable.socialmedia)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.ic_launcher_background))
                                .setContentIntent(pendingIntent)
                                .setLargeIcon(bitmap)
                                .setAutoCancel(true);
                        if (type.equals("like")) {
                            builder.setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmapImage));
                        }

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
                        managerCompat.notify(12, builder.build());

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}
