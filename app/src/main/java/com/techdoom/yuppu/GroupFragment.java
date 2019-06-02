package com.techdoom.yuppu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private RecyclerView mUsersList;
    private TextView mTest;
    private DatabaseReference mDatabase, mUsersDatabase, mRoot;
    private FirebaseUser mCurrentUser;
    private FirebaseRecyclerAdapter<group, GroupFragment.conViewHolder> mFriendsAdapter;
    private View mMainView;
    private FirebaseAuth mAuth;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_group, container, false);

        mUsersList = (RecyclerView) mMainView.findViewById(R.id.grouplist);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        //"News" here will reflect what you have called your database in Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Groupusers").child(current_uid);

        mDatabase.keepSynced(true);


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("group");
        mRoot = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<group>().setIndexedQuery(mDatabase, mUsersDatabase, group.class).build();

        mFriendsAdapter = new FirebaseRecyclerAdapter<group, conViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(final GroupFragment.conViewHolder holder, final int position, final group model) {
                holder.setGroupname(model.getGroupname());
                final String grpname = model.getGroupname();


                final String user_id = getRef(position).getKey();

                DatabaseReference details = mRoot.child("grpmsg");
                details.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {

                            final String sender = Objects.requireNonNull(dataSnapshot.child(user_id).child("from").getValue()).toString();
                            String msg = Objects.requireNonNull(dataSnapshot.child(user_id).child("messages").getValue()).toString();
                            holder.setMessage(msg);
                            String time = Objects.requireNonNull(dataSnapshot.child(user_id).child("time").getValue()).toString();
                            holder.setTime(time);


                            if (sender != null) {

                                DatabaseReference users = mRoot.child("Users").child(sender);
                                users.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String name = dataSnapshot.child("name").getValue().toString();
                                        String image = dataSnapshot.child("image").getValue().toString();

                                        holder.setDetails(name, image);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), GroupChat.class);
                        intent.putExtra("userid", user_id);
                        intent.putExtra("name", grpname);

                        startActivity(intent);


                    }
                });
            }

            @Override
            public GroupFragment.conViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.conv_single, parent, false);

                return new GroupFragment.conViewHolder(view);
            }
        };

        mUsersList.setAdapter(mFriendsAdapter);
        return mMainView;

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

    public class conViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public conViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setGroupname(final String groupname) {
            TextView post_title = (TextView) mView.findViewById(R.id.convoname);
            post_title.setText(groupname);
        }

        public void setDetails(String name, String image) {
            TextView post_title = (TextView) mView.findViewById(R.id.grp_namee);
            post_title.setText(name + ":");

            CircleImageView prf_img = (CircleImageView) mView.findViewById(R.id.grpimggg);
            Picasso.get().load(image).placeholder(R.drawable.ic_person_black_24dp).into(prf_img);

        }

        public void setMessage(final String msg) {
            TextView post_title = (TextView) mView.findViewById(R.id.grppmssgg);
            post_title.setText(msg);
        }

        public void setTime(final String time) {
            TextView post_title = (TextView) mView.findViewById(R.id.convtime);

            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = Long.parseLong(time);

            String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getContext());
            post_title.setText(lastSeenTime);
        }


    }


}



