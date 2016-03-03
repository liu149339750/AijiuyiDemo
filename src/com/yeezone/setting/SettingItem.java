package com.yeezone.setting;

import com.yeezone.aijiuyi.R;
import com.yeezone.bluetooth.BluetoothActivity;
import com.yeezone.date.DateActivity;
import com.yeezone.display.DisplayActivity;
import com.yeezone.setting.item.Item;
import com.yeezone.sound.VoiceActivity;
import com.yeezone.wifi.WifiActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class SettingItem extends Item implements View.OnClickListener{

	
	public static int WLAN = 1;
	public static int BLUETOOTH  =  2;
	public static int VOICE = 3;
	public static int DISPLAY = 4;
	public static int DATE = 5;
	
	public SettingItem(Context context) {
		super(context, R.layout.setting_item);
	}
	
	protected void initView() {
		super.initView();
		mRoot.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		if(mId == DATE) {
			intent.setClass(mContext, DateActivity.class);
		} else if(mId == VOICE) {
			intent.setClass(mContext, VoiceActivity.class);
		} else if(mId == DISPLAY) {
			intent.setClass(mContext, DisplayActivity.class);
		} else if(mId == WLAN) {
			intent.setClass(mContext, WifiActivity.class);
			intent.putExtra("setting", true);
		} else if(mId == BLUETOOTH) {
			intent.setClass(mContext, BluetoothActivity.class);
		}
		mContext.startActivity(intent);
	}
	
}
