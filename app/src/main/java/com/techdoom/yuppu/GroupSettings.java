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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupSettings extends AppCompatActivity {

    private Button mAddBtn, mLeaveBtn;
    private RecyclerView mGroupList;
    private DatabaseReference mRoot;
    private FirebaseUser mCurrent_user;
    private TextView heading;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Users, GroupSettings.MembersViewHolder> mMembersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        getSupportActionBar().hide();

        mAddBtn = (Button) findViewById(R.id.btn_add_users);
        mLeaveBtn = (Button) findViewById(R.id.btn_leave);
        final String group_id = getIntent().getStringExtra("group");
        final String group_name = getIntent().getStringExtra("name");
        mRoot = FirebaseDatabase.getInstance().getReference();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        heading = (TextView) findViewById(R.id.grpmembers);
        String currentUser = mCurrent_user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChats").child(group_id);

        mDatabase.keepSynced(true);

        mGroupList = (RecyclerView) findViewById(R.id.grp_list);

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GroupSettings.this, GroupFriends.class);
                intent.putExtra("group", group_id);
                intent.putExtra("name", group_name);
                startActivity(intent);

            }
        });

        mLeaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map map = new HashMap<>();
                map.put("GroupChats/" + group_id + "/" + mCurrent_user.getUid(), null);
                map.put("Groupusers/" + mCurrent_user.getUid() + "/" + group_id, null);
                mRoot.updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError == null) {
                            Intent intent = new Intent(GroupSettings.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(GroupSettings.this, "Some error occured please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });


        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Query personsQuery = mUsersDatabase.limitToLast(1);


        mGroupList.hasFixedSize();
        mGroupList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Users>().setIndexedQuery(mDatabase, mUsersDatabase, Users.class).build();

        mMembersAdapter = new FirebaseRecyclerAdapter<Users, GroupSettings.MembersViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(GroupSettings.MembersViewHolder holder, final int position, final Users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getImage());


            }

            @Override
            public GroupSettings.MembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_singl_layout, parent, false);

                return new GroupSettings.MembersViewHolder(view);
            }
        };

        mGroupList.setAdapter(mMembersAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMembersAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMembersAdapter.stopListening();


    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MembersViewHolder(View itemView) {
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


    }
}
