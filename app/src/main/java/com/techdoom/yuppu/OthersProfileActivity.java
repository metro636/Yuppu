package com.techdoom.yuppu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OthersProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfile;
    private Button mProfileSendReqBtn, mDeclineBtn,mAccept;
    private ImageView mBgImage;
    private ImageButton mBackbtn;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        getSupportActionBar().hide();


        final String user_id = getIntent().getStringExtra("id");
        final String group = getIntent().getStringExtra("act");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        mProfileName = (TextView) findViewById(R.id.othr_prf_display_name);
        mProfile = (TextView) findViewById(R.id.chat_dsp_name);
        mProfileStatus = (TextView) findViewById(R.id.othr_prof_status);
        mProfileImage = (CircleImageView) findViewById(R.id.othr_prf_user_img);
        mBgImage = (ImageView) findViewById(R.id.othr_prf_bg_img);

        mProfileSendReqBtn = (Button) findViewById(R.id.snd_rqst_Btn);
        mDeclineBtn = (Button) findViewById(R.id.dcln_Btn);
        mAccept =(Button)findViewById(R.id.btnaccept);
        mBackbtn = (ImageButton) findViewById(R.id.back_btn);


        mCurrent_state = "not_friends";

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mAccept.setVisibility(View.INVISIBLE);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String bgimage = dataSnapshot.child("bgimage").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                mProfile.setText(display_name);

                Picasso.get().load(image).placeholder(R.drawable.ic_person_black_24dp).into(mProfileImage);
                Picasso.get().load(bgimage).placeholder(R.color.colorAccent).into(mBgImage);


                if (mCurrent_user.getUid().equals(user_id)) {

                    mDeclineBtn.setVisibility(View.INVISIBLE);

                    mProfileSendReqBtn.setVisibility(View.INVISIBLE);
                    mAccept.setVisibility(View.INVISIBLE);

                }


                //--------------- FRIENDS LIST / REQUEST FEATURE -----

                mFriendReqDatabase.child(mCurrent_user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setVisibility(View.INVISIBLE);

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mAccept.setVisibility(View.VISIBLE);


                            } else if (req_type.equals("sent")) {

                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mAccept.setVisibility(View.INVISIBLE);

                            }

                            mProgressDialog.dismiss();


                        } else {


                            mFriendDatabase.child(mCurrent_user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setVisibility(View.INVISIBLE);

                                        mDeclineBtn.setText("Message");

                                        mDeclineBtn.setVisibility(View.VISIBLE);

                                        mAccept.setText("Unfriend");
                                        mAccept.setVisibility(View.VISIBLE);

                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

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

        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (group.equals("frnds")) {
                    Intent intent = new Intent(OthersProfileActivity.this, FriendsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }

                if (group.equals("users")) {
                    Intent intent = new Intent(OthersProfileActivity.this, UsersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                }

                if (group.equals("main")) {
                    Intent intent = new Intent(OthersProfileActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }

                if (group.equals("setting")) {
                    Intent intent = new Intent(OthersProfileActivity.this, SearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }
            }
        });


        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // --------------- NOT FRIENDS STATE ------------

                if (mCurrent_state.equals("not_friends")) {

                    String token_id = FirebaseInstanceId.getInstance().getToken();


                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrent_user.getUid());
                    notificationData.put("type", "request");
                    notificationData.put("token_id", token_id);

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Toast.makeText(OthersProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {

                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel");

                            }



                        }
                    });

                    DatabaseReference notify = mRootRef.child("Notify").child(user_id);
                    Map notifymap = new HashMap<>();
                    notifymap.put(mCurrent_user.getUid() + "/" + "type", "request");
                    notifymap.put(mCurrent_user.getUid() + "/" + "from", mCurrent_user.getUid());
                    notifymap.put(mCurrent_user.getUid() + "/" + "time", ServerValue.TIMESTAMP);

                    notify.updateChildren(notifymap);

                    DatabaseReference mNotify = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    mNotify.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String status = dataSnapshot.child("notifications").getValue().toString();
                            String token_id = FirebaseInstanceId.getInstance().getToken();

                            if (status.equals("on")) {

                                DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                                String newNotificationId = newNotificationref.getKey();
                                HashMap<String, String> notificationData1 = new HashMap<>();
                                notificationData1.put("from", mCurrent_user.getUid());
                                notificationData1.put("type", "New Friend Request");
                                notificationData1.put("message", " has send you a request");
                                notificationData1.put("token_id", token_id);


                                Map requestMap1 = new HashMap();
                                requestMap1.put("notifications/" + user_id + "/" + newNotificationId, notificationData1);


                                mRootRef.updateChildren(requestMap1);

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


                // - -------------- CANCEL REQUEST STATE ------------

                if (mCurrent_state.equals("req_sent")) {

                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Add");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mAccept.setVisibility(View.INVISIBLE);



                                }
                            });

                        }
                    });

                }






            }
        });

        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ------------ REQ RECEIVED STATE ----------

                if (mCurrent_state.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);


                    friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mCurrent_state = "friends";
                                mAccept.setText("Unfriend");
                                mDeclineBtn.setText("Message");
                                mProfileSendReqBtn.setVisibility(View.INVISIBLE);

                                mDeclineBtn.setVisibility(View.VISIBLE);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(OthersProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                    DatabaseReference notify = mRootRef.child("Notify").child(user_id);
                    Map notifymap1 = new HashMap<>();
                    notifymap1.put(mCurrent_user.getUid() + "/" + "type", "accept");
                    notifymap1.put(mCurrent_user.getUid() + "/" + "from", mCurrent_user.getUid());
                    notifymap1.put(mCurrent_user.getUid() + "/" + "time", ServerValue.TIMESTAMP);

                    notify.updateChildren(notifymap1);

                    DatabaseReference notify1 = mRootRef.child("Notify").child(mCurrent_user.getUid());
                    Map notifymap2 = new HashMap<>();
                    notifymap2.put(user_id + "/" + "type", null);
                    notifymap2.put(user_id + "/" + "from", null);
                    notifymap2.put(user_id + "/" + "time", null);

                    notify1.updateChildren(notifymap2);

                }





                // ------------ UNFRIENDS ---------

                if (mCurrent_state.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if (databaseError == null) {

                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("Add");
                                mProfileSendReqBtn.setVisibility(View.VISIBLE);



                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mAccept.setVisibility(View.INVISIBLE);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(OthersProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }


                        }
                    });

                    DatabaseReference Root = mRootRef.child("message");

                    Map messageMap20 = new HashMap();
                    messageMap20.put("messages", null);
                    messageMap20.put("read", null);
                    messageMap20.put("type", null);
                    messageMap20.put("time", null);
                    messageMap20.put("from", null);
                    messageMap20.put("under", null);

                    Map mapun = new HashMap<>();
                    mapun.put(mCurrent_user.getUid() + "/" + user_id, messageMap20);
                    mapun.put(user_id + "/" + mCurrent_user.getUid(), messageMap20);
                    Root.updateChildren(mapun, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });

                }


            }
        });

        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrent_state.equals("friends")){
                    Intent intent = new Intent(OthersProfileActivity.this,ChatActivity.class);
                    intent.putExtra("id",user_id);
                    startActivity(intent);
                }


                if (mCurrent_state.equals("req_received")){

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", null);
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", null);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {


                            } else {

                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("Add");
                                mProfileSendReqBtn.setVisibility(View.VISIBLE);
                                mAccept.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setVisibility(View.INVISIBLE

                                );

                            }



                        }
                    });


                }

            }
        });


    }


}