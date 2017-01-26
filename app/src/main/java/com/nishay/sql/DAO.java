package com.nishay.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nishay.tasktabs.Task;

public class DAO {
	
	private SQLiteDatabase db;
	private SQLHelper helper;
	private String[] allcolumns = { SQLHelper.COLUMN_ID, SQLHelper.COLUMN1, SQLHelper.COLUMN2 };
	
	public DAO(Context c) {
		helper = new SQLHelper(c);
	}
	
	public void open() {
		db = helper.getWritableDatabase();
	}
	
	public void close() {
		helper.close();
	}
	
	public Task add(String desc, String cat) {
		ContentValues values = new ContentValues();
		values.put(SQLHelper.COLUMN1, desc);
		values.put(SQLHelper.COLUMN2, cat);
		long newID = db.insert(SQLHelper.TABLE_NAME, null, values);
		Cursor newCursor = db.query(SQLHelper.TABLE_NAME, allcolumns, SQLHelper.COLUMN_ID + " = " + newID,
									null, null, null, null);
		newCursor.moveToFirst();
		Task newTask = getTaskFromCursor(newCursor);
		newCursor.close();
		return newTask;
	}
	
	public void delete(Task t) {
		long id = t.getId();
		db.delete(SQLHelper.TABLE_NAME, SQLHelper.COLUMN_ID + " = " + id, null);
	}
	
	public List<Task> getAllTasks(String cat) {
		List<Task> allTasks = new ArrayList<Task>();
		Cursor newCursor = db.query(SQLHelper.TABLE_NAME, allcolumns, SQLHelper.COLUMN2 + " = '" + cat + "'",
				null, null, null, null);
		newCursor.moveToFirst();
		while(!newCursor.isAfterLast()) {
			Task t = getTaskFromCursor(newCursor);
			allTasks.add(t);
			newCursor.moveToNext();
		}
		newCursor.close();
		return allTasks;
	}
	
	private Task getTaskFromCursor(Cursor c) {
		Task t = new Task();
		t.setId(c.getLong(0));
		t.setDescription(c.getString(1));
		t.setCategory(c.getString(2));
		return t;
	}

}
