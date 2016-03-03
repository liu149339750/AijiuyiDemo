package com.yeezone.sound;

import com.yeezone.aijiuyi.R;
import com.yeezone.setting.item.ItemGroup;
import com.yeezone.setting.item.SeekItem;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.System;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SoundSetting extends ItemGroup{
	
	private SeekBarVolumizer [] mSeekBarVolumizer;
	private static final int[] SEEKBAR_TYPE = new int[] {
            AudioManager.STREAM_MUSIC,
            AudioManager.STREAM_ALARM,
            AudioManager.STREAM_RING
        };

    public SoundSetting(Context context) {
		super(context);
	}
    
    @Override
    protected void initView() {
    	super.initView();
    	mSeekBarVolumizer = new SeekBarVolumizer[SEEKBAR_TYPE.length];
    	
    	SeekItem music = new SeekItem(mContext);
    	music.setTitleIcon(R.string.media_sound, R.drawable.voicemeiti);
    	mSeekBarVolumizer[0] = new SeekBarVolumizer(mContext, music.getSeekBar(), SEEKBAR_TYPE[0],getMediaVolumeUri(mContext));
    	
    	SeekItem alarm = new SeekItem(mContext);
    	alarm.setTitleIcon(R.string.alarm_volume, R.drawable.voice_alarm);
    	mSeekBarVolumizer[1] = new SeekBarVolumizer(mContext, alarm.getSeekBar(), SEEKBAR_TYPE[1]);
    	
    	SeekItem ring = new SeekItem(mContext);
    	ring.setTitleIcon(R.string.ring_volume, R.drawable.voice_ling);
    	mSeekBarVolumizer[2] = new SeekBarVolumizer(mContext, ring.getSeekBar(), SEEKBAR_TYPE[2]);
    	
    	addItem(music);
    	addItem(alarm);
    	addItem(ring);
    }
    
    private Uri getMediaVolumeUri(Context context) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.getPackageName()
                + "/" + R.raw.media_volume);
    }
    
    public void stop(){
        for (SeekBarVolumizer vol : mSeekBarVolumizer) {
            if (vol != null ) vol.stopSample();
        }
    }
    
    protected void onSampleStarting(SeekBarVolumizer volumizer) {
        for (SeekBarVolumizer vol : mSeekBarVolumizer) {
            if (vol != null && vol != volumizer) vol.stopSample();
        }
    }
    
    public boolean onKey(int keyCode, KeyEvent event) {
        boolean isdown = (event.getAction() == KeyEvent.ACTION_DOWN);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                return true;
            default:
                return false;
        }
    }
    
public class SeekBarVolumizer implements OnSeekBarChangeListener, Runnable {

        private Context mContext;
        private Handler mHandler = new Handler();

        private AudioManager mAudioManager;
        private int mStreamType;
        private int mOriginalStreamVolume;
        private int mOriginalRingMode;
        private Ringtone mRingtone;

        private int mLastProgress = -1;
        private SeekBar mSeekBar;
        private int mVolumeBeforeMute = -1;

        private ContentObserver mVolumeObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (mSeekBar != null && mAudioManager != null) {
                    int volume = mAudioManager.getStreamVolume(mStreamType);
                    mSeekBar.setProgress(volume);
                }
            }
        };

        public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType) {
            this(context, seekBar, streamType, null);
        }

        public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType, Uri defaultUri) {
            mContext = context;
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mStreamType = streamType;
            mSeekBar = seekBar;

            mOriginalRingMode = mAudioManager.getRingerMode();
            initSeekBar(seekBar, defaultUri);
        }

        private void initSeekBar(SeekBar seekBar, Uri defaultUri) {
            seekBar.setMax(mAudioManager.getStreamMaxVolume(mStreamType));
            mOriginalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
            seekBar.setProgress(mOriginalStreamVolume);
            seekBar.setOnSeekBarChangeListener(this);

            mContext.getContentResolver().registerContentObserver(
                    System.getUriFor(System.VOLUME_SETTINGS[mStreamType]),
                    false, mVolumeObserver);

            if (defaultUri == null) {
                if (mStreamType == AudioManager.STREAM_RING) {
                    defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
                } else if (mStreamType == AudioManager.STREAM_NOTIFICATION) {
                    defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                } else {
                    defaultUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                }
            }

            mRingtone = RingtoneManager.getRingtone(mContext, defaultUri);

            if (mRingtone != null) {
                mRingtone.setStreamType(mStreamType);
            }
        }

        public void stop() {
            stopSample();
            mContext.getContentResolver().unregisterContentObserver(mVolumeObserver);
            mSeekBar.setOnSeekBarChangeListener(null);
        }

        public void revertVolume() {
            if (mOriginalStreamVolume == 0
                    && (mStreamType == AudioManager.STREAM_RING
                    || mStreamType == AudioManager.STREAM_NOTIFICATION)
                    && mOriginalRingMode != AudioManager.RINGER_MODE_VIBRATE) {
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else {
                if ( mLastProgress == mAudioManager.getStreamVolume(mStreamType) &&
                        mLastProgress != mOriginalStreamVolume) {
                    mAudioManager.setStreamVolume(mStreamType, mOriginalStreamVolume, 0);
                }
            }
        }

        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromTouch) {
            if (!fromTouch) {
                return;
            }

            postSetVolume(progress);
        }

        void postSetVolume(int progress) {
            mLastProgress = progress;
            mHandler.removeCallbacks(this);
            mHandler.post(this);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!isSamplePlaying()) {
                startSample();
            }
        }

        public void run() {
            if (mLastProgress == 0
                    && (mStreamType == AudioManager.STREAM_RING
                    || mStreamType == AudioManager.STREAM_NOTIFICATION)
                    && mOriginalRingMode != AudioManager.RINGER_MODE_VIBRATE) {
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else {
                mAudioManager.setStreamVolume(mStreamType, mLastProgress, 0);
            }
        }

        public boolean isSamplePlaying() {
            return mRingtone != null && mRingtone.isPlaying();
        }

        public void startSample() {
            onSampleStarting(this);
            if (mRingtone != null) {
                mRingtone.play();
            }
        }

        public void stopSample() {
            if (mRingtone != null) {
                mRingtone.stop();
            }
        }

        public SeekBar getSeekBar() {
            return mSeekBar;
        }

        public void changeVolumeBy(int amount) {
            mSeekBar.incrementProgressBy(amount);
            if (!isSamplePlaying()) {
                startSample();
            }
            postSetVolume(mSeekBar.getProgress());
            mVolumeBeforeMute = -1;
        }

        public void muteVolume() {
            if (mVolumeBeforeMute != -1) {
                mSeekBar.setProgress(mVolumeBeforeMute);
                startSample();
                postSetVolume(mVolumeBeforeMute);
                mVolumeBeforeMute = -1;
            } else {
                mVolumeBeforeMute = mSeekBar.getProgress();
                mSeekBar.setProgress(0);
                stopSample();
                postSetVolume(0);
            }
        }

    }

}
