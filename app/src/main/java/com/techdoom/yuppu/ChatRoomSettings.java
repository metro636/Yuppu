package com.techdoom.yuppu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

public class ChatRoomSettings extends AppCompatActivity {

    private Switch images,hide;
    private Button mLeave,mDelete,mBack;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String Current_UserID;
    private String userrname;
    private TextView mRoomName,mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_settings);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser authdata = mAuth.getCurrentUser();
        Current_UserID = mAuth.getCurrentUser().getUid();

        getSupportActionBar().hide();



        images = (Switch) findViewById(R.id.switchimg);
        hide = (Switch) findViewById(R.id.switchhide);

        mBack = (Button)findViewById(R.id.btngoback);
        mLeave = (Button)findViewById(R.id.btnleave);
        mDelete = (Button)findViewById(R.id.btndelete);
        mRoomName =(TextView)findViewById(R.id.tvrooomname);
        mDate=(TextView)findViewById(R.id.tvdate);
        final String room = getIntent().getStringExtra("roomname");
        final String user = getIntent().getStringExtra("username");
        userrname = user;
        mRoomName.setText(room);
        mRootRef= FirebaseDatabase.getInstance().getReference();
        mRootRef.child("chatroomlist").child(room).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                String hids = dataSnapshot.child("hide").getValue().toString();
                if (image.equals("yes")){
                    images.setChecked(true);
                }else{
                    images.setChecked(false);
                }

                if (hids.equals("yes")){
                    hide.setChecked(true);
                }else {
                    hide.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        images.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRootRef.child("chatroomlist").child(room).child("image").setValue("yes");

                } else {
                    mRootRef.child("chatroomlist").child(room).child("image").setValue("no");

                }
            }
        });

        hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRootRef.child("chatroomlist").child(room).child("hide").setValue("yes");

                } else {
                    mRootRef.child("chatroomlist").child(room).child("hide").setValue("no");

                }
            }
        });
        mRootRef.child("chatroomlist").child(room).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String time = dataSnapshot.child("created").getValue().toString();

                long times = Long.parseLong(time);
                Date date = new Date(times);
                String timeshow = String.valueOf(date);
                mDate.setText(timeshow);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatRoomSettings.this,ChatRoom.class);
                intent.putExtra("roomname",room);
                intent.putExtra("username",user);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRootRef.child("ChatRoomUsers").child(user).child(room).setValue(null);
                mRootRef.child("ChatRoomUsersList").child(room).setValue(null);
                mRootRef.child("chatroomlist").child(room).setValue(null);
                mRootRef.child("ChatRoomMessages").child(room).setValue(null);

                mRootRef.child("ChatRoomUsers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user)){


                                    String roomn = dataSnapshot.child(user).getValue().toString();
                                    String remainder = roomn.substring(roomn.indexOf("=")+1, roomn.length()-1);
                                    String  roomname;

                                    if (remainder.contains(",")) {
                                        String  kept = remainder.substring(0, remainder.indexOf(","));

                                        roomname = kept;

                                        Intent intent = new Intent(ChatRoomSettings.this,ChatRoom.class);
                                        intent.putExtra("way","old");

                                        intent.putExtra("roomname",roomname);
                                        intent.putExtra("username",userrname);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        startActivity(intent);
                                    }else{
                                        roomname= remainder;
                                        Intent intent = new Intent(ChatRoomSettings.this,ChatRoom.class);
                                        intent.putExtra("way","old");
                                        intent.putExtra("roomname",roomname);
                                        intent.putExtra("username",userrname);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    }


                        }
                        else{

                            Intent intent = new Intent(ChatRoomSettings.this,RoomSelect.class);
                            intent.putExtra("root","new");

                            intent.putExtra("username",userrname);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        mLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRootRef.child("ChatRoomUsers").child(user).child(room).setValue(null);
                mRootRef.child("ChatRoomUsersList").child(room).child(Current_UserID).setValue(null);
                mRootRef.child("ChatRoomMessages").child(room).child(Current_UserID).setValue(null);

                mRootRef.child("ChatRoomUsers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userrname)){

                            mRootRef.child("ChatRoomUsers").child(userrname).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String roomn = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                    String remainder = roomn.substring(roomn.indexOf("=")+1, roomn.length()-1);
                                    String  roomname;

                                    if (remainder.contains(",")) {
                                        String  kept = remainder.substring(0, remainder.indexOf(","));

                                        roomname = kept;

                                        Intent intent = new Intent(ChatRoomSettings.this,ChatRoom.class);
                                        intent.putExtra("way","old");
                                        intent.putExtra("roomname",roomname);
                                        intent.putExtra("username",userrname);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        startActivity(intent);
                                    }else{
                                        roomname= remainder;
                                        Intent intent = new Intent(ChatRoomSettings.this,ChatRoom.class);
                                        intent.putExtra("way","old");
                                        intent.putExtra("roomname",roomname);
                                        intent.putExtra("username",userrname);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });





                        }
                        else{

                            Intent intent = new Intent(ChatRoomSettings.this,RoomSelect.class);
                            intent.putExtra("root","new");

                            intent.putExtra("username",userrname);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });



    }
}
