package com.nishay.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.nishay.tasktabs.R;

public class AddDialogFragment extends DialogFragment {

	private String cat;

	//interface listener setup
	public interface AddDialogListener {
		public void onAddDialogYesClick(AddDialogFragment df, String taskDesc, String cat);
		public void onAddDialogNoClick(AddDialogFragment df);
	}

	AddDialogListener adListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		//make sure activity implements listener
		try {
			adListener = (AddDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement AddDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)  {
		cat = getArguments().getString("cat");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_add, null);
		final EditText et = (EditText) view.findViewById(R.id.add_desc);

		builder.setView(view)
		.setPositiveButton("Add", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				adListener.onAddDialogYesClick(AddDialogFragment.this, et.getText().toString(), cat);
			}
		})

		.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				AddDialogFragment.this.getDialog().cancel();
			}
		});
		
		//create dialog from the builder
		final AlertDialog dialog = builder.create();

		//set keyboard to show on focus
		et.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }

			}
		});

		return dialog;

	}

}
