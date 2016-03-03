package com.yeezone.setting.item;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class ListItem extends DialogItem {
	private CharSequence[] mEntries;
	private CharSequence[] mEntryValues;
	private String mValue;
	private int mClickedDialogEntryIndex;
    private Listener mListener;

	public ListItem(Context context) {
		super(context);
	}
	
	@Override
	protected void initView() {
		super.initView();
		setHasNIcon(true);
		setHasSelectInfo(true);
		setHasSummary(false);
	}

	public void setListener(Listener listener) {
		mListener = listener;
	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		if (mEntries == null || mEntryValues == null) {
			throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
		}

		mClickedDialogEntryIndex = getValueIndex();
		builder.setSingleChoiceItems(mEntries, mClickedDialogEntryIndex, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mClickedDialogEntryIndex = which;

				ListItem.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
				dialog.dismiss();
			}
		});

		builder.setPositiveButton(null, null);
	}

	public void setValue(String value) {
		mValue = value;
		setMessage(getEntry());
	}
	
    public void setValueIndex(int index) {
        if (mEntryValues != null) {
            setValue(mEntryValues[index].toString());
        }
    }
    
    public CharSequence getEntry() {
        int index = getValueIndex();
        return index >= 0 && mEntries != null ? mEntries[index] : null;
    }

	private int getValueIndex() {
		return findIndexOfValue(mValue);
	}

	public int findIndexOfValue(String value) {
		if (value != null && mEntryValues != null) {
			for (int i = mEntryValues.length - 1; i >= 0; i--) {
				if (mEntryValues[i].equals(value)) {
					return i;
				}
			}
		}
		return -1;
	}

    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        
        if (positiveResult && mClickedDialogEntryIndex >= 0 && mEntryValues != null) {
            String value = mEntryValues[mClickedDialogEntryIndex].toString();
            setValue(value);
            if(mListener != null)
            	mListener.onValueChange(this, value);
        }
    }
    
	public void setEntries(CharSequence[] entries) {
		mEntries = entries;
	}

	public void setEntries(int entriesResId) {
		setEntries(mContext.getResources().getTextArray(entriesResId));
		setMessage(mEntries.length>0?mEntries[0]:"");
	}

	public CharSequence[] getEntries() {
		return mEntries;
	}

	public void setEntryValues(CharSequence[] entryValues) {
		mEntryValues = entryValues;
	}

	public void setEntryValues(int entryValuesResId) {
		setEntryValues(mContext.getResources().getTextArray(entryValuesResId));
	}

	public CharSequence[] getEntryValues() {
		return mEntryValues;
	}
	
	public interface Listener{
		public void onValueChange(ListItem item,Object v);
	}
}
