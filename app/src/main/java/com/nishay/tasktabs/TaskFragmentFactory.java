package com.nishay.tasktabs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nishay.dialog.AddDialogFragment;
import com.nishay.dialog.ImportRecipeDialogFragment;
import com.nishay.sql.DAO;
import com.swipedismiss.SwipeDismissListViewTouchListener;

import java.util.ArrayList;
import java.util.List;

public class TaskFragmentFactory extends Fragment {

	private DAO dao = null;
	private ArrayAdapter<Task> adapter;
	private String cat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		cat = getArguments().getString("category");
		//set text for add button
		String buttonText = null;
		if(cat.equals("Other")) {
			buttonText = "Add Generic Task";
		}
		else if (cat.equals("Shopping")) {
			buttonText = "Add Shopping List Item";
		}
		else {
			buttonText = "Add " + cat + " Task";
		}

		LinearLayout table = (LinearLayout) inflater.inflate(R.layout.fragment_all_tasks, container, false);
		LinearLayout row = (LinearLayout) table.findViewById(R.id.button_container);

		//setup db
		dao = new DAO(getActivity());
		dao.open();
		List<Task> allTasks = dao.getAllTasks(cat);

		//show elements in the ListView
		adapter = new ArrayAdapter<Task>(getActivity(), android.R.layout.simple_list_item_1, allTasks);
		final ListView list = (ListView) table.findViewById(android.R.id.list);

		ViewGroup buttonList = (ViewGroup) inflater.inflate(R.layout.button, container, false);

		ImageView addButton = (ImageView) buttonList.findViewById(R.id.button_image);
		ImageView importButton = (ImageView) buttonList.findViewById(R.id.button_import_icon);
		ImageView clearButton = (ImageView) buttonList.findViewById(R.id.button_clear);
		TextView addButtonText = (TextView) buttonList.findViewById(R.id.button_add_text);
		TextView importButtonText = (TextView) buttonList.findViewById(R.id.button_import_text);
		TextView clearButtonText = (TextView) buttonList.findViewById(R.id.button_clear_text);

		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addDialogPopup(cat);
			}
		});
		addButtonText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addDialogPopup(cat);
			}
		});


		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//delete all from this category
				List<Task> toDelete = dao.getAllTasks(cat);
				for(Task t : toDelete) {
					dao.delete(t);
				}
				list.setAdapter(new ArrayAdapter<Task>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<Task>()));
			}
		});

		clearButtonText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//delete all from this category
				List<Task> toDelete = dao.getAllTasks(cat);
				for(Task t : toDelete) {
					dao.delete(t);
				}
				list.setAdapter(new ArrayAdapter<Task>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<Task>()));
			}
		});

		if(cat.equals("Shopping")) {
			importButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					importDialogPopup();
				}
			});
			importButtonText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					importDialogPopup();
				}
			});
		}
		else {
			buttonList.removeView(importButton);
			buttonList.removeView(importButtonText);
		}

		row.addView(buttonList);


		//testing dynamic
		/*list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
				TextView tv = (TextView) v;
				boolean strikethrough = (tv.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG;
				if(strikethrough) {
					//Strikethrough is on -- turn off
					tv.setPaintFlags(tv.getPaintFlags() & ~(Paint.STRIKE_THRU_TEXT_FLAG));
					tv.setTextColor(Color.BLACK);
				}
				else {
					//Strikethrough is off -- turn on
					tv.setPaintFlags(tv.getPaintFlags() |(Paint.STRIKE_THRU_TEXT_FLAG));
					tv.setTextColor(Color.GRAY);
				}
			}
		});*/

		//setup dismiss touchviewlistener
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(list, new SwipeDismissListViewTouchListener.DismissCallbacks() {

			@Override
			public void onDismiss(ListView listView, int[] reverseSortedPositions) {
				for(int position : reverseSortedPositions) {
					Task t = adapter.getItem(position);
					delete(t);
				}

			}

			@Override
			public boolean canDismiss(int position) {
				return true;
			}
		});

		list.setOnTouchListener(touchListener);

		list.setOnScrollListener(touchListener.makeScrollListener());


		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> listView, View v, int position, long id) {
				final Task t = adapter.getItem(position);
				delete(t);
				//((TextView) v).setPaintFlags(((TextView) v).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				return true;
			}
		});
		list.setAdapter(adapter);

		return table; //return view
	}

	@Override
	public void onStop() {
		super.onStop();
		dao.close();
	}

	@Override
	public void onResume() {
		super.onResume();
		dao.open();
	}

	/**
	 * Creates a new AddDialogFragment, sets the cat to the bundle and passes it via supportfragmentmanager
	 * @param cat -- String
	 */
	private void addDialogPopup(String cat) {
		DialogFragment frag = new AddDialogFragment();
		Bundle b = new Bundle();
		b.putString("cat", cat);
		frag.setArguments(b);
		frag.show(getActivity().getSupportFragmentManager(), "AddDialogFragment");
	}

	/**
	 * Available only for cat=Shopping
	 */
	private void importDialogPopup() {
		DialogFragment frag = new ImportRecipeDialogFragment();
		frag.show(getActivity().getSupportFragmentManager(), "ImportRecipeDialogFragment");
	}

	public void addTask(String desc, String cat) {
		adapter.add(dao.add(desc, cat));
	}

	public String getCategory() {
		return cat;
	}

	//for deleting from list and db
	private void delete(Task t) {
		dao.delete(t);
		adapter.remove(t);
		adapter.notifyDataSetChanged();
	}

}
