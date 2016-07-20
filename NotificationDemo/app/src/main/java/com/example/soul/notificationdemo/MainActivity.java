package com.example.soul.notificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NOTIFICATION_ID_BASIC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View viewById = findViewById(R.id.bt_normal);

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Tag","被点击了");
                openNormalNotification();
            }
        });
        findViewById(R.id.bt_dontDelete).setOnClickListener(this);
        findViewById(R.id.bt_cutum).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //普通
            case R.id.bt_normal:


                break;
            //不可删除
            case R.id.bt_dontDelete:

                break;
            //自定义
            case R.id.bt_cutum:
                break;

        }


    }

    private void openNormalNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, 0);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        builder.build().flags = Notification.FLAG_NO_CLEAR;

        builder.setContentTitle("Basic Notification");
        builder.setContentText("I am a basic notification");
        builder.setSubText("it is really basic");
        Log.i("Tag","发送消息");
        //发送消息
        NotificationManager systemService = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        systemService.notify(NOTIFICATION_ID_BASIC, builder.build());


    }
}
