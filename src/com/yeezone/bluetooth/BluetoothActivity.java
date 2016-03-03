package com.yeezone.bluetooth;

import com.yeezone.aijiuyi.BaseSetting;
import com.yeezone.aijiuyi.R;
import com.yeezone.aijiuyi.R.layout;
import com.yeezone.aijiuyi.R.string;
import com.yeezone.setting.item.SwitchItem;
import com.yeezone.setting.item.SwitchItem.OnCheckChanageListener;

import android.os.Bundle;
import android.view.View;

public class BluetoothActivity extends BaseSetting implements View.OnClickListener{

	private BluetoothEnabler mBlueEnabler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SwitchItem item = new SwitchItem(this);
		item.setHasSummary(false);
		item.setTitle(R.string.open);
		mBlueEnabler = new BluetoothEnabler(this, item.getSwitch());
		addItem(item);
		View view =  getLayoutInflater().inflate(R.layout.bluetooth_main, null);
		addRootView(view);
		
		View v = findViewById(R.id.right_image);
		v.setVisibility(View.VISIBLE);
		v.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mBlueEnabler.resume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mBlueEnabler.pause();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.right_image){
			
		}
	}
}
