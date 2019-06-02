package com.techdoom.yuppu;

import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private DatabaseReference mDatabase;
    private ImageButton mBackbtn;

    private FirebaseRecyclerAdapter<Users, UsersViewHolder> mUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        getSupportActionBar().hide();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mBackbtn = (ImageButton) findViewById(R.id.back_btn);


        mUsersList = (RecyclerView) findViewById(R.id.users_list);

        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Query personsQuery = mUsersDatabase.orderByKey();

        mUsersList.hasFixedSize();
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Users>().setQuery(personsQuery, Users.class).build();

        mUsersAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(UsersViewHolder holder, final int position, final Users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getImage());
                holder.setOnline(model.getOnline());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getApplicationContext(), OthersProfileActivity.class);
                        intent.putExtra("id", user_id);
                        intent.putExtra("act", "users");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }
        };

        mUsersList.setAdapter(mUsersAdapter);


        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mUsersAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mUsersAdapter.stopListening();


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView post_title = (TextView) mView.findViewById(R.id.user_single_namef);
            post_title.setText(name);
        }

        public void setStatus(String status) {
            TextView post_desc = (TextView) mView.findViewById(R.id.user_single_status);
            post_desc.setText(status);
        }

        public void setImage(String image) {
            CircleImageView post_image = (CircleImageView) mView.findViewById(R.id.user_single_img);
            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_person_black_24dp).into(post_image);
        }

        public void setOnline(String online) {
            ImageView onlinestat = (ImageView) mView.findViewById(R.id.user_online);
            if (online.equals("true")) {
                onlinestat.setVisibility(View.VISIBLE);
            } else {
                onlinestat.setVisibility(View.INVISIBLE);
            }

        }
    }
}