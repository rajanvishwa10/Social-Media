package com.android.socialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    String username;
    //    final Fragment fragment1 = new HomePageFragment();
//    final Fragment fragment2 = new SearchFragment();
//    final Fragment fragment3 = new ImageFragment();
//    final Fragment fragment4 = new ProfileFragment();
    Fragment fragment = null;

    //final FragmentManager fm = getSupportFragmentManager();

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shortcut();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    FirebaseMessaging.getInstance().subscribeToTopic(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ChipNavigationBar bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setItemSelected(R.id.home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomePageFragment()).commit();
        bottomNavigationView.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int item) {
                switch (item) {

                    case R.id.home:
                        //fm.beginTransaction().hide(active).show(fragment1).commit();
                        //active = fragment1;
                        fragment = new HomePageFragment();
                        break;

                    case R.id.search:
//                        fm.beginTransaction().hide(active).show(fragment2).commit();
//                        active = fragment2;
                        fragment = new SearchFragment();
                        break;

                    case R.id.add:
//                        fm.beginTransaction().hide(active).show(fragment3).commit();
//                        active = fragment3;
                        fragment = new ImageFragment();
                        break;

                    case R.id.notification:
                        fragment = new NotificationFragment();
                        break;
                    case R.id.Profile:
//                        fm.beginTransaction().hide(active).show(fragment4).commit();
//                        active = fragment4;
                        fragment = new ProfileFragment();
                        break;

                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
                }
            }
        });

//        fm.beginTransaction().add(R.id.nav_host_fragment, fragment4, "4").hide(fragment4).commit();
//        fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3).commit();
//        fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(fragment2).commit();
//        fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void shortcut() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").getValue(String.class);
                    ShortcutManager manager = getSystemService(ShortcutManager.class);
                    Intent messageIntent = new Intent(getApplicationContext(), MessageActivity.class);
                    messageIntent.setAction(Intent.ACTION_VIEW);
                    ShortcutInfo info = new ShortcutInfo.Builder(getApplicationContext(), "Messages").
                            setShortLabel("Message").
                            setLongLabel("Message").
                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_message_24)).
                            setIntent(messageIntent).
                            build();

                    Intent followersIntent = new Intent(getApplicationContext(), ListActivity.class);
                    followersIntent.setAction(Intent.ACTION_VIEW);
                    followersIntent.putExtra("string", "Followers");
                    followersIntent.putExtra("username", username);
                    ShortcutInfo info2 = new ShortcutInfo.Builder(getApplicationContext(), "Followers").
                            setShortLabel("Followers").
                            setLongLabel("Followers").
                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_person_24)).
                            setIntent(followersIntent).
                            build();

                    Intent followingIntent = new Intent(getApplicationContext(), ListActivity.class);
                    followingIntent.setAction(Intent.ACTION_VIEW);
                    followingIntent.putExtra("string", "Following");
                    followingIntent.putExtra("username", username);
                    ShortcutInfo info3 = new ShortcutInfo.Builder(getApplicationContext(), "Following").
                            setShortLabel("Following").
                            setLongLabel("Following").
                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_person_24)).
                            setIntent(followingIntent).
                            build();

                    Intent newMessageIntent = new Intent(getApplicationContext(), NewmessageActivity.class);
                    newMessageIntent.setAction(Intent.ACTION_VIEW);
                    ShortcutInfo info4 = new ShortcutInfo.Builder(getApplicationContext(), "NewMessage").
                            setShortLabel("New Message").
                            setLongLabel("New Message").
                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_message_24)).
                            setIntent(newMessageIntent).
                            build();

                    manager.setDynamicShortcuts(Arrays.asList(info, info2, info3, info4));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}