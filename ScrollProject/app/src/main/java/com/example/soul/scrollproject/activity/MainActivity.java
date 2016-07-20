package com.example.soul.scrollproject.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.soul.scrollproject.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_overscroll).setOnClickListener(this);
        findViewById(R.id.bt_pulldown).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_overscroll:
                startActivity(new Intent(MainActivity.this,OverScrollActivity.class));
                break;
            case R.id.bt_pulldown:
                startActivity(new Intent(MainActivity.this,PullDownActivity.class));
                break;


        }
    }
}
