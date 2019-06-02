package com.techdoom.yuppu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RoomSelect extends AppCompatActivity {

    private Button btnPublic,Btncreate,btnJoin,btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_select);
        getSupportActionBar().hide();



        final String root = getIntent().getStringExtra("root");
        final String room = getIntent().getStringExtra("roomname");
        final String user = getIntent().getStringExtra("username");



        btnJoin = (Button)findViewById(R.id.btnJoin);
        Btncreate=(Button)findViewById(R.id.btnCreate);
        btnPublic=(Button)findViewById(R.id.btnPublic);
        btnBack = (Button)findViewById(R.id.btngoback);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (root.equals("new")){
                    Intent intent = new Intent(RoomSelect.this,MainActivity.class);
                    startActivity(intent);
                }else if (root.equals("main")){
                    Intent intent = new Intent(RoomSelect.this,ChatRoom.class);
                    intent.putExtra("roomname",room);
                    intent.putExtra("username",user);
                    startActivity(intent);
                }else if (root.equals("chat")){
                    Intent intent = new Intent(RoomSelect.this,MainActivity.class);
                    startActivity(intent);

                }
            }
        });

        btnPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RoomSelect.this,PublicRoomList.class);
                startActivity(intent);
            }
        });

        Btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RoomSelect.this,CreateRoom.class);
                startActivity(intent);

            }
        });
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RoomSelect.this,RoomSearch.class);
                startActivity(intent);

            }
        });

    }
}
