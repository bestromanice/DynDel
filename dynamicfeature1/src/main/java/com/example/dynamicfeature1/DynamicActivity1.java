package com.example.dynamicfeature1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.google.android.play.core.splitcompat.SplitCompat;

public class DynamicActivity1 extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Emulates installation of on demand modules using SplitCompat.
        SplitCompat.installActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic1);

//        Intent intent = new Intent();
//        intent.setClassName("com.example.dynamicfeature1", "com.example.dynamicfeature1.MainActivityDF1");
//        startActivity(intent);


    }

}