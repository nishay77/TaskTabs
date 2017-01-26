package com.nishay.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.nishay.tasktabs.R;

public class AddCategoryDialogFragment extends DialogFragment {

	//interface listener setup
	public interface AddCategoryDialogListener {
		public void onAddCategoryDialogYesClick(AddCategoryDialogFragment df, String cat);
		//public void onAddCategoryDialogNoClick(AddCategoryDialogFragment df);
	}

	AddCategoryDialogListener adListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		//make sure activity implements listener
		try {
			adListener = (AddCategoryDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement AddCategoryDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_category_add, null);
		builder.setView(view)
		.setPositiveButton("Add", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText et = (EditText) view.findViewById(R.id.add_cat);
				adListener.onAddCategoryDialogYesClick(AddCategoryDialogFragment.this, et.getText().toString());
			}
		})

		.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				AddCategoryDialogFragment.this.getDialog().cancel();
			}
		});
		return builder.create();
	}

}
