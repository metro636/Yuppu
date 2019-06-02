package com.techdoom.yuppu;


import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.support.design.widget.TabLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ViewPager mViewPager;
    private SectionsPageAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancelAll();
        mViewPager = (ViewPager) findViewById(R.id.tabPager);

        mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        mTabLayout.setBackgroundColor(Color.parseColor("#673AB7"));
        mTabLayout.setTabTextColors(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));



        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                changeTabs(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.child("online").setValue("true");
        mUserRef.child("timee").setValue(ServerValue.TIMESTAMP);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View mView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        final TextView mUserName = (TextView) mView.findViewById(R.id.textView3);

        final ImageView mBgImage = (ImageView) mView.findViewById(R.id.imageView);
        final CircleImageView mPimage = (CircleImageView) mView.findViewById(R.id.userImg);
        //TextView mMail = (TextView)findViewById(R.id.emainav);

        String text = getIntent().getStringExtra("email");

        // mMail.setText(text);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        final MenuItem chatroom=menu.findItem(R.id.nav_share);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("chatroomhide").getValue().toString();
                if (value.equals("yes")){
                    chatroom.setVisible(false);

                }else if (value.equals("no")){
                    chatroom.setVisible(true);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = mCurrentUser.getUid();
        String token_id = FirebaseInstanceId.getInstance().getToken();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
        mUserDatabase.child("token_id").setValue(token_id);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("name")) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    mUserName.setText(name);

                }

                if (dataSnapshot.hasChild("image")) {

                    final String image = dataSnapshot.child("image").getValue().toString();


                    if (!image.equals("default")) {

                        //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                        Picasso.get().load(image).placeholder(R.drawable.ic_people_outline_black_24dp)
                                .into(mPimage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {

                                        Picasso.get().load(image).placeholder(R.drawable.ic_people_outline_black_24dp).into(mPimage);
                                    }
                                });
                    }
                }
                if (dataSnapshot.hasChild("bgimage")) {


                    final String bgimage = dataSnapshot.child("bgimage").getValue().toString();


                    if (!bgimage.equals("default")) {

                        //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                        Picasso.get().load(bgimage).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.color.colorAccent
                                ).into(mBgImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                                Picasso.get().load(bgimage).placeholder(R.color.colorAccent).into(mBgImage);


                            }


                        });

                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void changeTabs(int position) {

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mUserRef.child("online").setValue("false");
            mUserRef.child("token_id").setValue("");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            // return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intentprof = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intentprof);

        } else if (id == R.id.nav_friends) {
            Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_group) {

            LayoutInflater linf = LayoutInflater.from(this);
            final View inflator = linf.inflate(R.layout.dialogbox, null);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Create Group");
            alert.setMessage("Lets give a name for your group!");
            alert.setView(inflator);

            final EditText et1 = (EditText) inflator.findViewById(R.id.groupName);


            alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String s1 = et1.getText().toString();

                    Intent intent = new Intent(getApplicationContext(), GroupChat.class);
                    intent.putExtra("name", s1);
                    startActivity(intent);


                    //do operations using s1 and s2 here...
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });

            alert.show();


        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MainActivity.this,Username.class);
            startActivity(intent);


        } else if (id == R.id.nav_send) {

            mUserRef.child("online").setValue("false");
            mUserRef.child("token_id").setValue("");
            FirebaseAuth.getInstance().signOut();


            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else if (id==R.id.nav_help){
            Intent intent = new Intent(MainActivity.this,Help.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserRef.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserDatabase.child("timee").setValue(ServerValue.TIMESTAMP);
        mUserRef.child("online").setValue("false");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserDatabase.child("timee").setValue(ServerValue.TIMESTAMP);
        mUserRef.child("online").setValue("false");


    }
}

