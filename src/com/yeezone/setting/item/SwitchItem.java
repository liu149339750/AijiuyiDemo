package com.yeezone.setting.item;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SwitchItem extends NormalItem implements OnCheckedChangeListener{

	private boolean mIsOn;
	private OnCheckChanageListener mChanageListener;
	public SwitchItem(Context context) {
		super(context);
	}
	
	@Override
	protected void initView() {
		super.initView();
		setHasSwitch(true);
		mSwitch.setOnCheckedChangeListener(this);
	}
	
	public void setChecked(boolean on) {
		mSwitch.setChecked(on);
		mIsOn = on;
	}
	
	public CompoundButton getSwitch() {
		return mSwitch;
	}
	
	public void setOnCheckChanageListener(OnCheckChanageListener l) {
		mChanageListener = l;
	}
	
	public boolean isCheck(){
		return mIsOn;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mIsOn = isChecked;
		if(mChanageListener != null)
			mChanageListener.onCheckedChanged(this, isChecked);
	}
	
	public interface OnCheckChanageListener{
		public void onCheckedChanged(SwitchItem item,boolean isChecked);
	}
}
