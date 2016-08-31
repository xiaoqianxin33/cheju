package com.chinalooke.android.cheju.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.chinalooke.android.cheju.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LipeiActivity extends AppCompatActivity {

    @Bind(R.id.btn_lipei)
    Button mBtnLipei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lipei);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_lipei)
    public void onClick() {
    }
}
