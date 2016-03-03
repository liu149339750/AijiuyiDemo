package com.yeezone.sound;

import com.yeezone.setting.item.NormalItem;
import com.yeezone.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;

public class RingItem extends NormalItem{

	private int mType;
	public RingItem(Context context,int type) {
		super(context);
		mType = type;
		setSummary(Util.getMediaTitleByType(context, type));
	}
	
	@Override
	protected void initView() {
		super.initView();
		setHasNIcon(true);
	}
	
	public void setType(int type ){
		mType = type;
		setSummary(Util.getMediaTitleByType(mContext, type));
	}
	
	public int getType() {
		return mType;
	}
	
    protected void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {

        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
        		RingtoneManager.getActualDefaultRingtoneUri(mContext, mType));
        
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);

        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, mType);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getTitle());
    }
    
    @Override
    public void onClick(View v) {
    	super.onClick(v);
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        onPrepareRingtonePickerIntent(intent);
        ((Activity)mContext).startActivityForResult(intent, mType);;
    }
    
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode != mType)
    		return false;
    	
        if (data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            onSaveRingtone(uri);
        }
    	return true;
    }
    
    protected void onSaveRingtone(Uri ringtoneUri) {
	  	RingtoneManager.setActualDefaultRingtoneUri(mContext, mType, ringtoneUri);
	  	setSummary(Util.getMediaTitleByType(mContext, mType));
  }

}
