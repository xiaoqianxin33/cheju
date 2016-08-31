package com.chinalooke.android.cheju.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

/**
 * Created by xiao on 2016/8/21.
 */

public class CheckableImageView extends ImageView implements Checkable {

    /**
     * @param context
     * @param attrs
     */
    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    private boolean mChecked = false;

    @Override
    public void setChecked(boolean checked) {
        // TODO Auto-generated method stub
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        // TODO Auto-generated method stub
        return mChecked;
    }

    @Override
    public void toggle() {
        // TODO Auto-generated method stub
        setChecked(!mChecked);
    }

}
