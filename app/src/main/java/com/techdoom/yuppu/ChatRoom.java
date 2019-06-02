package com.techdoom.yuppu;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoom extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mUsersList, mRoomlist;
    private TextView tvroomlist,header;
    private DatabaseReference mDatabase, mUsersDatabase, mRoot;
    private FirebaseUser mCurrentUser;
    private FirebaseRecyclerAdapter<Rooms, ChatRoom.RoomsViewHolder> mRoomlistadapter;
    private FirebaseRecyclerAdapter<Users, ChatRoom.UsersViewHolder> mUsersAdapter;
    private View mMainView;

    private LinearLayoutManager mLinearLayout;
    private Button mBack,mSettings;
    private FirebaseAuth mAuth;
    private String Current_UserID,rooomname,userrname;
    private DatabaseReference mRootRef;
    private ImageButton mAddBtn, mSendBtn,addrooms;
    private EditText mChatMessageVie;
    private RecyclerView mMessageList;
    private ChatRoomMessageAdapter mAdapter;
    private final List<RoomMessages> messagesList = new ArrayList<>();
    private StorageReference mImageStorage;
    private static final int GALLERY_PICK = 1;
    private String change;
    private String remove = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser authdata = mAuth.getCurrentUser();
        Current_UserID = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageVie = (EditText) findViewById(R.id.chat_message_view);
        mSettings =(Button)findViewById(R.id.btnleave);
        change ="no";


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageButton menuLeft = (ImageButton) findViewById(R.id.menuLeft);
        ImageButton menuRight = (ImageButton) findViewById(R.id.menuRight);
        tvroomlist = (TextView)findViewById(R.id.tvroomlist);
        mBack = (Button)findViewById(R.id.btnMain);
        header =(TextView)findViewById(R.id.headernav);

        addrooms = (ImageButton) findViewById(R.id.imgBtnaddroom);
        TextView roomname = (TextView) findViewById(R.id.toolbar_title_room);
        final String room = getIntent().getStringExtra("roomname");
        final String user = getIntent().getStringExtra("username");
        remove =  getIntent().getStringExtra("way");
        roomname.setText(room);

        getIntent().removeExtra("roomname");
        header.setText(user);
        rooomname= room;
        userrname = user;

        if (remove.equals("old")) {

            mRootRef.child("chatroomlist").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(rooomname)) {

                        CharSequence options[] = new CharSequence[]{"Room doesn't exist anymore. ", "Ok"};
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
                        builder.setTitle("Warning");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int i) {

                                if (i == 0) {
                                    dialog.cancel();

                                }

                                if (i == 1) {

                                    mRootRef.child("ChatRoomUsers").child(userrname).child(rooomname).setValue(null);

                                    mRootRef.child("ChatRoomUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(userrname)) {
                                                change = "yes";
                                                mRootRef.child("ChatRoomUsers").child(userrname).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String roomn = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                                        String remainder = roomn.substring(roomn.indexOf("=") + 1, roomn.length() - 1);
                                                        String roomnames;

                                                        if (remainder.contains(",")) {
                                                            String kept = remainder.substring(0, remainder.indexOf(","));

                                                            roomnames = kept;

                                                            Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                            intent.putExtra("way","old");

                                                            intent.putExtra("roomname", roomnames);
                                                            intent.putExtra("username", userrname);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                            startActivity(intent);
                                                        } else {
                                                            roomnames = remainder;
                                                            Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                            intent.putExtra("way","old");

                                                            intent.putExtra("roomname", roomnames);
                                                            intent.putExtra("username", userrname);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });


                                            } else {

                                                Intent intent = new Intent(ChatRoom.this, RoomSelect.class);
                                                intent.putExtra("root", "new");

                                                intent.putExtra("username", userrname);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                startActivity(intent);


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    dialog.cancel();

                                }

                            }
                        });
                        builder.setCancelable(false);
                        if (!ChatRoom.this.isFinishing()) {
                            builder.show();


                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



        mRootRef.child("Users").child(Current_UserID).child("onlinechatroom").setValue("true");
        final DatabaseReference online = mRootRef.child("Users").child(Current_UserID);


        online.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    online.child("onlinechatroom").onDisconnect().setValue("false");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRoot = FirebaseDatabase.getInstance().getReference().child("ChatRoomUsers");



        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                mRootRef.child("chatroomlist").child(rooomname).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String user = dataSnapshot.child("owner").getValue().toString();
                        if (!userrname.equals(user)){

                            CharSequence options[] = new CharSequence[]{"Leave Room " , "Cancel"};
                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle("Room Settings");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {

                                    if (i == 0) {
                                        mRootRef.child("ChatRoomUsers").child(userrname).child(rooomname).setValue(null);
                                        mRootRef.child("ChatRoomUsersList").child(rooomname).child(Current_UserID).setValue(null);
                                        mRootRef.child("ChatRoomMessages").child(rooomname).child(Current_UserID).setValue(null);
                                        mRootRef.child("ChatRoomUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(userrname)){
                                                    change="yes";

                                                    mRootRef.child("ChatRoomUsers").child(userrname).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            String roomn = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                                            String remainder = roomn.substring(roomn.indexOf("=")+1, roomn.length()-1);
                                                            String  roomnames;

                                                            if (remainder.contains(",")) {
                                                                String  kept = remainder.substring(0, remainder.indexOf(","));

                                                                roomnames = kept;

                                                                Intent intent = new Intent(ChatRoom.this,ChatRoom.class);
                                                                intent.putExtra("way","old");

                                                                intent.putExtra("roomname",roomnames);
                                                                intent.putExtra("username",userrname);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                                startActivity(intent);
                                                            }else{
                                                                roomnames= remainder;
                                                                Intent intent = new Intent(ChatRoom.this,ChatRoom.class);
                                                                intent.putExtra("way","old");

                                                                intent.putExtra("roomname",roomnames);
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

                                                    Intent intent = new Intent(ChatRoom.this,RoomSelect.class);
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

                                    if (i == 1) {

                                        dialog.cancel();

                                    }

                                }
                            });
                            builder.show();



                        }else if (userrname.equals(user)){
                            Intent intent =new Intent(ChatRoom.this,ChatRoomSettings.class);
                            intent.putExtra("roomname",rooomname);
                            intent.putExtra("username",user);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        addrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatRoom.this,RoomSelect.class);
                intent.putExtra("root","main");
                intent.putExtra("roomname",room);
                intent.putExtra("username",user);
                startActivity(intent);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatRoom.this,NavActivity.class);
                startActivity(intent);
            }
        });


        menuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view2);
        navigationView1.setNavigationItemSelectedListener(this);
        navigationView2.setNavigationItemSelectedListener(this);
        mMessageList = (RecyclerView) findViewById(R.id.messages_list);

        mAdapter = new ChatRoomMessageAdapter(messagesList);
        mLinearLayout = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mMessageList.setAdapter(mAdapter);


        //image

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRootRef.child("chatroomlist").child(room).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        if (image.equals("yes")){
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                        }else {
                            Toast.makeText(ChatRoom.this, "Images not allowed in this room.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });



        //Rooms list adapter


        mDatabase = FirebaseDatabase.getInstance().getReference().child("ChatRoomUsers").child(user);
        mDatabase.keepSynced(true);
        // mBackbtn = (ImageButton) findViewById(R.id.back_btn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = mChatMessageVie.getText().toString();

                final String time = String.valueOf(System.currentTimeMillis());


                if (!TextUtils.isEmpty(message)) {

                    if (message.contains(":")){
                        final String kept = message.substring( 0, message.indexOf(":"));

                        final DatabaseReference rootcheck = FirebaseDatabase.getInstance().getReference().child("RoomUsers");
                        rootcheck.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(kept)){
                                    String userid = dataSnapshot.child(kept).getValue().toString();

                                    Map messageMap = new HashMap();
                                    messageMap.put("messages", message);
                                    messageMap.put("type", "text");
                                    messageMap.put("time",time );
                                    messageMap.put("from", Current_UserID);
                                    messageMap.put("pvt","yes");

                                    Map map = new HashMap<>();
                                    map.put(time, messageMap);

                                    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("ChatRoomMessages").child(room);
                                    root.child(Current_UserID).updateChildren(map);
                                    root.child(userid).updateChildren(map);
                                    mChatMessageVie.setText("");


                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }else {


                        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("ChatRoomMessages").child(room);

                        root.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                System.out.println(snapshot.getValue());
                                for (DataSnapshot childSnap : snapshot.getChildren()) {
                                    Map messageMap = new HashMap();
                                    messageMap.put("messages", message);
                                    messageMap.put("type", "text");
                                    messageMap.put("time",time);
                                    messageMap.put("from", Current_UserID);
                                    messageMap.put("pvt","no");

                                    Map map = new HashMap<>();
                                    map.put(time, messageMap);
                                    childSnap.getRef().updateChildren(map);
                                    mChatMessageVie.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });

                    }


                }
            }
        });


        mRoomlist = (RecyclerView) findViewById(R.id.drawerRecyclerView);
        mUsersList = (RecyclerView) findViewById(R.id.drawerRecyclerView1);


        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("chatroomlist");
        Query personsQuery = mUsersDatabase.orderByKey();

        mRoomlist.hasFixedSize();
        mRoomlist.setLayoutManager(new LinearLayoutManager(this));
        mUsersList.hasFixedSize();
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Rooms>().setIndexedQuery(mDatabase, mUsersDatabase, Rooms.class).build();

        mRoomlistadapter = new FirebaseRecyclerAdapter<Rooms, ChatRoom.RoomsViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(final ChatRoom.RoomsViewHolder holder, final int position, final Rooms model) {


                holder.setName(model.getRoomname());
                final String roomnameg = model.getRoomname();


                final String user_id = getRef(position).getKey();


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mRootRef.child("Users").child(Current_UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                change = "yes";


                                String username = Objects.requireNonNull(dataSnapshot.child("chatusername").getValue()).toString();

                                Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
                                intent.putExtra("way","old");

                                intent.putExtra("roomname", roomnameg);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }

            @Override
            public ChatRoom.RoomsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.roomsingle, parent, false);

                return new ChatRoom.RoomsViewHolder(view);
            }
        };

        mRoomlist.setAdapter(mRoomlistadapter);


        //Users list Adapter

        DatabaseReference mUsersDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference().child("ChatRoomUsersList").child(room);

        Query personsQuery1 = mDatabase1.orderByValue();


        FirebaseRecyclerOptions personsOptions1 = new FirebaseRecyclerOptions.Builder<Users>().setIndexedQuery(personsQuery1, mUsersDatabase1, Users.class).build();

        mUsersAdapter = new FirebaseRecyclerAdapter<Users, ChatRoom.UsersViewHolder>(personsOptions1) {
            @Override
            protected void onBindViewHolder(final ChatRoom.UsersViewHolder holder, final int position, final Users model) {

                final String user_id = getRef(position).getKey();
                mRootRef.child("chatroomlist").child(room).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("owner")) {
                            String owner = dataSnapshot.child("owner").getValue().toString();
                            holder.setName(model.getChatusername(), owner);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRootRef.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String available;



                        String online = dataSnapshot.child("online").getValue().toString();
                        String online1 = dataSnapshot.child("onlinechat").getValue().toString();
                        String online2 = dataSnapshot.child("onlinechatroom").getValue().toString();

                        if (online2.equals("true")){
                            available = "online";

                            holder.setOnline(available);
                        }else if (online.equals("true")|| online1.equals("true")){
                            available = "away";
                            holder.setOnline(available);
                        }else {
                            available = "offline";
                            holder.setOnline(available);
                        }


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });








                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        if (!Current_UserID.equals(user_id)) {

                            mRootRef.child("chatroomlist").child(room).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String owner = dataSnapshot.child("owner").getValue().toString();


                                    if (owner.equals(user)) {

                                        mRootRef.child("ChatBlock").child(Current_UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.hasChild(user_id)) {
                                                    mRootRef.child("Users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            final String usrname = dataSnapshot.child("chatusername").getValue().toString();

                                                            CharSequence options[] = new CharSequence[]{"Block " + usrname, "Kick " + usrname, "Cancel"};
                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                            builder.setTitle("Select");
                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {

                                                                    if (i == 0) {
                                                                        mRootRef.child("ChatBlock").child(Current_UserID).child(user_id).setValue(usrname);
                                                                        dialog.cancel();
                                                                        change="yes";
                                                                        Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                                        intent.putExtra("way","old");

                                                                        intent.putExtra("roomname", room);
                                                                        intent.putExtra("username", user);
                                                                        startActivity(intent);
                                                                    }

                                                                    if (i == 1) {

                                                                        mRootRef.child("ChatKick").child(room).child(user_id).setValue("yes");
                                                                        mRootRef.child("ChatRoomUsersList").child(room).child(user_id).setValue(null);
                                                                        mRootRef.child("ChatRoomMessages").child(room).child(user_id).setValue(null);

                                                                        dialog.cancel();

                                                                    }

                                                                    if (i == 2) {


                                                                        dialog.cancel();

                                                                    }

                                                                }
                                                            });
                                                            builder.show();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });


                                                } else if (dataSnapshot.hasChild(user_id)) {

                                                    mRootRef.child("Users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            final String usrname = dataSnapshot.child("chatusername").getValue().toString();

                                                            CharSequence options[] = new CharSequence[]{"UnBlock " + usrname, "Cancel"};
                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                            builder.setTitle("Select");
                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {

                                                                    if (i == 0) {
                                                                        mRootRef.child("ChatBlock").child(Current_UserID).child(user_id).setValue(null);
                                                                        dialog.cancel();
                                                                        change="yes";
                                                                        Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                                        intent.putExtra("way","old");

                                                                        intent.putExtra("roomname", room);
                                                                        intent.putExtra("username", user);
                                                                        startActivity(intent);
                                                                    }

                                                                    if (i == 1) {

                                                                        dialog.cancel();

                                                                    }

                                                                }
                                                            });
                                                            builder.show();

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


                                    } else {

                                        mRootRef.child("ChatBlock").child(Current_UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.hasChild(user_id)) {
                                                    mRootRef.child("Users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            final String usrname = dataSnapshot.child("chatusername").getValue().toString();

                                                            CharSequence options[] = new CharSequence[]{"Block " + usrname, "Cancel"};
                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                            builder.setTitle("Select");
                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {

                                                                    if (i == 0) {
                                                                        mRootRef.child("ChatBlock").child(Current_UserID).child(user_id).setValue(usrname);
                                                                        dialog.cancel();
                                                                        change="yes";
                                                                        Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                                        intent.putExtra("way","old");

                                                                        intent.putExtra("roomname", room);
                                                                        intent.putExtra("username", user);
                                                                        startActivity(intent);
                                                                    }

                                                                    if (i == 1) {


                                                                        dialog.cancel();

                                                                    }

                                                                }
                                                            });
                                                            builder.show();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });


                                                } else if (dataSnapshot.hasChild(user_id)) {

                                                    mRootRef.child("Users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            final String usrname = dataSnapshot.child("chatusername").getValue().toString();

                                                            CharSequence options[] = new CharSequence[]{"UnBlock " + usrname, "Cancel"};
                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                            builder.setTitle("Select");
                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {

                                                                    if (i == 0) {
                                                                        mRootRef.child("ChatBlock").child(Current_UserID).child(user_id).setValue(null);
                                                                        dialog.cancel();
                                                                        change="yes";
                                                                        Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                                        intent.putExtra("way","old");

                                                                        intent.putExtra("roomname", room);
                                                                        intent.putExtra("username", user);
                                                                        startActivity(intent);
                                                                    }

                                                                    if (i == 1) {

                                                                        dialog.cancel();

                                                                    }

                                                                }
                                                            });
                                                            builder.show();

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
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }









                    }
                });
            }

            @Override
            public ChatRoom.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chatroomsinngleusers, parent, false);

                return new ChatRoom.UsersViewHolder(view);
            }
        };

        mUsersList.setAdapter(mUsersAdapter);


        //Message Adapter


        DatabaseReference messageref = mRootRef.child("ChatRoomMessages").child(room).child(Current_UserID);

        Query messageQuery = messageref.limitToLast(50);

        messageref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                RoomMessages message = dataSnapshot.getValue(RoomMessages.class);



                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessageList.scrollToPosition(messagesList.size() - 1);



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
    public void onStart() {
        super.onStart();
        mRootRef.child("Users").child(Current_UserID).child("onlinechatroom").setValue("true");
        mRoomlistadapter.startListening();
        mUsersAdapter.startListening();

        mRootRef.child("ChatKick").child(rooomname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Current_UserID)) {
                    String remove = dataSnapshot.child(Current_UserID).getValue().toString();
                    if (remove.equals("yes")) {


                        CharSequence options[] = new CharSequence[]{"You have been kicked from this room. ", "Ok"};
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
                        builder.setTitle("Warning");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int i) {

                                if (i == 0) {


                                }

                                if (i == 1) {

                                    mRootRef.child("ChatRoomUsers").child(userrname).child(rooomname).setValue(null);
                                    mRootRef.child("ChatKick").child(rooomname).child(Current_UserID).setValue(null);

                                    mRootRef.child("ChatRoomUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(userrname)) {
                                                change = "yes";

                                                mRootRef.child("ChatRoomUsers").child(userrname).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String roomn = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                                        String remainder = roomn.substring(roomn.indexOf("=") + 1, roomn.length() - 1);
                                                        String roomnames;

                                                        if (remainder.contains(",")) {
                                                            String kept = remainder.substring(0, remainder.indexOf(","));

                                                            roomnames = kept;

                                                            Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                            intent.putExtra("way","old");

                                                            intent.putExtra("roomname", roomnames);
                                                            intent.putExtra("username", userrname);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                            startActivity(intent);
                                                        } else {
                                                            roomnames = remainder;
                                                            Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                            intent.putExtra("way","old");

                                                            intent.putExtra("roomname", roomnames);
                                                            intent.putExtra("username", userrname);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });


                                            } else {

                                                Intent intent = new Intent(ChatRoom.this, RoomSelect.class);
                                                intent.putExtra("root", "new");
                                                intent.putExtra("username", userrname);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                startActivity(intent);


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    dialog.cancel();

                                }

                            }
                        });
                        builder.setCancelable(false);
                        if (!ChatRoom.this.isFinishing()){
                            builder.show();


                        }


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mRootRef.child("chatroomlist").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(rooomname)) {

                    CharSequence options[] = new CharSequence[]{"Room doesn't exist anymore. ", "Ok"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
                    builder.setTitle("Warning");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int i) {

                            if (i == 0) {
                                dialog.cancel();

                            }

                            if (i == 1) {

                                mRootRef.child("ChatRoomUsers").child(userrname).child(rooomname).setValue(null);

                                mRootRef.child("ChatRoomUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(userrname)) {
                                            change = "yes";
                                            mRootRef.child("ChatRoomUsers").child(userrname).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String roomn = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                                    String remainder = roomn.substring(roomn.indexOf("=") + 1, roomn.length() - 1);
                                                    String roomnames;

                                                    if (remainder.contains(",")) {
                                                        String kept = remainder.substring(0, remainder.indexOf(","));

                                                        roomnames = kept;

                                                        Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                        intent.putExtra("way","old");

                                                        intent.putExtra("roomname", roomnames);
                                                        intent.putExtra("username", userrname);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                        startActivity(intent);
                                                    } else {
                                                        roomnames = remainder;
                                                        Intent intent = new Intent(ChatRoom.this, ChatRoom.class);
                                                        intent.putExtra("way","old");

                                                        intent.putExtra("roomname", roomnames);
                                                        intent.putExtra("username", userrname);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                        } else {

                                            Intent intent = new Intent(ChatRoom.this, RoomSelect.class);
                                            intent.putExtra("root", "new");

                                            intent.putExtra("username", userrname);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                            startActivity(intent);


                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                dialog.cancel();

                            }

                        }
                    });
                    builder.setCancelable(false);
                    if (!ChatRoom.this.isFinishing()) {
                        builder.show();


                    }



                }
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
    public void onStop() {
        super.onStop();
        mRoomlistadapter.stopListening();
        mUsersAdapter.stopListening();
        if (change.equals("no")){
            mRootRef.child("Users").child(Current_UserID).child("onlinechatroom").setValue("false");

        }
        else if (change.equals("yes")){
            mRootRef.child("Users").child(Current_UserID).child("onlinechatroom").setValue("true");

        }




    }

    public static class RoomsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public RoomsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView post_title = (TextView) mView.findViewById(R.id.roomnamesingle);
            post_title.setText(name);
        }


    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name,String owner) {
            TextView post_title = (TextView) mView.findViewById(R.id.singlechatusername);

            if (name.equals(owner)) {
                post_title.setText(name+" (Owner)");
            }else {
                post_title.setText(name);
            }
        }


        public void setOnline(String available) {
            CircleImageView
                    post_title = (CircleImageView) mView.findViewById(R.id.imgonlne);

            if (available.equals("online")) {
                post_title.setImageDrawable(getResources().getDrawable(R.drawable.onlinrgreen));
            }else if (available.equals("away")) {
                post_title.setImageDrawable(getResources().getDrawable(R.drawable.onlineorange));
            }
            else if (available.equals("offline")){
                post_title.setImageDrawable(getResources().getDrawable(R.drawable.onlinered));


            }
        }



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String text = "";

        Toast.makeText(this, "You have chosen " + text, Toast.LENGTH_LONG).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            mImageStorage = FirebaseStorage.getInstance().getReference();

            final String tim = String.valueOf(System.currentTimeMillis());




            StorageReference filepath = mImageStorage.child("chatroomimages").child(tim + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        final String download_url = task.getResult().getDownloadUrl().toString();






                        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("ChatRoomMessages").child(rooomname);

                        root.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                System.out.println(snapshot.getValue());
                                for (DataSnapshot childSnap : snapshot.getChildren()) {
                                    Map messageMap = new HashMap();
                                    messageMap.put("messages", download_url);
                                    messageMap.put("type", "image");
                                    messageMap.put("time", tim);
                                    messageMap.put("from", Current_UserID);
                                    messageMap.put("pvt","no");

                                    Map map = new HashMap<>();
                                    map.put(tim, messageMap);
                                    childSnap.getRef().updateChildren(map);
                                    mChatMessageVie.setText("");
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

    @Override
    protected void onRestart() {
        super.onRestart();
        mRootRef.child("Users").child(Current_UserID).child("onlinechatroom").setValue("true");


    }


    @Override
    protected void onResume() {
        super.onResume();
        mRootRef.child("Users").child(Current_UserID).child("onlinechatroom").setValue("true");

    }
}