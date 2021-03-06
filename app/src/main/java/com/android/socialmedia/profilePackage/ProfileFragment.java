package com.android.socialmedia.profilePackage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.socialmedia.uploadImagePackage.ImageList;
import com.android.socialmedia.R;
import com.android.socialmedia.likePackage.ListActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    TextView textView, textView2, textView3, textView4, textView5, textView6, textView7;
    TextView postText, followerText, followingText;
    Uri uri;
    CircleImageView imageView;
    ImageView verifiedImage;
    Bitmap bitmap;
    List<ImageList> imageList;
    Button editProfile;
    private ProgressDialog progressDialog;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    GalleryImageAdapter galleryImageAdapter;
    String username;
    RelativeLayout followerRelative, followingRelative;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        read();
        imageList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(getContext(), 3);

        verifiedImage = view.findViewById(R.id.verified);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        editProfile = view.findViewById(R.id.editprofile);

        imageView = view.findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentedit = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intentedit);
            }
        });

        textView = view.findViewById(R.id.post);
        postText = view.findViewById(R.id.posttext);
        followerText = view.findViewById(R.id.followertext);
        followingText = view.findViewById(R.id.followingtext);
        textView2 = view.findViewById(R.id.username);
        textView3 = view.findViewById(R.id.name);
        textView4 = view.findViewById(R.id.followers);
        textView5 = view.findViewById(R.id.following);
        textView6 = view.findViewById(R.id.bio);
        textView7 = view.findViewById(R.id.website);

        followerRelative = view.findViewById(R.id.followerRelative);
        followingRelative = view.findViewById(R.id.followingRelative);
        followerRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("string", "Followers");
                startActivity(intent);
            }
        });
        followingRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("string", "Following");
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading....");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == getActivity().RESULT_OK) {
            uri = data.getData();
            Log.d("ImageUri", "" + uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                progressDialog.show();
                uploadImage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            progressDialog.show();
            uploadImage(bitmap);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage();
            } else {
                Toast.makeText(getContext(), "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void request() {
        if (ContextCompat.checkSelfPermission
                (getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            SelectImage();
        } else {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2
            );

        }
    }

    private void SelectImage() {
        final CharSequence[] items = {"Gallery", "Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select file"), 2);
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();

    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        final StorageReference reference = FirebaseStorage.getInstance().getReference().
                child("ProfileImages").
                child(FirebaseAuth.getInstance().getUid() + ".jpeg");

        reference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(
                                new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri Imguri) {

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getUid());

                                        Map<String, Object> updates = new HashMap<>();

                                        updates.put("profileImage", String.valueOf(Imguri));

                                        ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.cancel();
                                                Toast.makeText(getContext(), "Profile Photo Uploaded", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Toast.makeText(getContext(), "Not Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                        );

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void read() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("FullName").getValue(String.class);
                    username = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    long post = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("post").getValue(Integer.class);
                    long followers = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers").getValue(Integer.class);
                    long followings = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").getValue(Integer.class);
                    String url = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profileImage").getValue(String.class);
                    String bio = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Bio").getValue(String.class);
                    String website = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Website").getValue(String.class);
                    boolean verified = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("verified").getValue(Boolean.class);

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Images");
                    Query query = myRef.orderByChild("username").equalTo(username);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                imageList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    ImageList imageList1 = dataSnapshot.getValue(ImageList.class);
                                    imageList.add(imageList1);
                                    System.out.println(imageList);
                                }
                                Collections.reverse(imageList);
                                System.out.println(imageList);
//                                recyclerView.setBackground(null);
                                galleryImageAdapter = new GalleryImageAdapter(getContext(), imageList, username);
                                recyclerView.setAdapter(galleryImageAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //editProfile.setBackgroundColor(getResources().getColor(R.color.white));
                    editProfile.setBackgroundColor(Color.WHITE);
                    editProfile.setTextColor(Color.BLACK);
                    textView.setText("" + post);
                    textView.setBackground(null);
                    textView.setTextColor(Color.BLACK);
                    postText.setBackground(null);
                    postText.setTextColor(Color.BLACK);
                    followerText.setBackground(null);
                    followerText.setTextColor(Color.BLACK);
                    followingText.setBackground(null);
                    followingText.setTextColor(Color.BLACK);
                    textView2.setText(username);
                    textView2.setBackground(null);
                    textView2.setTextColor(Color.BLACK);
                    textView3.setText(name);
                    textView3.setBackground(null);
                    textView3.setTextColor(Color.BLACK);
                    textView4.setText("" + followers);
                    textView4.setBackground(null);
                    textView4.setTextColor(Color.BLACK);
                    textView5.setText("" + followings);
                    textView5.setBackground(null);
                    textView5.setTextColor(Color.BLACK);
                    if (bio == null) {
                        textView6.setVisibility(View.GONE);
                    } else {
                        bio.length();
                        textView6.setVisibility(View.VISIBLE);
                        textView6.setText(bio);
                        textView6.setBackground(null);
                        textView6.setTextColor(Color.BLACK);
                    }

                    if (website == null) {
                        textView7.setVisibility(View.GONE);
                    } else {
                        textView7.setVisibility(View.VISIBLE);
                        textView7.setText(website);
                        textView7.setBackground(null);
                        textView7.setTextColor(Color.BLACK);
                        textView7.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                CustomTabsIntent customTabsIntent = builder.build();
                                customTabsIntent.launchUrl(getContext(), Uri.parse(website));
                            }
                        });
                    }

                    if (verified) {
                        verifiedImage.setVisibility(View.VISIBLE);
                        verifiedImage.setLongClickable(true);
                        verifiedImage.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Toast.makeText(getContext(), "Verified", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
                    } else {
                        verifiedImage.setVisibility(View.GONE);
                    }

                    try {
                        if (url.isEmpty()) {
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_24));
                        } else {
                            Glide.with(getContext()).load(url).placeholder(R.drawable.ic_placeholder).into(imageView);
                        }
                        imageView.setBackground(null);
                    } catch (NullPointerException e) {
                    }


                } else {
                    Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Failed to read value
                // Toasty.error(ProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        imageList.clear();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        imageList.clear();
//        read();
//    }
}