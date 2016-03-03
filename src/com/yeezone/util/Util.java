package com.yeezone.util;

import com.yeezone.aijiuyi.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

public class Util {

	public static String getMediaTitleByType(Context context, int type) {
		Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, type);
		String summary = context.getString(R.string.ringtone_unknown);

		if (ringtoneUri != null) {
			try {
				Cursor cursor = context.getContentResolver().query(ringtoneUri,
						new String[] { MediaStore.Audio.Media.TITLE }, null, null, null);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						summary = cursor.getString(0);
					}
					cursor.close();
				}
			} catch (SQLiteException sqle) {
				// Unknown title for the ringtone
			}
		}
		return summary;
	}
}
