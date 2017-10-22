package com.friendly.walkingout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.friendly.walkingout.notification.LoginData;
import com.friendly.walkingout.notification.NotificationUtil;
import com.friendly.walkingout.service.MainService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static int notificationid = 0;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                databaseReference.child("users").push().setValue(data);  // 기본 database 하위 users child에 data를 list로 만들기
                id.setText("");
                password.setText("");
            }
        });

        databaseReference.child("users").addChildEventListener(new ChildEventListener() {  // users child의 이벤트를 수신합니다.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                 LoginData data = dataSnapshot.getValue(LoginData.class);  // chatData를 가져오고
                 textView.append(data.loginId + ", " + data.loginPassword);
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
    }

}
