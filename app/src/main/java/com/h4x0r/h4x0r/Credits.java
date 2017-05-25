package com.h4x0r.h4x0r;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;

public class Credits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onStop() {
        super.onStop();
        finish();
    }
}