package com.chinalooke.android.cheju.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.utills.ImageTools;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserQcodeActivity extends AppCompatActivity {

    @Bind(R.id.iv_qcord)
    ImageView mIvQcord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_qcode);
        ButterKnife.bind(this);
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/qdcode.png");
        mIvQcord.setImageBitmap(bitmap);
    }
}
