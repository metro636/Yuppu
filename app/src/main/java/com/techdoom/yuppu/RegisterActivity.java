package com.techdoom.yuppu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mRegister,mBack;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getSupportActionBar().hide();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mDisplayName = (EditText) findViewById(R.id.tvName);

        mEmail = (EditText) findViewById(R.id.tvEmail);
        mPassword = (EditText) findViewById(R.id.tvPassword);
        mRegister = (Button) findViewById(R.id.rgstr_btn);
        mBack =(Button)findViewById(R.id.btnbackstart) ;
        mAuth = FirebaseAuth.getInstance();
        mBack.setTransformationMethod(null);
        mRegister.setTransformationMethod(null);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });




        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (TextUtils.isEmpty(displayName)) {
                    Toast.makeText(getApplicationContext(), "Enter your name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                registeruser(displayName, email, password);
            }
        });
    }

    private void registeruser(final String displayname, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();
                            String token_id = FirebaseInstanceId.getInstance().getToken();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", displayname);
                            userMap.put("status", "Hi there! Am new here");
                            userMap.put("image", "default");
                            userMap.put("bgimage", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("thumb_bgimage", "default");
                            userMap.put("token_id", token_id);
                            userMap.put("online", "true");
                            userMap.put("onlinechat", "true");
                            userMap.put("lastseen", "on");
                            userMap.put("notifications", "on");
                            userMap.put("email",email);
                            userMap.put("chatroomhide","no");

                            mDatabase.setValue(userMap);

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            intent.putExtra("email", email);

                            startActivity(intent);
                            finish();

                            // FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
