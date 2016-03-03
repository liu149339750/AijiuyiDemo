package com.yeezone.display;

import com.yeezone.aijiuyi.R;
import com.yeezone.setting.item.Item;

import android.content.Context;
import android.widget.RadioButton;

public class FontItem extends Item{

	public FontItem(Context context) {
		super(context, R.layout.setting_item_font);
	}

	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.font_size);
	}

}
