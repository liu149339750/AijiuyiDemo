package com.yeezone.setting.item;

import java.util.ArrayList;
import java.util.List;

import com.yeezone.aijiuyi.R;

import android.content.Context;
import android.view.ViewGroup;

public class ItemGroup extends Item{
	
	private List<Item> mItems;
	private ViewGroup mGroup;

	public ItemGroup(Context context) {
		super(context, R.layout.group);
	}
	
	@Override
	protected void init() {
		mItems = new ArrayList<Item>();
	}
	@Override
	protected void initView() {
		super.initView();
		mGroup = (ViewGroup) findViewById(R.id.container);
	}
	
	public void addItem(Item item) {
		mItems.add(item);
		mGroup.addView(item.getView());
	}
	
	public void removeItem(Item item) {
		mItems.remove(item);
		mGroup.removeView(item.getView());
	}

}
