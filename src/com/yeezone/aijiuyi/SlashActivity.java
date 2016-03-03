package com.yeezone.aijiuyi;

import com.yeezone.wifi.WifiActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SlashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slash);
	}

	public void next(View v){
		Intent intent = new Intent();
		intent.setClass(this, WifiActivity.class);
		startActivity(intent);
	}
}
