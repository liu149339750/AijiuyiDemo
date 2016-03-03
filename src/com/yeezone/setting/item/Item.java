package com.yeezone.setting.item;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Item {
	
	private TextView mTitle;
	protected TextView mSummary;
	private ImageView mIcon;
	protected View mRoot;
	protected Context mContext;
	private int mLayout;
	private boolean isEnable = true;
	
	protected int mId;

	public Item(Context context,int layout){
		mContext = context;
		mLayout = layout;
		init();
		initView();
	}
	
	protected void init() {
	}
	
	public void setEnabled(boolean enable){
		isEnable = enable;
		notifyStatus();
	}

	protected void notifyStatus() {
		setEnabledStateOnViews(mRoot, isEnable);
		if(!isEnable)
			mRoot.setAlpha((float) 0.5);
		else
			mRoot.setAlpha(1);
	}
	
    private void setEnabledStateOnViews(View v, boolean enabled) {
        v.setEnabled(enabled);
        
        if (v instanceof ViewGroup) {
            final ViewGroup vg = (ViewGroup) v;
            for (int i = vg.getChildCount() - 1; i >= 0; i--) {
                setEnabledStateOnViews(vg.getChildAt(i), enabled);
            }
        }
    }

	public void setLayout(int l) {
		mLayout = l;
		initView();
	}
	
	protected void initView() {
		mRoot = LayoutInflater.from(mContext).inflate(mLayout, null);
		mTitle = (TextView) mRoot.findViewById(R.id.title);
		mSummary = (TextView) mRoot.findViewById(R.id.summary);
		mIcon = (ImageView) mRoot.findViewById(R.id.icon);
	}
	
	public void setId(int id){
		this.mId = id;
	}
	
	public int getId(){
		return mId;
	}
	
	public View findViewById(int id) {
		return mRoot.findViewById(id);
	}
	
	public View getView() {
		return mRoot;
	}
	
	public void setTitle(String title) {
		if(mTitle != null)
			mTitle.setText(title);
	}
	
	public void setTitle(int titleRes) {
		if(mTitle != null)
			mTitle.setText(titleRes);
	}
	
	public void setSummary(int summary) {
		if(mSummary != null)
			mSummary.setText(summary);
	}
	
	public void setSummary(String summary) {
		if(mSummary != null)
			mSummary.setText(summary);
	}
	
	public void setIcon(Drawable d) {
		if(mIcon != null)
			mIcon.setImageDrawable(d);
	}
	
	public void setIcon(int resId) {
		if(mIcon != null)
			mIcon.setImageResource(resId);;
	}
	
	public void setTitleIcon(int res,int dres) {
		mTitle.setText(res);
		mIcon.setImageResource(dres);
	}
	
	public String getTitle() {
		return mTitle.getText().toString();
	}
	
}
