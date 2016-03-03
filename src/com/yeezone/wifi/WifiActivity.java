package com.yeezone.wifi;

import com.yeezone.aijiuyi.R;
import com.yeezone.setting.SettingActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class WifiActivity extends PreferenceActivity implements OnCheckedChangeListener,View.OnClickListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_main);
		ViewGroup v = (ViewGroup) findViewById(R.id.title_content);
		boolean setting = getIntent().getBooleanExtra("setting", false);
		View root = getLayoutInflater().inflate(R.layout.wifi_header, v);
		TextView title = (TextView) root.findViewById(R.id.title);
		Button right = (Button) root.findViewById(R.id.right_text);
		CompoundButton cb = (CompoundButton) findViewById(R.id.switch_bt);
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		cb.setChecked(wm.isWifiEnabled());
		cb.setOnCheckedChangeListener(this);
		if(!setting) {
			root.findViewById(R.id.back).setVisibility(View.GONE);
			findViewById(R.id.wifi_switch).setVisibility(View.GONE);
			title.setText("ÇëÑ¡ÔñWLANÍøÂç");
			right.setText("Ìø¹ý");
			right.setOnClickListener(this);
		} else{
			root.findViewById(R.id.back).setOnClickListener(this);
			title.setText("WLAN");
			findViewById(R.id.next).setVisibility(View.GONE);
			ImageView image = (ImageView) findViewById(R.id.right_image);
			image.setVisibility(View.VISIBLE);
			image.setOnClickListener(this);
		}

	}

	public void next(View v){
		Intent intent = new Intent();
		intent.setClass(this, SettingActivity.class);
		startActivity(intent);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wm.setWifiEnabled(isChecked);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.back)
			finish();
		else if( id == R.id.right_image) {
			
		} else if( id == R.id.right_button) {
			
		}
	}
}
