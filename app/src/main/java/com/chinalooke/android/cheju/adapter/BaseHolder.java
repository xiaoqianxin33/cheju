package com.chinalooke.android.cheju.adapter;

import android.view.View;

/**
 * @author  Administrator
 * @time 	2015-7-16 上午10:37:39
 * @des	TODO
 *
 * @version $Rev: 16 $
 * @updateAuthor $Author: admin $
 * @updateDate $Date: 2015-07-16 10:46:25 +0800 (星期四, 16 七月 2015) $
 * @updateDes TODO
 */
public abstract class BaseHolder<HODLERBEANTYPE> {

	public View				mHolderView;
	// 做holder需要持有孩子对象
	private HODLERBEANTYPE	mData;

	public BaseHolder() {
		// 初始化根布局
		mHolderView = initHolderView();
		// 绑定tag
		mHolderView.setTag(this);
	}

	/**
	 * @des 设置数据刷新视图
	 * @call 需要设置数据和刷新数据的时候调用
	 */
	public void setDataAndRefreshHolderView(HODLERBEANTYPE data) {
		// 保存数据
		mData = data;
		// 刷新显示
		refreshHolderView(data);
	}

	/**
	 * @des 初始化holderView/根视图
	 * @call BaseHolder 初始化的被调用
	 */
	public abstract View initHolderView();

	/**
	 * @des 刷新holder视图
	 * @call setDataAndRefreshHolderView()方法被调用的时候就被调用了吧
	 */
	public abstract void refreshHolderView(HODLERBEANTYPE data);

}
