package com.example.registration;

import android.content.Intent;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.registration.Fragments.ChatsFragment;
import com.example.registration.Fragments.PrivateChatsFragment;
import com.example.registration.Fragments.ProfileFragment;
import com.example.registration.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SlideMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ActionBarDrawerToggle mToggle;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    NavigationView navigationView;
    CircleImageView imageCircle;
    TextView username;
    View headerView;
    TextView email;

    DrawerLayout mDrawerLayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_menu);
        mDrawerLayout = findViewById(R.id.slide_menu);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        updateInfo();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.profile);
        }
    }

    public void updateInfo(){
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        imageCircle = headerView.findViewById(R.id.profile_photo);
        email = headerView.findViewById(R.id.textView2);
        username = headerView.findViewById(R.id.textView1);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("/users/" + firebaseUser.getUid());
        Log.d("Heh", "////" + reference.toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                Picasso.get().load(user.getProfileImageUrl()).into(imageCircle);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.profile:
                setTitle("Profile");
                ProfileFragment fragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SlideMenu.this, LoginIn.class);
                finish();
                startActivity(intent);
                break;
            case R.id.chatrooms:
                setTitle("Chats");
                ChatsFragment fragment1 = new ChatsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment1).commit();
                break;
            case R.id.p_chatrooms:
                setTitle("Private Chats");
                PrivateChatsFragment fragment2 = new PrivateChatsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment2).commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void startChatting(Bundle data) {
        Fragment chatFragment = new MainChatFragment();
        chatFragment.setArguments(data);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, chatFragment);
        transaction.show(chatFragment);
        transaction.commit();
    }
}