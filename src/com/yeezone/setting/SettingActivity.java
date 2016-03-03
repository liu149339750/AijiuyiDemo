package com.yeezone.setting;

import com.yeezone.aijiuyi.R;
import com.yeezone.aijiuyi.BaseSetting;

import android.os.Bundle;

public class SettingActivity extends BaseSetting{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.setting);
		initView();
	}
	
	
	private void initView() {
		
		SettingItem wlan = new SettingItem(this);
		wlan.setTitleIcon(R.string.wlan, R.drawable.wlan);
		wlan.setId(SettingItem.WLAN);
		SettingItem bluetooth = new SettingItem(this);
		bluetooth.setTitleIcon(R.string.bluetooth, R.drawable.bluetooth);
		bluetooth.setId(SettingItem.BLUETOOTH);
		SettingItem voice = new SettingItem(this);
		voice.setTitleIcon(R.string.voice, R.drawable.voice);
		voice.setId(SettingItem.VOICE);
		SettingItem display = new SettingItem(this);
		display.setTitleIcon(R.string.display, R.drawable.display);
		display.setId(SettingItem.DISPLAY);
		SettingItem date = new SettingItem(this);
		date.setTitleIcon(R.string.date_time, R.drawable.date);
		date.setId(SettingItem.DATE);
		
		mContent.addView(wlan.getView());
		mContent.addView(bluetooth.getView());
		mContent.addView(voice.getView());
		mContent.addView(display.getView());
		mContent.addView(date.getView());
	}
}
