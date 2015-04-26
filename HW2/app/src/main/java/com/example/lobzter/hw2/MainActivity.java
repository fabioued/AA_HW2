package com.example.lobzter.hw2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	Button btn_addnote;
	ListView lv_notes;
	SQLiteDatabase db;
	ArrayList<String> titlelist;
    private ListViewMenuListener lclickmenu = new ListViewMenuListener();


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn_addnote = (Button)findViewById(R.id.btn_addnote);
		btn_addnote.setOnClickListener(addnote);
		
		lv_notes = (ListView)findViewById(R.id.lv_notes);
        lv_notes.setOnCreateContextMenuListener(lclickmenu);
		lv_notes.setOnItemClickListener(iclick);

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		db.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		DBOpenHelper openhelper = new DBOpenHelper(this);
		db = openhelper.getWritableDatabase();
		
		titlelist = NoteDB.getTitleList(db);
		ArrayAdapter<String> adapter =new ArrayAdapter<String>
			(this, android.R.layout.simple_list_item_1, titlelist);
		lv_notes.setAdapter(adapter);
	}

	OnClickListener addnote = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,
					NoteEditor.class);
			intent.putExtra("NOTEPOS", -1);
			startActivity(intent);
		}
	};

	OnItemClickListener iclick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> av, View v,
			int position, long id) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,
					MapsActivity.class);
			intent.putExtra("NOTEPOS", position);
			startActivity(intent);
		}
	};

    private class ListViewMenuListener implements View.OnCreateContextMenuListener {
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, 0, 0, "modify");
            menu.add(0, 1, 0, "delete");
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,
                        NoteEditor.class);
                intent.putExtra("NOTEPOS", info.position);
                startActivity(intent);
                break;
            case 1:
                String title = titlelist.get(info.position);
                NoteDB.delNote(db, title);
                titlelist = NoteDB.getTitleList(db);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (MainActivity.this,
                                android.R.layout.simple_list_item_1, titlelist);
                lv_notes.setAdapter(adapter);
                break;
        }
        return super.onContextItemSelected(item);
    }

}
