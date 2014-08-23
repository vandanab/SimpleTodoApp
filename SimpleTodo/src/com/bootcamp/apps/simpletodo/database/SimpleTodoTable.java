package com.bootcamp.apps.simpletodo.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/** Table class. */
public class SimpleTodoTable {
	// constants
	public static final String TABLE_NAME = "todo";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ITEM = "item";

	private static final String CREATE_TABLE = "create table "
			+ TABLE_NAME
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_ITEM + " text not null "
			+ ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SimpleTodoTable.class.getName(), "Upgrading database from version "
		        + oldVersion + " to " + newVersion
		        + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}