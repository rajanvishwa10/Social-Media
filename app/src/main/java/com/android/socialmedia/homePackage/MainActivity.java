package com.android.socialmedia.homePackage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import com.android.socialmedia.R;
import com.android.socialmedia.chatsPackage.MessageActivity;
import com.android.socialmedia.notificationPackage.NotificationFragment;
import com.android.socialmedia.profilePackage.ProfileFragment;
import com.android.socialmedia.searchPackage.SearchFragment;
import com.android.socialmedia.uploadImagePackage.ImageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    String username;
    //    final Fragment fragment1 = new HomePageFragment();
//    final Fragment fragment2 = new SearchFragment();
//    final Fragment fragment3 = new ImageFragment();
//    final Fragment fragment4 = new ProfileFragment();
    Fragment fragment = null;
    float x1, x2, y1, y2;
    //final FragmentManager fm = getSupportFragmentManager();

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shortcut();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        Uri uri = getIntent().getData();
        if (uri != null) {
            String intentData = uri.getQueryParameters("data").toString();
            String data = intentData.substring(1, intentData.length() - 1);
            //Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
            switch (data) {
                case "profile":
                    bottomNavigationView.setItemSelected(R.id.Profile, true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ProfileFragment()).commit();
                    break;
                case "camera":
                    bottomNavigationView.setItemSelected(R.id.add, true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ImageFragment()).commit();
                    break;
                case "noti":
                    bottomNavigationView.setItemSelected(R.id.notification, true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new NotificationFragment()).commit();
                    break;
            }
        }

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
                            setDisabledMessage("Disabled").
                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_message_24)).
                            setIntent(messageIntent).
                            build();

//                    Intent followersIntent = new Intent(getApplicationContext(), ListActivity.class);
//                    followersIntent.setAction(Intent.ACTION_VIEW);
//                    followersIntent.putExtra("string", "Followers");
//                    followersIntent.putExtra("username", username);
//                    ShortcutInfo info2 = new ShortcutInfo.Builder(getApplicationContext(), "Followers").
//                            setShortLabel("Followers").
//                            setLongLabel("Followers").
//                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_person_24)).
//                            setIntent(followersIntent).
//                            build();
//
//                    Intent followingIntent = new Intent(getApplicationContext(), ListActivity.class);
//                    followingIntent.setAction(Intent.ACTION_VIEW);
//                    followingIntent.putExtra("string", "Following");
//                    followingIntent.putExtra("username", username);
//                    ShortcutInfo info3 = new ShortcutInfo.Builder(getApplicationContext(), "Following").
//                            setShortLabel("Following").
//                            setLongLabel("Following").
//                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_person_24)).
//                            setIntent(followingIntent).
//                            build();

//                    Intent newMessageIntent = new Intent(getApplicationContext(), NewmessageActivity.class);
//                    newMessageIntent.setAction(Intent.ACTION_VIEW);
//                    ShortcutInfo info3 = new ShortcutInfo.Builder(getApplicationContext(), "NewMessage").
//                            setShortLabel("New Message").
//                            setLongLabel("New Message").
//                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_message_24)).
//                            setIntent(newMessageIntent).
//                            build();

                    ShortcutInfo info2 = new ShortcutInfo.Builder(getApplicationContext(), "camera").
                            setShortLabel("Upload").
                            setLongLabel("Upload").
                            setDisabledMessage("Disabled").
                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_camera_alt_24)).
                            setIntent(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.rajanvishwakarma.com/profile?data=camera"))).
                            build();

                    ShortcutInfo info3 = new ShortcutInfo.Builder(getApplicationContext(), "notification").
                            setShortLabel("Notification").
                            setLongLabel("Notification").
                            setDisabledMessage("Disabled").
                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_notifications_24)).
                            setIntent(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.rajanvishwakarma.com/profile?data=noti"))).
                            build();

                    ShortcutInfo info4 = new ShortcutInfo.Builder(getApplicationContext(), "Profile").
                            setShortLabel("Profile").
                            setLongLabel("Profile").
                            setDisabledMessage("Disabled").
                            setIntent(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.rajanvishwakarma.com/profile?data=profile"))).
                            setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_baseline_person_24)).
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