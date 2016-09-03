package com.chinalooke.android.cheju.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chinalooke.android.cheju.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.fl_takes})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fl_takes:
                finish();
                break;
        }
    }
}
