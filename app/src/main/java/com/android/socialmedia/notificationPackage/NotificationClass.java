package com.android.socialmedia.notificationPackage;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationClass {

    String url = "https://fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;

    public void setNotification(String username, String currentUsername, String Message, String image, String caption, String date, Context context) {
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
        updates.put("type", "like");
        ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendNotification(Message, username, currentUsername, context, "like", image);
            }
        });
    }

    private void sendNotification(String message, String receiver, String sender, Context context, String type, String image) {
        requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/topics/" + receiver);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("title", sender);
            jsonObject1.put("body", message);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("type", type);
            jsonObject2.put("image", image);

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

    public void setNotification(String username, String currentUsername, String Message, Context context) {
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
        updates.put("type", "follow");
        ref.updateChildren(updates).addOnSuccessListener(aVoid -> {
            sendNotification(Message, username, currentUsername, context, "follow", "");
        });
    }
}
