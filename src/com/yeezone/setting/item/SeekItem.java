package com.yeezone.setting.item;

import com.yeezone.aijiuyi.R;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;

public class SeekItem extends Item implements SeekBar.OnSeekBarChangeListener{
	
	protected SeekBar mFirst;
	protected SeekBar mSecond;
	private boolean mIsSimple;
	private OnSeekChanageListener mChanageListener;
	
	public SeekItem(Context context) {
		super(context, R.layout.setting_item_seekbar);
	}
	
	@Override
	protected void initView() {
		super.initView();
		mFirst = (SeekBar) findViewById(R.id.seekbar1);
		mSecond = (SeekBar) findViewById(R.id.seekbar2);
		mFirst.setOnSeekBarChangeListener(this);
		mSecond.setOnSeekBarChangeListener(this);
		
		useSimpleSeek(false);
	}
	
	public SeekItem useSimpleSeek(boolean simple){
		mIsSimple = simple;
		if(simple) {
			findViewById(R.id.second).setVisibility(View.GONE);
			mFirst.setVisibility(View.VISIBLE);
		} else {
			mFirst.setVisibility(View.GONE);
			findViewById(R.id.second).setVisibility(View.VISIBLE);
		}
		return this;
	}
	
	public void setOnSeekChanageListener(OnSeekChanageListener l){
		mChanageListener = l;
	}
	
	public SeekBar getSeekBar(){
		if(mIsSimple)
			return mFirst;
		else
			return mSecond;
	}	
	
	public void setMax(int m) {
		getSeekBar().setMax(m);
	}
	
	public void setProgress(int progress) {
		getSeekBar().setProgress(progress);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(mChanageListener != null) 
			mChanageListener.onProgress(this, progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	
	public interface OnSeekChanageListener{
		public void onProgress(SeekItem item,int progress);
	}
}
