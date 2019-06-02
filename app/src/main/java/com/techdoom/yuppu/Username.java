package com.techdoom.yuppu;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Username extends AppCompatActivity {

    private Button RegName,AnonyName,btnSubmit;
    private EditText tvRegName,tvAnonyName;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String Current_UserID,kept,username,roomname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser authdata = mAuth.getCurrentUser();
        Current_UserID = mAuth.getCurrentUser().getUid();
        getSupportActionBar().hide();



        //  RegName = (Button)findViewById(R.id.btnNewUserName);
        btnSubmit= (Button)findViewById(R.id.btnSubmitName);
        tvRegName =(EditText)findViewById(R.id.editTextName);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // tvAnonyName =(EditText)findViewById(R.id.editTextanonymous);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("Users").child(Current_UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("chatusername")){

                    username = dataSnapshot.child("chatusername").getValue().toString();
                    btnSubmit.setText("Go with registered user name");
                    tvRegName.setVisibility(View.INVISIBLE);

                    mRootRef.child("ChatRoomUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(username)){

                                mRootRef.child("ChatRoomUsers").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {

                                            String roomn = dataSnapshot.getValue().toString();
                                            String remainder = roomn.substring(roomn.indexOf("=") + 1, roomn.length() - 1);

                                            if (remainder.contains(",")) {
                                                kept = remainder.substring(0, remainder.indexOf(","));

                                                roomname = kept;
                                            } else {
                                                roomname = remainder;
                                            }

                                        }
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



                }else {
                    btnSubmit.setText("Create");
                    btnSubmit.setVisibility(View.VISIBLE);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String label =   btnSubmit.getText().toString();
                if (label.equals("Go with registered user name")) {



                                mRootRef.child("ChatRoomUsers").child(username).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {
                                            Intent intent = new Intent(Username.this, ChatRoom.class);
                                            intent.putExtra("way","old");

                                            intent.putExtra("roomname", roomname);
                                            intent.putExtra("username", username);
                                            tvRegName.setHint(username);
                                            startActivity(intent);
                                         }else {
                                        Intent intent = new Intent(Username.this, RoomSelect.class);
                                        intent.putExtra("root", "chat");
                                        startActivity(intent);
                                        finish();
                                    }


                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }


                                });







                }else if (label.equals("Create")) {

                    final String username = tvRegName.getText().toString();

                    mRootRef.child("ChatRoomUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(username)) {
                                Toast.makeText(getApplicationContext(), "User name already used! Try another one", Toast.LENGTH_SHORT).show();

                            } else {
                                Map map1 = new HashMap<>();
                                map1.put("chatusername", username);
                                mRootRef.child("Users").child(Current_UserID).updateChildren(map1);
                                Map map = new HashMap<>();
                                map.put(username, ServerValue.TIMESTAMP);
                                mRootRef.child("ChatRoomUsers").updateChildren(map);

                                Map map2 = new HashMap<>();
                                map2.put(username, Current_UserID);
                                mRootRef.child("RoomUsers").updateChildren(map2);

                                CharSequence options[] = new CharSequence[]{"To send private message type username:message(Eg Albert:Hello)Username is case sensitive,if you dont type username correctly message wont be send. " , "Ok"};
                                final AlertDialog.Builder builder = new AlertDialog.Builder(Username.this);
                                builder.setTitle("Info");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, final int i) {

                                        if (i == 0) {


                                        }

                                        if (i == 1) {
                                            Intent intent = new Intent(Username.this, RoomSelect.class);
                                            intent.putExtra("root","new");
                                            startActivity(intent);
                                            finish();




                                            dialog.cancel();

                                        }

                                    }
                                });

                                builder.setCancelable(false);
                                if (!Username.this.isFinishing()){
                                    builder.show();


                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }
}
