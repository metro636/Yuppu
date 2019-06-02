package com.techdoom.yuppu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RoomSearch extends AppCompatActivity {
    private Button msearch,mback;
    private EditText tvName;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String Current_UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_search);
        getSupportActionBar().hide();


        tvName =(EditText)findViewById(R.id.tvromname);
        msearch=(Button)findViewById(R.id.Btnsbmt);
        mback = (Button)findViewById(R.id.btnBack);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser authdata = mAuth.getCurrentUser();
        Current_UserID = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomSearch.this,RoomSelect.class);
                startActivity(intent);
            }
        });

        msearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = tvName.getText().toString();
                mRootRef.child("chatroomlist").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(name)){

                            mRootRef.child("Users").child(Current_UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    final String username = Objects.requireNonNull(dataSnapshot.child("chatusername").getValue()).toString();

                                    mRootRef.child("ChatRoomUsers").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(name)){

                                                Toast.makeText(getApplicationContext(), "You already joined this room.", Toast.LENGTH_SHORT).show();


                                            }else{

                                                Map map2 = new HashMap<>();
                                                map2.put(name, name);

                                                mRootRef.child("ChatRoomUsers").child(username).updateChildren(map2);

                                                Map map3 = new HashMap<>();
                                                map3.put(Current_UserID, ServerValue.TIMESTAMP);
                                                mRootRef.child("ChatRoomUsersList").child(name).updateChildren(map3);
                                                mRootRef.child("ChatRoomMessages").child(name).updateChildren(map3);

                                                Map map4 = new HashMap<>();
                                                map4.put(Current_UserID,"no");
                                                mRootRef.child("ChatKick").child(name).updateChildren(map4);


                                                Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
                                                intent.putExtra("way","new");

                                                intent.putExtra("roomname", name);
                                                intent.putExtra("username", username);
                                                startActivity(intent);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });




                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                        }else{
                            Toast.makeText(getApplicationContext(), "Room name not found.Are you sure you typed room name correctly?", Toast.LENGTH_SHORT).show();
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
