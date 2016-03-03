package com.yeezone.display;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import com.yeezone.aijiuyi.BaseSetting;
import com.yeezone.aijiuyi.R;
import com.yeezone.aijiuyi.R.array;
import com.yeezone.aijiuyi.R.string;
import com.yeezone.setting.item.Item;
import com.yeezone.setting.item.ListItem;
import com.yeezone.setting.item.SeekItem;
import com.yeezone.setting.item.SwitchItem;
import com.yeezone.setting.item.ListItem.Listener;
import com.yeezone.setting.item.SeekItem.OnSeekChanageListener;
import com.yeezone.setting.item.SwitchItem.OnCheckChanageListener;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DisplayActivity extends BaseSetting implements OnSeekChanageListener, OnCheckChanageListener {

	private SeekItem mLight;
	private SwitchItem mSwitchLight;
	private ListItem mSleep;
	private static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;

	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLight = new SeekItem(this);
		mLight.useSimpleSeek(true);
		mLight.setTitle(R.string.brightness);
		mLight.setMax(255);
		mLight.setProgress(getBrightness());
		mLight.setOnSeekChanageListener(this);
		
		mSwitchLight = new SwitchItem(this);
		mSwitchLight.setHasSwitch(true);
		mSwitchLight.setTitle(R.string.auto_brightness);
		mSwitchLight.setChecked(getScreenMode() == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? true : false);
		mSwitchLight.setOnCheckChanageListener(this);
		mSwitchLight.setSummary(R.string.auto_brightness_by_environment);
		mLight.setEnabled(!mSwitchLight.isCheck());

		mSleep = new ListItem(this);
		mSleep.setTitle(R.string.sleep);
		mSleep.setDialogTitle("title");
//		mSleep.setDialogMessage("ddd");
		mSleep.setEntries(R.array.dream_timeout_entries);
		mSleep.setEntryValues(R.array.dream_timeout_values);
		mSleep.setPositiveText("td");
		mSleep.setListener(new Listener() {
			
			@Override
			public void onValueChange(ListItem item, Object v) {
				int value = Integer.parseInt(v.toString());
                Settings.System.putInt(getContentResolver(), SCREEN_OFF_TIMEOUT, value);
                updateTimeoutPreferenceDescription(value);
			}
		});
        final long currentTimeout = Settings.System.getLong(getContentResolver(), SCREEN_OFF_TIMEOUT,
                FALLBACK_SCREEN_TIMEOUT_VALUE);
        updateTimeoutPreferenceDescription(currentTimeout);

		addItem(mLight);
		addItem(mSwitchLight);
		addItem(mSleep);
	}

	@Override
	public void onProgress(SeekItem item, int progress) {
		if (item == mLight)
			setScreenBrightness(progress);
	}
	
    private void updateTimeoutPreferenceDescription(long currentTimeout) {
        ListItem item = mSleep;
        String summary;
        if (currentTimeout < 0) {
            // Unsupported value
            summary = "";
        } else {
            final CharSequence[] entries = item.getEntries();
            final CharSequence[] values = item.getEntryValues();
            if (entries == null || entries.length == 0) {
                summary = "";
            } else {
                int best = 0;
                for (int i = 0; i < values.length; i++) {
                    long timeout = Long.parseLong(values[i].toString());
                    if (currentTimeout >= timeout) {
                        best = i;
                    }
                }
                summary = getString(R.string.screen_timeout_summary,
                        entries[best]);
            }
        }
        item.setMessage(summary);
    }

	private int getBrightness() {
		int screenBrightness = 255;
		try {
			screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception localException) {

		}
		return screenBrightness;
	}

	private void setScreenBrightness(int paramInt) {
		try {
			Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	private int getScreenMode() {
		int screenMode = 0;
		try {
			screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (Exception localException) {

		}
		return screenMode;
	}

	private void setScreenMode(int paramInt) {
		try {
			Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	@Override
	public void onCheckedChanged(SwitchItem item, boolean isChecked) {
		if (isChecked) {
			setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		} else {
			setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
		mLight.setEnabled(!isChecked);
	}
}
