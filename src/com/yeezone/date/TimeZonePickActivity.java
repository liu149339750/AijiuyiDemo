package com.yeezone.date;

import com.yeezone.aijiuyi.R;

import android.app.Activity;
import android.os.Bundle;

public class TimeZonePickActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new ZonePicker()).commit();
		}
	}
}
