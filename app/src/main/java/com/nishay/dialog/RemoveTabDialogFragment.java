package com.nishay.dialog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RemoveTabDialogFragment extends DialogFragment {
	
	//interface listener setup
		public interface RemoveTabDialogListener {
			public void onRemoveTabDialogClick(RemoveTabDialogFragment df, int position);
		}

		RemoveTabDialogListener adListener;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

			//make sure activity implements listener
			try {
				adListener = (RemoveTabDialogListener) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString() + "must implement RemoveTabDialogListener");
			}
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			ArrayList<String> tabs = getArguments().getStringArrayList("tabs");
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Choose a tab to remove...")
			.setItems(tabs.toArray(new String[tabs.size()]), new OnClickListener () {

				@Override
				public void onClick(DialogInterface dialog, int position) {
					adListener.onRemoveTabDialogClick(RemoveTabDialogFragment.this, position);
				}
				
			});
			
			return builder.create();
		}
	
}
