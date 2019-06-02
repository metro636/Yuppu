package com.techdoom.yuppu;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
public class ChatFragment extends Fragment {

    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<Conv, ChatFragment.ConvViewHolder> mConvAdapter;
    private String mCurrent_user_id;

    private View mMainView;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.convo_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("message").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment


        Query conversationQuery = mMessageDatabase.orderByChild("time");

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Conv>().setQuery(conversationQuery, Conv.class).build();

        mConvAdapter = new FirebaseRecyclerAdapter<Conv, ChatFragment.ConvViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(final ChatFragment.ConvViewHolder holder, final int position, final Conv model) {

                final String userid = mMessageDatabase.getRef().getKey();


                // holder.setMessages(model.getMessages(),model.getRead(),model.getFrom());

                final String user_id = model.getUnder();

                final String msg = model.getMessages();

                holder.setTime(model.getTime());


                mUsersDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (user_id != null) {
                            String name = Objects.requireNonNull(dataSnapshot.child(user_id).child("name").getValue()).toString();
                            String image = Objects.requireNonNull(dataSnapshot.child(user_id).child("image").getValue()).toString();


                            holder.setName(name);
                            holder.setImage(image);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if (user_id!=null) {

                    mMessageDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String abc = Objects.requireNonNull(dataSnapshot.child("read").getValue()).toString();
                            holder.setMessages(model.getMessages(), abc, model.getFrom());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("id", user_id);
                        mUsersDatabase.child(mCurrent_user_id).child("online").setValue("true");
                        mMessageDatabase.child(user_id).child("read").setValue("yes");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                        startActivity(intent);


                    }
                });
            }

            @Override
            public ChatFragment.ConvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.convo_single, parent, false);

                return new ChatFragment.ConvViewHolder(view);
            }
        };

        mConvList.setAdapter(mConvAdapter);

        return mMainView;

    }


    @Override
    public void onStart() {
        super.onStart();
        mConvAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mConvAdapter.stopListening();


    }


    public class ConvViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMessages(String messages, String abc, String from) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.ConvoMessage);
            userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);


            if (mCurrent_user_id.equals(from)) {

                userStatusView.setText("You:" + messages);
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);

            } else {

                userStatusView.setText(messages);

            }

            if (abc.equals("yes")) {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            } else if (abc.equals("no")) {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {

                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);


            }


        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.convoNAme);
            userNameView.setText(name);

        }


        public void setImage(String image) {

            CircleImageView userNameView = (CircleImageView) mView.findViewById(R.id.convoimg);
            Picasso.get().load(image).placeholder(R.drawable.ic_person_black_24dp).into(userNameView);

        }


        public void setTime(long time) {


            TextView userNameView = (TextView) mView.findViewById(R.id.ConvoTime);

            GetTimeAgo getTimeAgo = new GetTimeAgo();
            String lastSeenTime = getTimeAgo.getTimeAgo(time, getContext());
            userNameView.setText(lastSeenTime);

        }


    }


}


