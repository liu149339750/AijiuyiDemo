package com.yeezone.date;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.yeezone.aijiuyi.R;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.yeezone.aijiuyi.BaseSetting;
import com.yeezone.aijiuyi.R.string;
import com.yeezone.setting.item.NormalItem;
import com.yeezone.setting.item.SwitchItem;
import com.yeezone.setting.item.NormalItem.OnItemClickListener;
import com.yeezone.setting.item.SwitchItem.OnCheckChanageListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;

public class DateActivity extends BaseSetting implements OnItemClickListener,OnCheckChanageListener{
	
    private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";

	private SwitchItem mDateTimeAuto;
	private SwitchItem mTimeZoneAuto;
	private NormalItem mDateSet;
	private NormalItem mTimeSet;
	private NormalItem mTimeZoneSelect;
	private SwitchItem mTimeFormat;
	
	private Calendar mDummyDate;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDummyDate = Calendar.getInstance();
		
		mDateTimeAuto = new SwitchItem(this);
		mDateTimeAuto.setTitle(R.string.auto_datetime);
		mDateTimeAuto.setSummary(R.string.datetime_by_network);
		mDateTimeAuto.setChecked(getAutoState(Settings.Global.AUTO_TIME));
		mDateTimeAuto.setOnCheckChanageListener(this);
		
		mTimeZoneAuto = new SwitchItem(this);
		mTimeZoneAuto.setTitle(R.string.auto_timezone);
		mTimeZoneAuto.setSummary(R.string.timezone_by_network);
		mTimeZoneAuto.setChecked(getAutoState(Settings.Global.AUTO_TIME_ZONE));
		mTimeZoneAuto.setOnCheckChanageListener(this);
		
		mDateSet = new NormalItem(this);
		mDateSet.setTitle(R.string.set_date);
		mDateSet.setOnItemClickListener(this);
		mDateSet.setEnabled(!getAutoState(Settings.Global.AUTO_TIME));
		
		mTimeSet = new NormalItem(this);
		mTimeSet.setTitle(R.string.set_time);
		mTimeSet.setEnabled(!getAutoState(Settings.Global.AUTO_TIME));
		mTimeSet.setOnItemClickListener(this);
		
		mTimeZoneSelect = new NormalItem(this);
		mTimeZoneSelect.setTitle(R.string.select_timezone);
		mTimeZoneSelect.setOnItemClickListener(this);
		mTimeZoneSelect.setEnabled(!getAutoState(Settings.Global.AUTO_TIME_ZONE));
		
		mTimeFormat = new SwitchItem(this);
		mTimeFormat.setTitle(R.string.use_24hours);
		mTimeFormat.setChecked(is24Hour());
		mTimeFormat.setOnCheckChanageListener(this);
		
		
		addItem(mDateTimeAuto);
		addItem(mTimeZoneAuto);
		addItem(mDateSet);
		addItem(mTimeSet);
		addItem(mTimeZoneSelect);
		addItem(mTimeFormat);
		
		updateTimeAndDateDisplay(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mTimeZoneSelect.setSummary(getTimeZoneText(Calendar.getInstance().getTimeZone()));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	public void onClick(NormalItem item, int id) {
		if(item == mTimeZoneSelect) {
			Intent intent = new Intent();
			intent.setClass(this, TimeZonePickActivity.class);
			startActivity(intent);
		} else if(item == mDateSet) {
			Calendar calendar = Calendar.getInstance();
			DatePickerDialog date = DatePickerDialog.newInstance(new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
					setDate(year, month, day);
					updateTimeAndDateDisplay(DateActivity.this);
				}
			},calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
			date.show(getFragmentManager(),"data");
		} else if(item == mTimeSet) {
			Calendar calendar = Calendar.getInstance();
			TimePickerDialog time = TimePickerDialog.newInstance(new OnTimeSetListener() {
				
				@Override
				public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
					setTime(hourOfDay, minute);
					updateTimeAndDateDisplay(DateActivity.this);
				}
			},calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24Hour());
			time.show(getFragmentManager(), "time");
		}
	}
	
    private boolean getAutoState(String name) {
        try {
            return Settings.Global.getInt(getContentResolver(), name) > 0;
        } catch (SettingNotFoundException snfe) {
            return false;
        }
    }
	
    private boolean is24Hour() {
        return DateFormat.is24HourFormat(this);
    }
    
    private void set24Hour(boolean is24Hour) {
        Settings.System.putString(getContentResolver(),
                Settings.System.TIME_12_24,
                is24Hour? HOURS_24 : HOURS_12);
    }
	
    private String getTimeZoneText(TimeZone tz) {

        Date now = new Date();
        return formatOffset(new StringBuilder(), tz, now).
            append(", ").
            append(tz.getDisplayName(tz.inDaylightTime(now), TimeZone.LONG)).toString();
    }
    
    private static StringBuilder formatOffset(StringBuilder sb, TimeZone tz, Date d) {
        int off = tz.getOffset(d.getTime()) / 1000 / 60;

        sb.append("GMT");
        if (off < 0) {
            sb.append('-');
            off = -off;
        } else {
            sb.append('+');
        }

        int hours = off / 60;
        int minutes = off % 60;

        sb.append((char) ('0' + hours / 10));
        sb.append((char) ('0' + hours % 10));

        sb.append(':');

        sb.append((char) ('0' + minutes / 10));
        sb.append((char) ('0' + minutes % 10));

        return sb;
    }
    
    public void updateTimeAndDateDisplay(Context context) {
        java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(context);
        final Calendar now = Calendar.getInstance();
        mDummyDate.setTimeZone(now.getTimeZone());
        mDummyDate.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH), 13, 0, 0); 
        Date dummyDate = mDummyDate.getTime();
        mTimeSet.setSummary(DateFormat.getTimeFormat(this).format(now.getTime()));
        mTimeZoneSelect.setSummary(getTimeZoneText(now.getTimeZone()));
        mDateSet.setSummary(shortDateFormat.format(now.getTime()));
        mTimeFormat.setSummary(DateFormat.getTimeFormat(this).format(dummyDate));
    }
    
    private void timeUpdated() {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        sendBroadcast(timeChanged);
    }
    
    private void setDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
    }
    
    private void setTime(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
    }

	@Override
	public void onCheckedChanged(SwitchItem item, boolean isChecked) {
		if(item == mTimeFormat) {
			set24Hour(isChecked);
			updateTimeAndDateDisplay(this);
			timeUpdated();
		}else if(item == mDateTimeAuto) {
            Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME,
            		isChecked ? 1 : 0);
            mDateSet.setEnabled(!isChecked);
            mTimeSet.setEnabled(!isChecked);
            updateTimeAndDateDisplay(this);
		}else if(item == mTimeZoneAuto) {
            Settings.Global.putInt(
                    getContentResolver(), Settings.Global.AUTO_TIME_ZONE, isChecked ? 1 : 0);
			mTimeZoneSelect.setEnabled(!isChecked);
            updateTimeAndDateDisplay(this);
		}
	}
}
