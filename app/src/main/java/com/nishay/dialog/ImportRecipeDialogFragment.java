package com.nishay.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nishay.tasktabs.R;
import com.nishay.urlparser.RecipeURLParser;

import org.w3c.dom.Text;

public class ImportRecipeDialogFragment extends DialogFragment {

	private String URL;

	//interface listener setup
	public interface ImportRecipeDialogListener {
		public void onImportRecipeDialogYesClick(ImportRecipeDialogFragment df, String URL);
		public void onImportRecipeDialogNoClick(ImportRecipeDialogFragment df);
	}

	ImportRecipeDialogListener irListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		//make sure activity implements listener
		try {
			irListener = (ImportRecipeDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement ImportRecipeDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)  {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_import_recipe, null);
		TextView description = (TextView) view.findViewById(R.id.import_recipe_description);
		StringBuilder sb = new StringBuilder();
		sb.append("Enter the URL for a recipe from your choice of the following sites:\n");
		String[] list = RecipeURLParser.sitesArray;
		for(String site : list) {
			sb.append("\n\t\t");
			sb.append(site);
		}
		description.setText(sb.toString());

		final EditText et = (EditText) view.findViewById(R.id.import_recipe_URL);
		
		//get clipboard text
		ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		if(clipboard.hasPrimaryClip()) {
			ClipData clip = clipboard.getPrimaryClip();
			et.setText(clip.getItemAt(0).getText());
		}

		builder.setView(view)
		.setPositiveButton("Import", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				irListener.onImportRecipeDialogYesClick(ImportRecipeDialogFragment.this, et.getText().toString());
			}
		})

		.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ImportRecipeDialogFragment.this.getDialog().cancel();
			}
		});
		
		//create dialog from the builder
		final AlertDialog dialog = builder.create();

		return dialog;

	}

}
