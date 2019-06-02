package com.techdoom.yuppu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat extends AppCompatActivity {

    private String s1;
    private DatabaseReference mRootRef0;
    private DatabaseReference mRootRef1;
    private DatabaseReference mRootRef2, mDatabase, mRoot, mRootRef;
    private FirebaseAuth mAuth;
    private String Current_UserID;
    private String group_id;
    private ImageButton mBackBtn, mStngBtn;

    private TextView mTitleView;
    private CircleImageView mProfileImage;
    private String chat_user;
    private String profimg, currentgroup, user_id;
    private ImageButton mAddBtn, mSendBtn;
    private EditText mChatMessageVie;
    private RecyclerView mMessageList;
    private SwipeRefreshLayout mRefresh;
    private final List<GroupMessages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private GroupMessageAdapter mAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private static final int GALLERY_PICK = 1;


    private StorageReference mImageStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        // LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //  View actionbarview = inflater.inflate(R.layout.chat_layout,null);

        getSupportActionBar().hide();


        mAuth = FirebaseAuth.getInstance();
        Current_UserID = mAuth.getCurrentUser().getUid();
        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

        mTitleView = (TextView) findViewById(R.id.chat_dsp_name);
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);
        mStngBtn = (ImageButton) findViewById(R.id.grpstng);
        mAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageVie = (EditText) findViewById(R.id.chat_message_view);

        mAdapter = new GroupMessageAdapter(messagesList);
        mMessageList = (RecyclerView) findViewById(R.id.messages_list);

        mLinearLayout = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mMessageList.setAdapter(mAdapter);

        mImageStorage = FirebaseStorage.getInstance().getReference();


        mRootRef = FirebaseDatabase.getInstance().getReference();

        final String text = getIntent().getStringExtra("name");
        // getSupportActionBar().setTitle(text);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        mTitleView.setText(text);

        user_id = getIntent().getStringExtra("userid");
        if (user_id != null) {
            mRoot = FirebaseDatabase.getInstance().getReference().child("group").child(user_id);

            mRoot.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String name = dataSnapshot.child("groupname").getValue().toString();
                    getSupportActionBar().setTitle(name);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (user_id == null) {


            mRootRef0 = FirebaseDatabase.getInstance().getReference().child("GroupChats").push();
            group_id = mRootRef0.getKey();


            Map mapUsers = new HashMap<>();
            mapUsers.put(Current_UserID, Current_UserID);

            mRootRef0.updateChildren(mapUsers);


            mRootRef1 = FirebaseDatabase.getInstance().getReference().child("Groupusers").child(Current_UserID);

            Map map1 = new HashMap<>();
            map1.put(group_id, group_id);

            mRootRef1.updateChildren(map1);
            mRootRef2 = FirebaseDatabase.getInstance().getReference().child("group").child(group_id);

            Map map2 = new HashMap<>();
            map2.put("groupname", text);

            mRootRef2.updateChildren(map2);

        }

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChat.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });
        mStngBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (group_id == null) {
                    currentgroup = user_id;
                }

                if (user_id == null) {
                    currentgroup = group_id;

                }


                Intent intent = new Intent(GroupChat.this, GroupSettings.class);
                intent.putExtra("group", currentgroup);
                intent.putExtra("name", text);

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


        loadMessages();


    }

    private void loadMessages() {

        if (group_id == null) {
            currentgroup = user_id;
        }

        if (user_id == null) {
            currentgroup = group_id;

        }

        DatabaseReference messageref = mRootRef.child("groupmessages").child(currentgroup);

        Query messageQuery = messageref.limitToLast(TOTAL_ITEMS_TO_LOAD);

        messageref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GroupMessages message = dataSnapshot.getValue(GroupMessages.class);

               /* itemPos++;
                if (itemPos==1){
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;

                }*/

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessageList.scrollToPosition(messagesList.size() - 1);

                //  mRefresh.setRefreshing(false);

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();



            final DatabaseReference user_message_push = mRootRef.child("groupmessages").child(currentgroup).push();

            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        String download_url = task.getResult().getDownloadUrl().toString();


                        Map messageMap1 = new HashMap();
                        messageMap1.put("messages", download_url);

                        messageMap1.put("type", "image");
                        messageMap1.put("time", ServerValue.TIMESTAMP);
                        messageMap1.put("from", Current_UserID);

                        user_message_push.updateChildren(messageMap1, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });

                        DatabaseReference messsageconv = mRootRef.child("grpmsg").child(currentgroup);
                        Map messageMap2 = new HashMap();
                        messageMap2.put("messages", "Send an image");

                        messageMap2.put("type", "image");
                        messageMap2.put("time", ServerValue.TIMESTAMP);
                        messageMap2.put("from", Current_UserID);

                        messsageconv.updateChildren(messageMap2);



                    }

                }
            });

        }

    }


    private void sendMessage() {
        String message = mChatMessageVie.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            if (group_id == null) {
                currentgroup = user_id;
            }

            if (user_id == null) {
                currentgroup = group_id;

            }

            // String current_user_ref ="messages/"+Current_UserID+"/"+mChatUser;
            //  String chat_user_ref = "messages/"+mChatUser+"/"+Current_UserID;

            DatabaseReference user_message_push = mRootRef.child("groupmessages").child(currentgroup).push();
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("messages", message);

            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", Current_UserID);

            //Map messageUserMap = new HashMap();
            //messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            // messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);


            user_message_push.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });

            DatabaseReference messsageconv = mRootRef.child("grpmsg").child(currentgroup);
            Map messageMap1 = new HashMap();
            messageMap1.put("messages", message);

            messageMap1.put("type", "text");
            messageMap1.put("time", ServerValue.TIMESTAMP);
            messageMap1.put("from", Current_UserID);

            messsageconv.updateChildren(messageMap1);

            mChatMessageVie.setText("");


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.leave, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {

            if (group_id == null) {
                currentgroup = user_id;
            }

            if (user_id == null) {
                currentgroup = group_id;

            }


            Intent intent = new Intent(GroupChat.this, GroupSettings.class);
            intent.putExtra("group", currentgroup);

            startActivity(intent);
            // return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GroupChat.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}
