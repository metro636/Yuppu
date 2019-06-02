package com.techdoom.yuppu;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupFriends extends AppCompatActivity {


    private RecyclerView mUsersList;
    private TextView mTest;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mRootRef;
    private FirebaseRecyclerAdapter<Users, FriendsActivity.FriendsViewHolder> mFriendsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_friends);
        final String group_id = getIntent().getStringExtra("group");
        final String group_name = getIntent().getStringExtra("name");

        setTitle("Select");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        //"News" here will reflect what you have called your database in Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid);

        mDatabase.keepSynced(true);

        mUsersList = (RecyclerView) findViewById(R.id.friends_list);


        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Query personsQuery = mUsersDatabase.orderByKey();


        mUsersList.hasFixedSize();
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Users>().setIndexedQuery(mDatabase, mUsersDatabase, Users.class).build();

        mFriendsAdapter = new FirebaseRecyclerAdapter<Users, FriendsActivity.FriendsViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(FriendsActivity.FriendsViewHolder holder, final int position, final Users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getImage());
                holder.setOnline(model.getOnline());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence options[] = new CharSequence[]{"Open Profile", "Add to group"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupFriends.this);
                        builder.setTitle("Select");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if (i == 0) {
                                    Intent intent = new Intent(getApplicationContext(), OthersProfileActivity.class);
                                    intent.putExtra("id", user_id);
                                    startActivity(intent);
                                }

                                if (i == 1) {

                                    mRootRef = FirebaseDatabase.getInstance().getReference().child("GroupChats").child(group_id);


                                    mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.hasChild(user_id)) {
                                                mRootRef = FirebaseDatabase.getInstance().getReference().child("GroupChats").child(group_id);

                                                Map map = new HashMap<>();
                                                map.put(user_id, user_id);
                                                mRootRef.updateChildren(map);

                                                DatabaseReference mRootRef1 = FirebaseDatabase.getInstance().getReference().child("Groupusers").child(user_id);
                                                Map map1 = new HashMap<>();
                                                map1.put(group_id, group_id);

                                                mRootRef1.updateChildren(map1);
                                                Intent intent = new Intent(getApplicationContext(), GroupChat.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                Toast.makeText(GroupFriends.this, "User added to group.",
                                                        Toast.LENGTH_SHORT).show();

                                                intent.putExtra("userid", group_id);
                                                intent.putExtra("name", group_name);
                                                startActivity(intent);


                                            } else {

                                                Toast.makeText(GroupFriends.this, "User already in group.",
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }

                            }
                        });
                        builder.show();


                    }
                });
            }

            @Override
            public FriendsActivity.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_singl_layout, parent, false);

                return new FriendsActivity.FriendsViewHolder(view);
            }
        };

        mUsersList.setAdapter(mFriendsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFriendsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFriendsAdapter.stopListening();


    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView post_title = (TextView) mView.findViewById(R.id.user_single_namef);
            post_title.setText(name);
        }

        public void setStatus(String status) {
            TextView post_desc = (TextView) mView.findViewById(R.id.user_single_statusf);
            post_desc.setText(status);
        }

        public void setImage(String image) {
            CircleImageView post_image = (CircleImageView) mView.findViewById(R.id.user_single_imgf);
            Picasso.get().load(image).into(post_image);
        }

        public void setOnline(String online) {
            ImageView onlinestat = (ImageView) mView.findViewById(R.id.user_onlinef);
            if (online.equals("true")) {
                onlinestat.setVisibility(View.VISIBLE);
            } else {
                onlinestat.setVisibility(View.INVISIBLE);
            }

        }


    }

}
