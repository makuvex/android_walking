/*
package com.friendly.walkingout;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendly.walkingout.notification.LoginData;
import NotificationUtil;
import MainService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    public static int notificationid = 0;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    // firebase 로그인 인증
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("", "@@@ onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("", "@@@ onAuthStateChanged:signed_out");
                }
            }
        };

        String email = "makuvex7@gmail.com";
        String password = "malice77";



        Intent intent = new Intent(MainActivity.this, MainService.class);
        startService(intent);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationUtil.getInstance(getApplicationContext()).makeNotification(1,
                        "우리 은비와 함께 산책할 시간입니다.",
                        "우리 은비와 함께 산책할 시간입니다.",
                        "어서 같이 나가셔야죠!");
            }
        });

        textView = (TextView) findViewById(R.id.loaded_textView);

        Button sendButton = (Button)findViewById(R.id.db_save);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText id = (EditText) findViewById(R.id.login_id);
                EditText password = (EditText) findViewById(R.id.login_password);


                LoginData data = new LoginData(id.getText().toString(), password.getText().toString());

//                databaseReference.child("users").push().setValue(data);  // 기본 database 하위 users child에 data를 list로 만들기
//                id.setText("");
//                password.setText("");

                createAccount("makuvex7@gmail.com", "malice77");

            }
        });

        databaseReference.child("users").addChildEventListener(new ChildEventListener() {  // users child의 이벤트를 수신합니다.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                 LoginData data = dataSnapshot.getValue(LoginData.class);  // chatData를 가져오고
                 textView.append(data.loginId + ", " + data.loginPassword+"\n");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText id = (EditText) findViewById(R.id.login_id);
                EditText password = (EditText) findViewById(R.id.login_password);

                mAuth.signInWithEmailAndPassword("makuvex7@gmail.com", "malice77")
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                Log.d("", "@@@ signInWithEmail:onComplete:" + task.isSuccessful());

                                Log.e("","### email : "+task.getResult().getUser().getEmail()+", uid : "+task.getResult().getUser().getUid());
// If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("", "@@@ signInWithEmail:failed", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed",  Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.w("", "@@@ signInWithEmail succeed");
                                    Toast.makeText(MainActivity.this, "Authentication succeed",  Toast.LENGTH_SHORT).show();

                                }
// …
                            }
                        });
            }
        });

        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
//                FirebaseAuth.getInstance().signOut();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean isValidPasswd(String target) {
        Pattern p = Pattern.compile("(^.*(?=.{6,100})(?=.*[0–9])(?=.*[a-zA-Z]).*$)");

        Matcher m = p.matcher(target);
        if (m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
            return true;
        }else{
            return false;
        }
    }

    private boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void createAccount(String email, String password) {
        if(!isValidEmail(email)){
            Log.e("", "createAccount: email is not valid ");
            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isValidPasswd(password)){
            Log.e("", "@@@ createAccount: password is not valid ");
            Toast.makeText(this, "Password is not valid", Toast.LENGTH_SHORT).show();
            return;
        }

//        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        Log.d("", "@@@ createUserWithEmail:onComplete:" + task.isSuccessful());
// If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication succeed",
                                    Toast.LENGTH_SHORT).show();
                        }
// [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.e("","@@@@@@ onAuthStateChanged email : " + user.getEmail() + ", uid : " + user.getUid());
        if(user != null) {
            Toast.makeText(this, "email : " + user.getEmail() + ", uid : " + user.getUid(), Toast.LENGTH_SHORT).show();
        }
    }
}
*/