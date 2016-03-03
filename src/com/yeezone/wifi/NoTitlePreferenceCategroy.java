package com.yeezone.wifi;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.view.View;
import android.view.ViewGroup;

public class NoTitlePreferenceCategroy extends PreferenceCategory{

	public NoTitlePreferenceCategroy(Context context) {
		super(context);
	}

	
	
	@Override
	public View getView(View convertView, ViewGroup parent) {
		return new View(getContext());
	}
}
