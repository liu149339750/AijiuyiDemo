package com.yeezone.sound;

import com.yeezone.aijiuyi.R;
import com.yeezone.aijiuyi.BaseSetting;
import com.yeezone.aijiuyi.R.string;
import com.yeezone.setting.item.NormalItem;
import com.yeezone.setting.item.NormalItem.OnItemClickListener;

import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.KeyEvent;

public class VoiceActivity extends BaseSetting implements OnItemClickListener{
	
	private SoundSetting mVoices ;
	private RingItem mRing;
	private RingItem mDefaultRing;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mVoices = new SoundSetting(this);
		addItem(mVoices);
		mRing = new RingItem(this,RingtoneManager.TYPE_RINGTONE);
		mRing.setTitle(R.string.ring);
		mRing.setOnItemClickListener(this);
		addItem(mRing);
		
		mDefaultRing = new RingItem(this,RingtoneManager.TYPE_NOTIFICATION);
		mDefaultRing.setTitle(R.string.default_ring);
		mDefaultRing.setOnItemClickListener(this);
		addItem(mDefaultRing);
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}
		return mVoices.onKey(keyCode, event);
	}
	
		@Override
		protected void onPause() {
			super.onPause();
			mVoices.stop();
		}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
		if(mRing.onActivityResult(requestCode, resultCode, data))
			return;
		mDefaultRing.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void onClick(NormalItem item, int id) {
		mVoices.stop();
	}
	

}
