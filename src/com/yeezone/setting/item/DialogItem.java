package com.yeezone.setting.item;

import com.yeezone.aijiuyi.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogItem extends NormalItem implements DialogInterface.OnDismissListener,DialogInterface.OnClickListener{

	private AlertDialog.Builder mBuilder;
	private CharSequence mDialogTitle;
	private Drawable mDialogIcon;
    private CharSequence mPositiveButtonText;
    private CharSequence mNegativeButtonText;
    private int mDialogLayoutResId;
    private CharSequence mDialogMessage;
    private Dialog mDialog;
    private TextView mMessage;
    
    private int mWhichButtonClicked;
    
	public DialogItem(Context context) {
		super(context);
	}
	
	public void setMessage(CharSequence msg) {
		if(msg != null)
			mMessage.setText(msg);
	}
	
	@Override
	protected void initView() {
		super.initView();
		mMessage = (TextView) findViewById(R.id.message);
	}
	
	public void setDialogTitle(CharSequence title) {
		mDialogTitle = title;
	}
	
	public void setDialogIcon(Drawable d) {
		mDialogIcon = d;
	}
	
	public void setPositiveText(CharSequence t){
		mPositiveButtonText = t;
	}
	
	public void setNegativeText(CharSequence t){
		mNegativeButtonText = t;
	}
	
	public void setDialogMessage(CharSequence message) {
		mDialogMessage = message;
	}
	
    protected void showDialog() {
        Context context = mContext;
        mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
        mBuilder = new AlertDialog.Builder(context)
            .setTitle(mDialogTitle)
            .setIcon(mDialogIcon)
            .setPositiveButton(mPositiveButtonText, this)
            .setNegativeButton(mNegativeButtonText, this);

        View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            mBuilder.setView(contentView);
        } else {
            mBuilder.setMessage(mDialogMessage);
        }
        
        onPrepareDialogBuilder(mBuilder);
        
        final Dialog dialog = mDialog = mBuilder.create();
        if (needInputMethod()) {
            requestInputMethod(dialog);
        }
        dialog.setOnDismissListener(this);
        dialog.show();
    }
    
    public void setDialogLayoutResource(int dialogLayoutResId) {
        mDialogLayoutResId = dialogLayoutResId;
    }
    
    public int getDialogLayoutResource() {
        return mDialogLayoutResId;
    }
    
    public CharSequence getDialogMessage() {
        return mDialogMessage;
    }
    
    protected void onBindDialogView(View view) {
        View dialogMessageView = view.findViewById(android.R.id.message);
        
        if (dialogMessageView != null) {
            final CharSequence message = getDialogMessage();
            int newVisibility = View.GONE;
            
            if (!TextUtils.isEmpty(message)) {
                if (dialogMessageView instanceof TextView) {
                    ((TextView) dialogMessageView).setText(message);
                }
                
                newVisibility = View.VISIBLE;
            }
            
            if (dialogMessageView.getVisibility() != newVisibility) {
                dialogMessageView.setVisibility(newVisibility);
            }
        }
    }
    
    protected View onCreateDialogView() {
        if (mDialogLayoutResId == 0) {
            return null;
        }
        
        LayoutInflater inflater = LayoutInflater.from(mBuilder.getContext());
        return inflater.inflate(mDialogLayoutResId, null);
    }

	protected void onPrepareDialogBuilder(Builder mBuilder2) {
	}
	
    protected boolean needInputMethod() {
        return false;
    }

    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

	@Override
	public void onDismiss(DialogInterface dialog) {
		mDialog = null;
		onDialogClosed(mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE);
	}
	
    public Dialog getDialog() {
        return mDialog;
    }
    
    protected void onDialogClosed(boolean positiveResult) {
    }

	@Override
	public void onClick(View v) {
        if (mDialog != null && mDialog.isShowing()) return;
        System.out.println("onClick");
        showDialog();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		mWhichButtonClicked = which;
	}

}
