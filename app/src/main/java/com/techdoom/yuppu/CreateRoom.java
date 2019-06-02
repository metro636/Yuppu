package com.techdoom.yuppu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateRoom extends AppCompatActivity {

    private Button btnCreate,mback;
    private EditText tvRoomName,tvRoomDes;
    private Switch imageSwitch;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String Current_UserID;
    private String image;
    private String user;

    Boolean switchState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser authdata = mAuth.getCurrentUser();
        Current_UserID = mAuth.getCurrentUser().getUid();
        getSupportActionBar().hide();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mRootRef.child("Users").child(Current_UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                 user = Objects.requireNonNull(dataSnapshot.child("chatusername").getValue()).toString();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        btnCreate=(Button)findViewById(R.id.btnpubcreate);
        mback=(Button)findViewById(R.id.btngoback);

        tvRoomName=(EditText)findViewById(R.id.tvpubroomname);
        tvRoomDes=(EditText)findViewById(R.id.tvRoomdes);
        imageSwitch=(Switch)findViewById(R.id.switchimage);


        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateRoom.this,RoomSelect.class);
                startActivity(intent);
            }
        });



        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              final   String roomname= tvRoomName.getText().toString();
                final String roomdes = tvRoomDes.getText().toString();

                if (TextUtils.isEmpty(roomname)) {
                    Toast.makeText(getApplicationContext(), "Enter room name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(roomdes)) {
                    Toast.makeText(getApplicationContext(), "Enter room description!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mRootRef.child("chatroomlist").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(roomname)){
                            Toast.makeText(getApplicationContext(), "Room name already used! Try another one", Toast.LENGTH_SHORT).show();

                        }else{

                            if (imageSwitch.isChecked()){
                                image = "yes";
                            }else{
                                image = "no";
                            }

                            //  DatabaseReference rootkey = mRootRef.child("chatroomlist").push();
                            //  String roomid = rootkey.getKey();

                            Map map= new HashMap<>();
                            map.put("roomname",roomname);
                            map.put("roomdes",roomdes);
                            map.put("owner",user);
                            map.put("created",ServerValue.TIMESTAMP);
                            map.put("image",image);
                            map.put("hide","no");


                            mRootRef.child("chatroomlist").child(roomname).updateChildren(map);

                            Map map2 = new HashMap<>();
                            map2.put(roomname,roomname);

                            mRootRef.child("ChatRoomUsers").child(user).updateChildren(map2);

                            Map map3 = new HashMap<>();
                            map3.put(Current_UserID,ServerValue.TIMESTAMP);
                            mRootRef.child("ChatRoomUsersList").child(roomname).updateChildren(map3);
                            mRootRef.child("ChatRoomMessages").child(roomname).updateChildren(map3);

                            Map map4 = new HashMap<>();
                            map4.put(Current_UserID,"no");
                            mRootRef.child("ChatKick").child(roomname).updateChildren(map4);






                            mRootRef.child("Users").child(Current_UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String users = dataSnapshot.child("chatusername").getValue().toString();
                            Intent intent = new Intent(CreateRoom.this,ChatRoom.class);
                            intent.putExtra("way","new");
                            intent.putExtra("roomname", roomname);
                            intent.putExtra("username", users);
                            startActivity(intent);

                            finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

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
