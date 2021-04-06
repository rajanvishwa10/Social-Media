package com.android.socialmedia;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    String number;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private final Context context;
    private final List<Chats> chats;

    public MessageAdapter(Context context, List<Chats> chats) {
        this.chats = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right_message, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left_message, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Chats chat = chats.get(position);
        final String chatMessage = chat.getMessage();

        final String time = chat.getDate();
        String[] newTime = time.split("\\s");

        String type = chat.getType();
        if (type.equals("image")) {
            holder.cardView.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
            holder.imageButton.setVisibility(View.VISIBLE);
            holder.imageTime.setVisibility(View.VISIBLE);
            holder.imageTime.setText(newTime[1] + " " + newTime[2]);
            holder.isSeen.setVisibility(View.GONE);
            if (position == chats.size() - 1) {
                holder.isSeen2.setVisibility(View.VISIBLE);
                if (chat.isIsseen()) {
                    holder.isSeen2.setText("Seen");
                } else {
                    holder.isSeen2.setText("Delivered");
                }
            } else {
                holder.isSeen2.setVisibility(View.GONE);
            }
            Glide.with(context).load(chatMessage).transform(new CenterCrop(), new RoundedCorners(30))
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("url", chatMessage);
                context.startActivity(intent);
            });
            holder.imageButton.setOnClickListener(v -> {

                if(checkPermission()) {
                    try {
                        Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();
                        Uri uri = Uri.parse(chatMessage);
                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Image Downloaded");
                        request.setDescription("Image is Ready");
                        request.setAllowedOverMetered(true);
                        request.setAllowedOverRoaming(true);
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        String fileName = chat.getName();
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "/" +
                                context.getResources().getString(R.string.app_name) + "/" + fileName);
                        request.setMimeType("image/*");
                        downloadManager.enqueue(request);
                        holder.imageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_download_done_24));
                    } catch (Exception e) {
                        Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

        }else if(type.equals("file")){
            holder.cardView.setVisibility(View.GONE);
            holder.cardView2.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
            holder.imageButton.setVisibility(View.GONE);
            holder.imageTime.setVisibility(View.GONE);
            holder.isSeen3.setVisibility(View.VISIBLE);
            holder.fileTime.setText(newTime[1] + " " + newTime[2]);
            holder.isSeen.setVisibility(View.GONE);
            if (position == chats.size() - 1) {
                holder.isSeen3.setVisibility(View.VISIBLE);
                if (chat.isIsseen()) {
                    holder.isSeen3.setText("Seen");
                } else {
                    holder.isSeen3.setText("Delivered");
                }
            } else {
                holder.isSeen3.setVisibility(View.GONE);
            }
//            Glide.with(context).load(chatMessage).into(holder.imageView);
            String fileName = chat.getName();
            holder.fileName.setText(fileName);
            String[] fileType = fileName.split("\\.");
            holder.fileType.setText(fileType[1]);
            holder.imageButton2.setOnClickListener(v -> {

                if(checkPermission()) {
                    try {
                        Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();
                        Uri uri = Uri.parse(chatMessage);
                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("File Downloaded");
                        request.setDescription("File is Ready");
                        request.setAllowedOverMetered(true);
                        request.setAllowedOverRoaming(true);
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "/" +
                                context.getResources().getString(R.string.app_name) + "/" + fileName);
                        if(fileType[1].equals("apk")){
                            request.setMimeType("application/apk");
                        }else{
                            request.setMimeType("application/"+fileType[1]);
                        }
                        downloadManager.enqueue(request);

                        //holder.imageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_download_done_24));
                    } catch (Exception e) {
                        Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            holder.cardView.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.VISIBLE);

            holder.imageButton.setVisibility(View.GONE);
            holder.show_message.setText(chatMessage);

            holder.time.setText(newTime[1] + " " + newTime[2]);

            if (position == chats.size() - 1) {
                if (chat.isIsseen()) {
                    holder.isSeen.setText("Seen");
                } else {
                    holder.isSeen.setText("Delivered");
                }
            } else {
                holder.isSeen.setVisibility(View.GONE);
            }
        }

    }


    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("To download a file it is necessary to allow required permission");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity)context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView show_message, time, imageTime, isSeen,
                isSeen2, isSeen3, fileName, fileTime, fileType;
        ImageView imageView;
        ImageButton imageButton, imageButton2;
        LinearLayout linearLayout;
        CardView cardView,cardView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.chats);
            time = itemView.findViewById(R.id.time);
            imageView = itemView.findViewById(R.id.image);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            cardView = itemView.findViewById(R.id.cardView);
            cardView2 = itemView.findViewById(R.id.cardView2);
            imageTime = itemView.findViewById(R.id.imageTime);
            isSeen = itemView.findViewById(R.id.seen);
            isSeen2 = itemView.findViewById(R.id.seen2);
            isSeen3 = itemView.findViewById(R.id.seen3);
            fileName = itemView.findViewById(R.id.fileName);
            imageButton = itemView.findViewById(R.id.download);
            imageButton2 = itemView.findViewById(R.id.fileDownload);
            fileTime = itemView.findViewById(R.id.fileTime);
            fileType = itemView.findViewById(R.id.fileType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("username", Context.MODE_PRIVATE);
        number = sharedPreferences.getString("username", "");
        if (chats.get(position).getSender().equals(number)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
