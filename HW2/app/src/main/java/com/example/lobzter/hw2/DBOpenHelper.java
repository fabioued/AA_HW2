package com.example.lobzter.hw2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	
	public DBOpenHelper(Context context) {
		super(context, "note.db", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " +
				NoteDB.NOTETABLE + "(title, latitude, longitude);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
	}

}
