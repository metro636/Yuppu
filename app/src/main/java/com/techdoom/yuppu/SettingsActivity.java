package com.techdoom.yuppu;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class SettingsActivity extends AppCompatActivity {
    private Switch mNotify, mOnline,mchatroom;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Button mback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNotify = (Switch) findViewById(R.id.switch1);
        mOnline = (Switch) findViewById(R.id.switch2);
        mchatroom=(Switch)findViewById(R.id.switchchatroom);
        mback = (Button)findViewById(R.id.btngoback) ;
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = mCurrentUser.getUid();
        getSupportActionBar().hide();



        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);

        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String CurrentState = dataSnapshot.child("notifications").getValue().toString();
                String CurrentStatus = dataSnapshot.child("lastseen").getValue().toString();
                String chatroomhide = dataSnapshot.child("chatroomhide").getValue().toString();
                if (CurrentState.equals("on")) {
                    mNotify.setChecked(true);

                } else {
                    mNotify.setChecked(false);

                }
                if (CurrentStatus.equals("on")) {
                    mOnline.setChecked(true);

                } else {
                    mOnline.setChecked(false);

                }

                if (chatroomhide.equals("yes")) {
                    mchatroom.setChecked(true);

                } else {
                    mchatroom.setChecked(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRootRef.child("notifications").setValue("on");

                } else {
                    mRootRef.child("notifications").setValue("off");


                }
            }
        });


        mchatroom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRootRef.child("chatroomhide").setValue("yes");

                } else {
                    mRootRef.child("chatroomhide").setValue("no");


                }
            }
        });

        mOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mRootRef.child("lastseen").setValue("on");

                } else {
                    mRootRef.child("lastseen").setValue("off");


                }

            }
        });
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(intent);
    }
}