package com.nishay.tasktabs;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nishay.dialog.AddCategoryDialogFragment;
import com.nishay.dialog.AddCategoryDialogFragment.AddCategoryDialogListener;
import com.nishay.dialog.AddDialogFragment;
import com.nishay.dialog.AddDialogFragment.AddDialogListener;
import com.nishay.dialog.ImportRecipeDialogFragment;
import com.nishay.dialog.ImportRecipeDialogFragment.ImportRecipeDialogListener;
import com.nishay.dialog.RemoveTabDialogFragment;
import com.nishay.dialog.RemoveTabDialogFragment.RemoveTabDialogListener;
import com.nishay.urlparser.RecipeURLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends FragmentActivity implements TabListener, AddDialogListener, AddCategoryDialogListener, RemoveTabDialogListener, ImportRecipeDialogListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private ArrayList<String> tabs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences shared = getPreferences(Context.MODE_PRIVATE);
		Set<String> list = shared.getStringSet("list", null);
		if(list == null) {
			//set default tabs
			tabs = new ArrayList<String>();
			tabs.add("Other");
			tabs.add("Shopping");
		}
		else {
			tabs = new ArrayList<String>();
			tabs.addAll(list);
		}

		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), tabs);
		viewPager.setAdapter(mAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}

			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});

		//actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		//add tabs into the bar
		for(String name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(name).setTabListener(this));
		}

		//set last tab
		int lastTab = shared.getInt("lastTab", -1);
		if(lastTab != -1) {
			actionBar.setSelectedNavigationItem(lastTab);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();

		SortedSet<String> toSave = new TreeSet<String>();
		toSave.addAll(tabs);

		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putStringSet("list", toSave);
		editor.putInt("lastTab", actionBar.getSelectedNavigationIndex());
		editor.commit();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.add_category_menu: {
			startAddCategory();
			return true;
		}
		case R.id.remove_tab: {
			removeTab();
			return true;
		}
		default: return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {}


	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		viewPager.setCurrentItem(arg0.getPosition());

	}


	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {}


	@Override
	//add new task, loop through all tabs, find right one and break out then add to it
	public void onAddDialogYesClick(AddDialogFragment df, String taskDesc, String cat) {
		getTFFByCat(cat).addTask(taskDesc, cat);
	}


	@Override
	public void onAddDialogNoClick(AddDialogFragment df) {}

	public void startAddCategory() {
		//prompt for dialog, getresponse back by interface
		//test
		DialogFragment frag = new AddCategoryDialogFragment();
		frag.show(getSupportFragmentManager(), "AddCategoryDialogFragment");
	}

	@Override
	public void onAddCategoryDialogYesClick(AddCategoryDialogFragment df, String c) {
		String cat = format(c.trim());
		if(!tabs.contains(cat)) {
			tabs.add(cat);
			actionBar.addTab(actionBar.newTab().setText(cat).setTabListener(this));
			mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), tabs);
			viewPager.setAdapter(mAdapter);
			actionBar.setSelectedNavigationItem(0);
		}
		else {
			int index = tabs.indexOf(cat);
			actionBar.setSelectedNavigationItem(index);
		}
	}

	/*@Override
	public void onAddCategoryDialogNoClick(AddCategoryDialogFragment df) {}*/

	public void removeTab() {
		//start dialog with list
		DialogFragment frag = new RemoveTabDialogFragment();
		Bundle b = new Bundle();
		b.putStringArrayList("tabs", tabs);
		frag.setArguments(b);
		frag.show(getSupportFragmentManager(), "RemoveTabDialogFragment");

	}

	//Format string for category, capitalize first letter
	private String format(String s) {
		return (s.charAt(0) + "").toUpperCase(Locale.US) + s.substring(1).toLowerCase(Locale.US);
	}

	@Override
	public void onRemoveTabDialogClick(RemoveTabDialogFragment df, int position) {
		if(actionBar.getTabCount() > 1 && !actionBar.getTabAt(position).getText().equals("Shopping")) {
			tabs.remove(position);
			actionBar.removeTabAt(position);
			mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), tabs);

			viewPager.setAdapter(mAdapter);
			actionBar.setSelectedNavigationItem(0);
		}
		else if(actionBar.getTabAt(position).getText().equals("Shopping")) {
			Toast.makeText(getApplicationContext(), "Cannot remove Shopping tab.", Toast.LENGTH_LONG).show();
		}
		else {
			//cannot remove last tab, toast instead
			Toast.makeText(getApplicationContext(), "Cannot remove only tab.", Toast.LENGTH_LONG).show();
		}

		return;
	}

	/**
	 * Called when URL is attempting to be imported
	 * Returns a list of all items to be added to the "Shopping" cat
	 * 
	 * @param URL - String
	 */
	@Override
	public void onImportRecipeDialogYesClick(ImportRecipeDialogFragment df, String URL) {
		//call url parser, pass in this activity, so you can open a new dao
		RecipeURLParser parser = new RecipeURLParser();
		ArrayList<String> list = null;

		list = parser.parse(URL);

		//if list is null, show toast, malformed url
		if(list == null) {
			Toast.makeText(getApplicationContext(), "Failed to connect.  Check the URL.", Toast.LENGTH_LONG).show();
			return;
		}
		//if list is empty, or first item is "Bad Host", then toast that
		if(list.get(0).equals("unsupported")){
			Toast.makeText(getApplicationContext(), list.get(1) + " not supported.", Toast.LENGTH_LONG).show();
			return;
		}

		TaskFragmentFactory tff = getTFFByCat("Shopping");
		for(String s : list) {
			tff.addTask(s, "Shopping");
		}

	}

	@Override
	public void onImportRecipeDialogNoClick(ImportRecipeDialogFragment df) {}

	/**
	 * Returns the correct TaskFragmentFactory from the supportFragmentManager based on the category
	 * @param cat String
	 * @return TFF
	 */
	private TaskFragmentFactory getTFFByCat(String cat) {
		List <Fragment> factoryList = getSupportFragmentManager().getFragments();
		int i = 0;
		String category = "";
		for(; i < mAdapter.getCount(); i++) {
			try{
				category = ((TaskFragmentFactory) factoryList.get(i)).getCategory();
			} catch (ClassCastException e) {
				continue;
			} catch (NullPointerException e) {
				continue;
			}
			if(category.equals(cat)) {
				break;
			}
		}
		return ((TaskFragmentFactory) factoryList.get(i));
	}
}
