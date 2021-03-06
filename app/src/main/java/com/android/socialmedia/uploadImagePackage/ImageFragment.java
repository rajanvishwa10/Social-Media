package com.android.socialmedia.uploadImagePackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.socialmedia.homePackage.MainActivity;
import com.android.socialmedia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ImageFragment extends Fragment {
    // Uri uri;
    Bitmap bitmap;
    ImageView imageView;
    ProgressDialog progressDialog;
    int post;
    String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        read();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);

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

        Button button = view.findViewById(R.id.postImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                EditText editText = view.findViewById(R.id.editText);
                String caption = editText.getText().toString().trim();
                if (bitmap == null) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Choose an Image", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage(bitmap, caption, username);
                }
            }
        });
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(getContext(), ImageFragment.this);
            }
//                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
//                        getContext(), R.style.BottomSheetDialogTheme
//                );
//                View bottomSheetView = LayoutInflater.from(getContext())
//                        .inflate(R.layout.layout_bottomsheet,
//                                (LinearLayout) view.findViewById(R.id.bottomsheetcontainer));
//
//                bottomSheetView.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        bottomSheetDialog.cancel();
//                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        intent.setType("image/*");
//                        startActivityForResult(intent.createChooser(intent, "Select file"), 2);
////                        Toast.makeText(MainActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                bottomSheetView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        bottomSheetDialog.cancel();
////                        Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(intent, 0);
//                    }
//                });
//                bottomSheetView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        bottomSheetDialog.cancel();
//                    }
//                });
//                bottomSheetDialog.setContentView(bottomSheetView);
//                bottomSheetDialog.show();
//            }
        });
        return view;
    }

    private void uploadImage(Bitmap bitmap, String caption, String username) {

        final Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        final String formattedDate = df.format(c);
        System.out.println(formattedDate);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        final StorageReference reference = FirebaseStorage.getInstance().getReference().
                child("Images").
                child(username).
                child(FirebaseAuth.getInstance().getUid() + formattedDate + ".jpeg");

        reference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(
                                new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri Imguri) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Images")
                                                //.child(username)
                                                .child(String.valueOf(System.currentTimeMillis()));

                                        Map<String, Object> updates = new HashMap<>();

                                        updates.put("Image", String.valueOf(Imguri));
                                        updates.put("caption", caption);
                                        updates.put("data", formattedDate);
                                        updates.put("username", username);
                                        updates.put("likes", 0);

                                        ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                Map<String, Object> updates = new HashMap<String, Object>();
                                                int postNum = post + 1;
                                                updates.put("post", postNum);

                                                databaseReference.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(getContext(), "Post Uploaded", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Not Uploaded", Toast.LENGTH_SHORT).show();
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
                        progressDialog.dismiss();
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
                    post = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("post").getValue(Integer.class);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 2 && resultCode == getActivity().RESULT_OK) {
//            uri = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                imageView.setImageBitmap(bitmap);
////                progressDialog.show();
////                uploadImage(bitmap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } else if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
//            bitmap = (Bitmap) data.getExtras().get("data");
//            System.out.println(bitmap);
//
//            imageView.setImageBitmap(bitmap);
////            progressDialog.show();
////            uploadImage(bitmap);
//
//        }
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {

            Uri uri = CropImage.getPickImageResultUri(getContext(), data);
            System.out.println(uri);
            startCrop(uri);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                Uri uri = result.getUri();
                System.out.println("uri = " + uri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    System.out.println("bit = " + bitmap);

                    //convertedImage = getResizedBitmap(bitmap, 1000);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                Bitmap bitmap = drawable.getBitmap();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                byte[] bitmapdata = bos.toByteArray();
//                textView.setText(bitmapdata.length / 1024 + " kb");


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(getActivity(), exception.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCrop(Uri uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(getActivity());
    }
}