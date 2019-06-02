package com.techdoom.yuppu;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private TextView mTest;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private ImageButton mBackbtn;

    private FirebaseRecyclerAdapter<Users, FriendsViewHolder> mFriendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        getSupportActionBar().hide();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        //"News" here will reflect what you have called your database in Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid);

        mDatabase.keepSynced(true);
        mBackbtn = (ImageButton) findViewById(R.id.back_btn);


        mUsersList = (RecyclerView) findViewById(R.id.friends_list);


        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Query personsQuery = mUsersDatabase.orderByKey();


        mUsersList.hasFixedSize();
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Users>().setIndexedQuery(mDatabase, mUsersDatabase, Users.class).build();

        mFriendsAdapter = new FirebaseRecyclerAdapter<Users, FriendsViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(FriendsViewHolder holder, final int position, final Users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getImage());
                holder.setOnline(model.getOnline());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                        builder.setTitle("Select");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if (i == 0) {
                                    Intent intent = new Intent(getApplicationContext(), OthersProfileActivity.class);
                                    intent.putExtra("id", user_id);

                                    intent.putExtra("act", "frnds");
                                    startActivity(intent);
                                }

                                if (i == 1) {
                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra("id", user_id);
                                    intent.putExtra("grp", "frnds");

                                    startActivity(intent);

                                }

                            }
                        });
                        builder.show();


                    }
                });
            }

            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_singl_layout, parent, false);

                return new FriendsViewHolder(view);
            }
        };

        mUsersList.setAdapter(mFriendsAdapter);


        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });
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
            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_person_black_24dp).into(post_image);
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