package com.yeezone.aijiuyi;

import com.yeezone.aijiuyi.R;
import com.yeezone.aijiuyi.R.id;
import com.yeezone.aijiuyi.R.layout;
import com.yeezone.setting.item.Item;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BaseSetting extends Activity{

	protected ViewGroup mContent;
	private TextView mTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		mContent = (ViewGroup) findViewById(R.id.content);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(getTitle());
		
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		if(mTitle != null)
			mTitle.setText(title);
	}
	
	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);
		if(mTitle != null)
			mTitle.setText(titleId);
	}
	
	public void addItem(Item item) {
		mContent.addView(item.getView());
	}
	
	public void addView(View v) {
		mContent.addView(v);
	}
	
	public void addView(View v,LayoutParams lp) {
		mContent.addView(v, lp);
	}
	
	public void addRootView(View v) {
		addContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
}
