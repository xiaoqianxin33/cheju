package com.chinalooke.android.cheju.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QRCodeActivity extends AppCompatActivity {

    @Bind(R.id.iv_qcord)
    ImageView mIvQcord;
    @Bind(R.id.pb_qr)
    ProgressBar mPbQr;
    private AVObject mOrder;
    private AVUser mCurrentUser;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        initData();
    }

    private void initData() {
        mOrder = getIntent().getParcelableExtra("order");
        String mGoods = mOrder.getString("goodsId");
        String mCount = mOrder.getNumber("count") + "";
        final String mPrice = mOrder.getNumber("price") + "";
        String message = mCurrentUser.getObjectId() + ":" + mGoods
                + ":" + mCount + ":" + mPrice;


        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest imageRequest = new ImageRequest("http://api.k780.com:88/?app=qr.get&data=" + message + "&level=L&size=6",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mIvQcord.setImageBitmap(bitmap);
                        mPbQr.setVisibility(View.GONE);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mToast.setText("二维码查看失败，请检查网络连接");
                mToast.show();
            }
        });

        mQueue.add(imageRequest);
    }

    @OnClick(R.id.iv_wirte_back)
    public void onClick() {
        finish();
    }
}
