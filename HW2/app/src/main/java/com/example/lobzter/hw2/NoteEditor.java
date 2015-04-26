package com.example.lobzter.hw2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class NoteEditor extends Activity {
	
	EditText et_title, et_latitude, et_longitude;
	ArrayList<String> titlelist;
	SQLiteDatabase db;
	int notepos;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noteeditor);
		et_title = (EditText)findViewById(R.id.et_title);
        et_latitude = (EditText)findViewById(R.id.et_latitude);
        et_longitude = (EditText)findViewById(R.id.et_longitude);

		Intent intent = getIntent();
		notepos = intent.getIntExtra("NOTEPOS", -1);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DBOpenHelper openhelper = new DBOpenHelper(this);
		db = openhelper.getWritableDatabase();
		
		titlelist = NoteDB.getTitleList(db);

		if (notepos != -1) {
			String title = titlelist.get(notepos);
			et_title.setText(title);
            et_latitude.setText(NoteDB.getLatitude(db, title));
            et_longitude.setText(NoteDB.getLongitude(db, title));
        } else {
			et_title.setText("");
			et_latitude.setText("");
            et_longitude.setText("");
        }
	}

	@Override
	public void onPause() {
		super.onPause();
		String title = et_title.getText().toString();
		if (title.length() == 0) {
			Toast.makeText(this, "標題不能為空白，便條無儲存",
					Toast.LENGTH_LONG).show();
		} else {
			NoteDB.addNote(db,
                    et_title.getText().toString(),
                    et_latitude.getText().toString(),
                    et_longitude.getText().toString());
		}
	}

	boolean isTitleExist(String title) {
		for (int i = 0; i < titlelist.size(); i++)
			if (title.equalsIgnoreCase(titlelist.get(i)))
				return true;
		return false;
	}
}
