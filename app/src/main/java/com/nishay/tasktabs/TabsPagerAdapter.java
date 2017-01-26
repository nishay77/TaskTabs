package com.nishay.tasktabs;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {
	
	private int count;
	private List <String> tabs;
	
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public TabsPagerAdapter(FragmentManager fm, List<String> tabs) {
		super(fm);
		this.tabs = tabs;
		count = tabs.size();
	}

	@Override
	public Fragment getItem(int index) {
		Bundle b = new Bundle();
		b.putString("category", tabs.get(index));
		TaskFragmentFactory tff = new TaskFragmentFactory();
		tff.setArguments(b);
		return tff;
	}
	
	@Override
	public int getItemPosition(Object obj) {
		return PagerAdapter.POSITION_NONE;
	}
	
	public void setCount(int c) {
		count = c;
	}

	@Override
	public int getCount() {
		return count;
	}

}
