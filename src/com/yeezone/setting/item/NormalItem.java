package com.yeezone.setting.item;

import com.yeezone.aijiuyi.R;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

public class NormalItem extends Item implements View.OnClickListener{
	
	protected CompoundButton mSwitch;
	private OnItemClickListener mClickListener;

	public NormalItem(Context context) {
		super(context, R.layout.setting_item_normal);
	}
	

	@Override
	protected void initView() {
		super.initView();
		mSwitch = (CompoundButton) findViewById(R.id.switch_button);
		setHasNIcon(false);
		setHasSelectInfo(false);
		setHasSwitch(false);
		
		mRoot.setOnClickListener(this);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mClickListener = listener;
	}
	
	public void setHasSummary(boolean h) {
		mSummary.setVisibility(h ? View.VISIBLE:View.GONE);
	}
	
	public NormalItem setHasNIcon(boolean h) {
		if(h)
			findViewById(R.id.n_icon).setVisibility(View.VISIBLE);
		else
			findViewById(R.id.n_icon).setVisibility(View.GONE);
		return this;
	}
	
	public NormalItem setHasSelectInfo(boolean s){
		if(s)
			findViewById(R.id.message).setVisibility(View.VISIBLE);
		else
			findViewById(R.id.message).setVisibility(View.GONE);
		return this;
	}
	
	public NormalItem setHasSwitch(boolean h) {
		if(h)
			mSwitch.setVisibility(View.VISIBLE);
		else
			mSwitch.setVisibility(View.GONE);
		return this;
	}

	@Override
	public void onClick(View v) {
		if(mClickListener != null) {
			mClickListener.onClick(this, mId);
		}
	}
	
	
	public interface OnItemClickListener{
		public void onClick(NormalItem item,int id);
	}
	
}
