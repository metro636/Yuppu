package com.techdoom.yuppu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.MessageViewHolder> {

    private Context context;


    private List<GroupMessages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    public GroupMessageAdapter(List<GroupMessages> mMessageList) {


        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single, parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName, displaytime;
        public ImageView messageImage;


        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            displaytime = (TextView) view.findViewById(R.id.time_text_layout);


        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();

        GroupMessages c = mMessageList.get(i);
        String from_user = c.getFrom();
        String message_type = c.getType();
        long time = c.getTime();

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        String lastSeenTime = getTimeAgo.getTimeAgo(time, context);
        viewHolder.displaytime.setText(lastSeenTime);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name + ":");

                Picasso.get().load(image)
                        .placeholder(R.drawable.ic_person_black_24dp).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (message_type.equals("text")) {
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
            viewHolder.messageText.setVisibility(View.VISIBLE);

            viewHolder.messageImage.setImageDrawable(null);
            viewHolder.messageText.setText(c.getMessages());


        }

        if (message_type.equals("image")) {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Glide.with(viewHolder.profileImage.getContext()).load(c.getMessages()).placeholder(R.drawable.download).into(viewHolder.messageImage);
        }

    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}







