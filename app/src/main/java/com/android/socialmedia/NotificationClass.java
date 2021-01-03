package com.android.socialmedia;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationClass {

    public void setNotification(String username, String currentUsername, String Message, String image, String caption, String date) {
        final Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        final String formattedDate = df.format(c);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notification")
                .child(username)
                .child(formattedDate);

        Map<String, Object> updates = new HashMap<>();

        updates.put("Message", Message);
        updates.put("senderUsername", currentUsername);
        updates.put("date", formattedDate);
        updates.put("username", username);
        updates.put("imageUrl", image);
        updates.put("caption", caption);
        updates.put("imageDate", date);
        ref.updateChildren(updates);
    }
}
