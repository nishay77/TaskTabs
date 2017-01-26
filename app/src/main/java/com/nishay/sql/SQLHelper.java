package com.nishay.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME="tasks_table";
	public static final String COLUMN1 = "description";
	public static final String COLUMN2 = "category";
	private static final String DBNAME = "tasks_table.db";
	public static final String COLUMN_ID = "_id";
	private static final int DBVER = 1;

	public SQLHelper(Context context) {
		super(context, DBNAME, null, DBVER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, " 
					 + COLUMN1 + " text not null, " + COLUMN2 + ");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);

	}


}
