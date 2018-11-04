package com.example.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.test.R;

public class OtherActivity extends Activity {
    //1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
//        Intent intent = new Intent();
//        String uname = intent.getStringExtra("uname");
//        Log.i("name", uname);
    }

    //2
    @Override
    protected void onStart() {
        super.onStart();
    }

    //3
    @Override
    protected void onResume() {
        super.onResume();
    }
}
