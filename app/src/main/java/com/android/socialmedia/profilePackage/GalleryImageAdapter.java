package com.android.socialmedia.profilePackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.socialmedia.uploadImagePackage.ImageList;
import com.android.socialmedia.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ViewHolder>{

    private final Context context;
    private final List<ImageList> imageLists;
    private final String username;

    public GalleryImageAdapter(Context context, List<ImageList> imageLists, String username){
        this.context = context;
        this.imageLists = imageLists;
        this.username = username;
    }

    @NonNull
    @Override
    public GalleryImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryImageAdapter.ViewHolder holder, int position) {
        ImageList imageList = imageLists.get(position);
        System.out.println(imageList.getImage());
        Glide.with(context).load(imageList.getImage()).placeholder(R.drawable.ic_placeholder).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UploadImageActivity.class);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) context, holder.imageView, holder.imageView.getTransitionName());
                intent.putExtra("image",imageList.getImage());
                intent.putExtra("caption",imageList.getCaption());
                intent.putExtra("date",imageList.getData());
                intent.putExtra("username",username);
                context.startActivity(intent,activityOptionsCompat.toBundle());
            }
        });
        System.out.println(imageList.getData());
       // holder.textView.setText(imageList.getData());
    }

    @Override
    public int getItemCount() {
        return imageLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
