package com.techdoom.yuppu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotificationFragment extends Fragment {
    private RecyclerView mNotList;

    private DatabaseReference mNotifydatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<notify, NotificationFragment.NotViewHolder> mNotifyAdapter;
    private String mCurrent_user_id;
    private TextView message;

    private View mMainView;


    public NotificationFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        mNotList = mMainView.findViewById(R.id.convo_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mNotifydatabase = FirebaseDatabase.getInstance().getReference().child("Notify").child(mCurrent_user_id);

        mNotifydatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mRootRef = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mNotList.setHasFixedSize(true);
        mNotList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment


        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<notify>().setQuery(mNotifydatabase, notify.class).build();

        mNotifyAdapter = new FirebaseRecyclerAdapter<notify, NotificationFragment.NotViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(final NotificationFragment.NotViewHolder holder, final int position, final notify model) {

                final String user_id = model.getFrom();

                holder.setTime(model.getTime());

                if (user_id != null) {

                    mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String name = Objects.requireNonNull(dataSnapshot.child(user_id).child("name").getValue()).toString();
                            final String image = Objects.requireNonNull(dataSnapshot.child(user_id).child("image").getValue()).toString();

                            holder.setName(name);
                            holder.setImage(image);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                holder.setType(model.getType());


                // holder.setMessages(model.getMessages(),model.getRead(),model.getFrom());

                final String type = model.getType();


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (type.equals("request")) {

                            CharSequence options[] = new CharSequence[]{"Open Profile", "Delete"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle("Select");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {

                                    if (i == 0) {
                                        Intent intent = new Intent(getContext(), OthersProfileActivity.class);
                                        intent.putExtra("id", user_id);
                                        intent.putExtra("act", "main");
                                        startActivity(intent);
                                    }

                                    if (i == 1) {
                                        if (mCurrent_user_id != null) {

                                            DatabaseReference notify1 = mRootRef.child("Notify").child(mCurrent_user_id);
                                            Map notifymap2 = new HashMap<>();
                                            notifymap2.put(user_id + "/" + "type", null);
                                            notifymap2.put(user_id + "/" + "from", null);
                                            notifymap2.put(user_id + "/" + "time", null);

                                            notify1.updateChildren(notifymap2);

                                        }

                                    }

                                }
                            });
                            builder.show();
                        } else if (type.equals("accept")) {

                            CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message", "Remove"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle("Select");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {

                                    if (i == 0) {
                                        Intent intent = new Intent(getContext(), OthersProfileActivity.class);
                                        intent.putExtra("id", user_id);
                                        startActivity(intent);
                                    }

                                    if (i == 1) {

                                        Intent intent = new Intent(getContext(), ChatActivity.class);
                                        intent.putExtra("id", user_id);
                                        startActivity(intent);
                                        if (mCurrent_user_id != null) {

                                            DatabaseReference notify1 = mRootRef.child("Notify").child(mCurrent_user_id);
                                            Map notifymap2 = new HashMap<>();
                                            notifymap2.put(user_id + "/" + "type", null);
                                            notifymap2.put(user_id + "/" + "from", null);
                                            notifymap2.put(user_id + "/" + "time", null);

                                            notify1.updateChildren(notifymap2);

                                        }

                                    }

                                    if (i == 2) {
                                        if (mCurrent_user_id != null) {

                                            DatabaseReference notify1 = mRootRef.child("Notify").child(mCurrent_user_id);
                                            Map notifymap2 = new HashMap<>();
                                            notifymap2.put(user_id + "/" + "type", null);
                                            notifymap2.put(user_id + "/" + "from", null);
                                            notifymap2.put(user_id + "/" + "time", null);

                                            notify1.updateChildren(notifymap2);

                                        }

                                    }

                                }
                            });
                            builder.show();

                        }


                    }
                });
            }

            @Override
            public NotificationFragment.NotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notify_single, parent, false);

                return new NotificationFragment.NotViewHolder(view);
            }
        };

        mNotList.setAdapter(mNotifyAdapter);


        return mMainView;

    }


    @Override
    public void onStart() {
        super.onStart();
        mNotifyAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mNotifyAdapter.stopListening();


    }


    public class NotViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public NotViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {

            TextView userStatusView = mView.findViewById(R.id.tvsendernam);
            userStatusView.setText(name + " :");

        }

        public void setImage(String image) {

            CircleImageView post_image = mView.findViewById(R.id.ntimage);
            Picasso.get().load(image).into(post_image);


        }

        public void setType(String type) {

            TextView userStatusView = mView.findViewById(R.id.tv_notify);

            if (type.equals("request")) {
                userStatusView.setText(" Sent you a friend request");

            } else if (type.equals("accept")) {
                userStatusView.setText("Accepted your friend request");
            }


        }


        public void setTime(long time) {


            TextView userNameView = mView.findViewById(R.id.noti_time);

            GetTimeAgo getTimeAgo = new GetTimeAgo();
            String lastSeenTime = GetTimeAgo.getTimeAgo(time, getContext());
            userNameView.setText(lastSeenTime);

        }


    }


}


