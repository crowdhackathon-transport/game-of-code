package com.example.alberto.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.R.*;
/**
 * Created by andrewchron on 13/07/2015.
 */
public class ViewActivity extends Activity {
    TextView tvView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        tvView = (TextView) findViewById(R.id.tvView);
        Intent intent = getIntent();
        String Comment = intent.getStringExtra("Comment");
        tvView.setText(Comment);
    }
}
