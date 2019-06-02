package com.techdoom.yuppu;


import android.content.Context;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private DatabaseReference mRootRef, mUserRef;

    private TextView mTitleView, lastseen;
    private CircleImageView mProfileImage, onlinegreen, onlineorange, onlinered;

    private String chat_user;
    private String profimg;
    private FirebaseAuth mAuth;
    private String Current_UserID;
    private ImageButton mAddBtn, mSendBtn;
    private EditText mChatMessageVie;
    private RecyclerView mMessageList;
    private SwipeRefreshLayout mRefresh;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private static final int GALLERY_PICK = 1;
    private ImageButton backbtn;


    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        isOnline();


        mAuth = FirebaseAuth.getInstance();
        Current_UserID = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Current_UserID);


        mUserRef.child("onlinechat").setValue("true");

        mChatUser = getIntent().getStringExtra("id");

        getSupportActionBar().hide();


        // ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setDisplayShowCustomEnabled(true);


        // LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View actionbarview = inflater.inflate(R.layout.chat_layout,null);
        //actionBar.setCustomView(actionbarview);
        mTitleView = (TextView) findViewById(R.id.chat_dsp_name);
        mProfileImage = (CircleImageView) findViewById(R.id.user_single_imgChat);
        backbtn = (ImageButton) findViewById(R.id.back_btn);
        onlinegreen = (CircleImageView) findViewById(R.id.id_online);
        onlineorange = (CircleImageView) findViewById(R.id.id_away);
        onlinered = (CircleImageView) findViewById(R.id.id_offline);


        mTitleView = (TextView) findViewById(R.id.chat_dsp_name);
        lastseen = (TextView) findViewById(R.id.lstseen);
        mProfileImage = (CircleImageView) findViewById(R.id.user_single_imgChat);
        mAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageVie = (EditText) findViewById(R.id.chat_message_view);
        mMessageList = (RecyclerView) findViewById(R.id.messages_list);


        mAdapter = new MessageAdapter(messagesList);

        mLinearLayout = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mMessageList.setAdapter(mAdapter);

        mImageStorage = FirebaseStorage.getInstance().getReference();


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef.child("onlinechat").setValue("true");


        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Connecting...", Snackbar.LENGTH_LONG).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chat_user = dataSnapshot.child("name").getValue().toString();
                profimg = dataSnapshot.child("image").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();
                String online1 = dataSnapshot.child("onlinechat").getValue().toString();
                String lstseen = dataSnapshot.child("lastseen").getValue().toString();
                String lst = dataSnapshot.child("timee").getValue().toString();


                mTitleView.setText(chat_user);
                Picasso.get().load(profimg).placeholder(R.drawable.ic_person_black_24dp).into(mProfileImage);

                if (lstseen.equals("on")) {
                    onlinered.setVisibility(View.INVISIBLE);

                    if (online1.equals("true")) {
                        onlinegreen.setVisibility(View.VISIBLE);
                    } else if (online1.equals("false")) {
                        onlinegreen.setVisibility(View.INVISIBLE);

                    }
                    if (online.equals("true")) {
                        onlineorange.setVisibility(View.VISIBLE);
                    } else if (online.equals("false")) {
                        onlineorange.setVisibility(View.INVISIBLE);

                    }
                    if (online.equals("false") && online1.equals("false")) {
                        onlinered.setVisibility(View.VISIBLE);

                        GetTimeAgo getTimeAgo = new GetTimeAgo();

                        long lastTime = Long.parseLong(lst);

                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                        lastseen.setText("Last Seen: " + lastSeenTime);


                    }

                } else {
                    onlinered.setVisibility(View.INVISIBLE);
                    onlinegreen.setVisibility(View.INVISIBLE);
                    onlineorange.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(Current_UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + Current_UserID + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + Current_UserID, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadMessages();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });



       /* mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos=0;

                loadMoreMessages();

            }
        });*/



    }

    private void loadMoreMessages() {

        DatabaseReference messageref = mRootRef.child("messages").child(Current_UserID).child(mChatUser);

        Query messageQuery = messageref.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)) {

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if (itemPos == 1) {

                    mLastKey = messageKey;

                }
                mAdapter.notifyDataSetChanged();

                mRefresh.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {

        DatabaseReference messageref = mRootRef.child("messages").child(Current_UserID).child(mChatUser);

        Query messageQuery = messageref.limitToLast(8);

        messageref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

               /* itemPos++;
                if (itemPos==1){
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;

                }*/

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessageList.scrollToPosition(messagesList.size() - 1);

               // mRefresh.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {

        final String message = mChatMessageVie.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + Current_UserID + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + Current_UserID;

            DatabaseReference user_message_push = mRootRef.child("messages").child(Current_UserID).child(mChatUser).push();
            String push_id = user_message_push.getKey();

            DatabaseReference Root = mRootRef.child("message");

            Map messageMap = new HashMap();
            messageMap.put("messages", message);

            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", Current_UserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
            mChatMessageVie.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });

            Map messageMap1 = new HashMap();
            messageMap1.put("messages", message);
            messageMap1.put("read", "no");
            messageMap1.put("type", "text");
            messageMap1.put("time", ServerValue.TIMESTAMP);
            messageMap1.put("from", Current_UserID);
            messageMap1.put("under", Current_UserID);

            Map messageMap2 = new HashMap();
            messageMap2.put("messages", message);
            messageMap2.put("read", "yes");
            messageMap2.put("type", "text");
            messageMap2.put("time", ServerValue.TIMESTAMP);
            messageMap2.put("from", Current_UserID);
            messageMap2.put("under", mChatUser);


            Map map = new HashMap<>();
            map.put(Current_UserID + "/" + mChatUser, messageMap2);
            map.put(mChatUser + "/" + Current_UserID, messageMap1);

            Root.updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });

            DatabaseReference mNotify = FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser);
            mNotify.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String status = dataSnapshot.child("notifications").getValue().toString();
                    String state = dataSnapshot.child("online").getValue().toString();
                    String state1 = dataSnapshot.child("onlinechat").getValue().toString();
                    String token_id = FirebaseInstanceId.getInstance().getToken();

                    if (status.equals("on")) {

                        if (state.equals("false") && state1.equals("false")) {

                            DatabaseReference newNotificationref = mRootRef.child("notifications").child(mChatUser).push();
                            String newNotificationId = newNotificationref.getKey();
                            HashMap<String, String> notificationData1 = new HashMap<>();
                            notificationData1.put("from", Current_UserID);
                            notificationData1.put("type", "New Message");
                            notificationData1.put("message", message);
                            notificationData1.put("token_id", token_id);


                            Map requestMap1 = new HashMap();
                            requestMap1.put("notifications/" + mChatUser + "/" + newNotificationId, notificationData1);


                            mRootRef.updateChildren(requestMap1);
                        }



                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + Current_UserID + "/" + mChatUser;
            final String chat_user_ref = "messages/" + mChatUser + "/" + Current_UserID;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(Current_UserID).child(mChatUser).push();

            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        String download_url = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("messages", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", Current_UserID);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        mChatMessageVie.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null) {

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });

                        DatabaseReference Root = mRootRef.child("message");


                        Map messageMap3 = new HashMap();
                        messageMap3.put("messages", "You send an image");
                        messageMap3.put("read", "no");
                        messageMap3.put("type", "text");
                        messageMap3.put("time", ServerValue.TIMESTAMP);
                        messageMap3.put("from", Current_UserID);
                        messageMap3.put("under", Current_UserID);

                        Map messageMap4 = new HashMap();
                        messageMap4.put("messages", "Send you an image");
                        messageMap4.put("read", "yes");
                        messageMap4.put("type", "text");
                        messageMap4.put("time", ServerValue.TIMESTAMP);
                        messageMap4.put("from", Current_UserID);
                        messageMap4.put("under", mChatUser);


                        Map map = new HashMap<>();
                        map.put(Current_UserID + "/" + mChatUser, messageMap4);
                        map.put(mChatUser + "/" + Current_UserID, messageMap3);

                        Root.updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });


                    }

                }
            });

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserRef.child("onlinechat").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserRef.child("timee").setValue(ServerValue.TIMESTAMP);
        mUserRef.child("onlinechat").setValue("false");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mUserRef.child("timee").setValue(ServerValue.TIMESTAMP);

        mUserRef.child("onlinechat").setValue("false");

        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
