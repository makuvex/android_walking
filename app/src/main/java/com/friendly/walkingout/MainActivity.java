package com.friendly.walkingout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.friendly.walkingout.notification.NotificationUtil;
import com.friendly.walkingout.service.MainService;

public class MainActivity extends AppCompatActivity {

    public static int notificationid = 0;
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
    }


}
