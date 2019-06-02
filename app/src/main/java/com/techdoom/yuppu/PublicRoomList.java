package com.techdoom.yuppu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicRoomList extends AppCompatActivity {

    private RecyclerView mRoomlist;
    private DatabaseReference mDatabase, mRootRef;
    private Button mBackbtn;
    private FirebaseAuth mAuth;
    private String Current_UserID;
    private String username;

    private FirebaseRecyclerAdapter<Rooms, PublicRoomList.UsersViewHolder> mRoomAdaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_room_list);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mBackbtn = (Button) findViewById(R.id.btngoback);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser authdata = mAuth.getCurrentUser();
        Current_UserID = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        getSupportActionBar().hide();

        mRootRef.child("Users").child(Current_UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                username = Objects.requireNonNull(dataSnapshot.child("chatusername").getValue()).toString();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mRoomlist = (RecyclerView) findViewById(R.id.pubroomlist);

        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublicRoomList.this,RoomSelect.class);
                intent.putExtra("root","new");
                startActivity(intent);
            }
        });


        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("chatroomlist");
        Query personsQuery = mUsersDatabase.orderByValue();

        mRoomlist.hasFixedSize();
        mRoomlist.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Rooms>().setQuery(mUsersDatabase, Rooms.class).build();

        mRoomAdaper = new FirebaseRecyclerAdapter<Rooms, PublicRoomList.UsersViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(PublicRoomList.UsersViewHolder holder, final int position, final Rooms model) {
                holder.setRoomname(model.getRoomname());
                holder.setRoomdes(model.getRoomdes());
                holder.setHide(model.getHide());


                final String roomname = model.getRoomname();
                final String roomid = model.getRoomid();


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Intent intent = new Intent(PublicRoomList.this, ChatRoom.class);
                        intent.putExtra("roomname", roomname);
                        intent.putExtra("username", username);
                        intent.putExtra("way","new");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);



                        mRootRef.child("ChatRoomUsers").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.hasChild(roomname)){


                                    Map map2 = new HashMap<>();
                                    map2.put(roomname, roomname);

                                    mRootRef.child("ChatRoomUsers").child(username).updateChildren(map2);

                                    Map map3 = new HashMap<>();
                                    map3.put(Current_UserID, ServerValue.TIMESTAMP);
                                    mRootRef.child("ChatRoomUsersList").child(roomname).updateChildren(map3);
                                    mRootRef.child("ChatRoomMessages").child(roomname).updateChildren(map3);

                                    Map map4 = new HashMap<>();
                                    map4.put(Current_UserID,"no");
                                    mRootRef.child("ChatKick").child(roomname).updateChildren(map4);






                                    startActivity(intent);
                                    finish();





                                }else{

                                    Toast.makeText(getApplicationContext(), "You already joined this room.", Toast.LENGTH_SHORT).show();


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });





                    }
                });
            }

            @Override
            public PublicRoomList.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pubroomsingle, parent, false);

                return new PublicRoomList.UsersViewHolder(view);
            }
        };

        mRoomlist.setAdapter(mRoomAdaper);


       /* mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, NavActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });*/
    }


    @Override
    public void onStart() {
        super.onStart();
        mRoomAdaper.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRoomAdaper.stopListening();


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setRoomname(String name) {
            TextView post_title = (TextView) mView.findViewById(R.id.roomnamesingle);
            post_title.setText(name);
        }
        public void setHide(String hide) {
            RelativeLayout layout =(RelativeLayout)mView.findViewById(R.id.roomsinl);
            ViewGroup.LayoutParams params = layout.getLayoutParams();

            if (hide.equals("yes")){
                params.height= 0;
                layout.setLayoutParams(params);
            }else {
                layout.setLayoutParams(params);
            }
        }

        public void setRoomdes(String status) {
            TextView post_desc = (TextView) mView.findViewById(R.id.roomdessingle);
            post_desc.setText(status);
        }

    }
}