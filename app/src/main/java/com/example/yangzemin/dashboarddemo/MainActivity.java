package com.example.yangzemin.dashboarddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DashBoardView boardView = (DashBoardView) findViewById(R.id.dashBoardView);
        boardView.setValues(1, 50);

        boardView.setCurrentValue(31);
    }
}
