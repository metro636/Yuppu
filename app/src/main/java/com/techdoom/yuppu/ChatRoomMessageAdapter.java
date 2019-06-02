package com.techdoom.yuppu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomMessageAdapter extends RecyclerView.Adapter<ChatRoomMessageAdapter.MessageViewHolder> {

    private Context context;


    private List<RoomMessages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    public ChatRoomMessageAdapter(List<RoomMessages> mMessageList) {


        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_room, parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;

        public TextView displayName, displaytime;
        public ImageView messageImage;
        public RelativeLayout layout;
         public ViewGroup.LayoutParams params;


        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);

            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            displaytime = (TextView) view.findViewById(R.id.time_text_layout);


            layout = (RelativeLayout)view.findViewById(R.id.message_single_layout);
           params = layout.getLayoutParams();




        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();
        String Current_UserID = mAuth.getCurrentUser().getUid();

        RoomMessages c = mMessageList.get(i);
        final String from_user = c.getFrom();
        String message_type = c.getType();
        String time = c.getTime();
        String priv = c.getPvt();
        long times = Long.parseLong(time);

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        String lastSeenTime = getTimeAgo.getTimeAgo(times, context);
        viewHolder.displaytime.setText(lastSeenTime);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("chatusername").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name + ":");



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference().child("ChatBlock").child(Current_UserID);
        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if (dataSnapshot.hasChild(from_user))

              {
                  viewHolder.params.height=0;
                  viewHolder.layout.setLayoutParams(viewHolder.params);
              }else {
                  viewHolder.layout.setLayoutParams(viewHolder.params);
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (priv.equals("yes")){
            viewHolder.messageText.setTypeface(viewHolder.messageText.getTypeface(),Typeface.ITALIC);
            viewHolder.layout.setBackgroundColor(Color.parseColor("#A3E4D7"));
        }else if (priv.equals("no")){
            viewHolder.messageText.setTypeface(viewHolder.messageText.getTypeface(),Typeface.NORMAL);
            viewHolder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }

        if (message_type.equals("text")) {
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
            viewHolder.messageText.setVisibility(View.VISIBLE);

            viewHolder.messageImage.setImageDrawable(null);
            viewHolder.messageText.setText(c.getMessages());


        }

        if (message_type.equals("image")) {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Glide.with(viewHolder.messageImage.getContext()).load(c.getMessages()).into(viewHolder.messageImage);
        }

    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}

