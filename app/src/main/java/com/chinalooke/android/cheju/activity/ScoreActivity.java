package com.chinalooke.android.cheju.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.utills.NetUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScoreActivity extends AppCompatActivity {

    @Bind(R.id.tv_score)
    TextView mTvScore;
    private AVUser mCurrentUser;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        initData();
    }

    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mCurrentUser.fetchIfNeededInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    showScore();
                }
            });
        } else {
            mToast.setText("网络不可用，无法获取最新积分余额");
            mToast.show();
            showScore();
        }
    }

    private void showScore() {
        mTvScore.setText(mCurrentUser.getNumber("score") + "");
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
