/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yeezone.wifi;

import com.yeezone.aijiuyi.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

class WifiDialog extends Dialog implements OnCheckedChangeListener,View.OnClickListener,TextWatcher{
    static int BUTTON_SUBMIT = DialogInterface.BUTTON_POSITIVE;
    static final int BUTTON_FORGET = DialogInterface.BUTTON_NEUTRAL;
    static int BUTTON_DISCONNECT = DialogInterface.BUTTON_POSITIVE;
    private final DialogInterface.OnClickListener mListener;
    private final AccessPoint mAccessPoint;

    private View mView;
    private Context mContext;
    private EditText mPasswordView;
    private Button mButton;

    public WifiDialog(Context context, DialogInterface.OnClickListener listener,
            AccessPoint accessPoint) {
        super(context,R.style.dialog_style);
        mContext = context;
        mListener = listener;
        mAccessPoint = accessPoint;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mView = getLayoutInflater().inflate(R.layout.wifi_dialog, null);
        LayoutParams  lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        RelativeLayout r = new RelativeLayout(mContext);
        r.addView(mView, lp);
        setContentView(r);
        
        initView();
    }


	private void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(mAccessPoint.ssid);
		CheckBox check = (CheckBox) findViewById(R.id.showPassword);
		check.setOnCheckedChangeListener(this);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.addTextChangedListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		mButton = (Button) findViewById(R.id.connect);
		mButton.setOnClickListener(this);
		mButton.setEnabled(false);
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		System.out.println("isChecked = " +isChecked);
		if(isChecked)
			mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		else
			mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.connect) {
			mListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
		}else if(id == R.id.cancel){
			mListener.onClick(this, DialogInterface.BUTTON_NEGATIVE);
		}
		
	}
	
	public String getPassWord(){
		return mPasswordView.getText().toString();
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}


	@Override
	public void afterTextChanged(Editable s) {
		enableSubmitIfAppropriate();
	}
	
    void enableSubmitIfAppropriate() {
        boolean passwordInvalid = false;

        /* Check password invalidity for manual network set up alone */
        if (mPasswordView != null && 
            ((mAccessPoint.security == AccessPoint.SECURITY_WEP && WepInvalid()) ||
            (mAccessPoint.security == AccessPoint.SECURITY_PSK && mPasswordView.length() < 8))) {
            passwordInvalid = true;
        } else if (mPasswordView != null &&
        		mAccessPoint.security == AccessPoint.SECURITY_WAPI_PSK) {
            int passwdLength = mPasswordView.length();
            if (passwdLength < 8 || passwdLength > 64) {
                passwordInvalid = true;
            } 
        } 
        mButton.setEnabled(!passwordInvalid);
    }
	
    /*It's used to judge whether the password is correct of WEP auth type base on router daily*/
    private boolean WepInvalid() {
        if (mPasswordView != null) {
            int length = mPasswordView.length();
            String password = mPasswordView.getText().toString();
            // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
            if ((length == 10 || length == 26 || length == 58) &&
                    password.matches("[0-9A-Fa-f]*")) {
                return false;
            } else if (length == 5 || length == 13 || length == 29) {
                byte[] bytePassword = password.getBytes();
                int asciiPassword = 0;
                for (byte b : bytePassword) {
                    asciiPassword = (int)b;
                    if (asciiPassword < 0 || asciiPassword > 127) return true;
                }
                return false;
            }
       }
       return true;
   }
    
}
