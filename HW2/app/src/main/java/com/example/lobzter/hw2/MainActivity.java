package com.example.lobzter.hw2;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

	String TABLE_NAME = "latlng";

    Button btn_create;
	ListView lv_notes;
    EditText et_latitude, et_longitude;
	SQLiteDatabase db;
	ArrayList<String> titlelist;
    private ListViewMenuListener lclickmenu = new ListViewMenuListener();
    LayoutInflater layoutInflater;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        btn_create = (Button)findViewById(R.id.btn_addnote);
        btn_create.setOnClickListener(listen_btn_create);
		
		lv_notes = (ListView)findViewById(R.id.lv_notes);
        lv_notes.setOnCreateContextMenuListener(lclickmenu);
		lv_notes.setOnItemClickListener(iclick);

        layoutInflater = getLayoutInflater();

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
        // reset list view
        getList();
	}


	OnClickListener listen_btn_create = new OnClickListener() {
		@Override
		public void onClick(View v) {
            // initialize the view used by alertdialog
            View v_create = layoutInflater.inflate(R.layout.alertdialog_create, null);
            et_latitude = (EditText) (v_create.findViewById(R.id.et_latitude));
            et_longitude = (EditText) (v_create.findViewById(R.id.et_longitude));

            // set alertdialog
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("CREATE")
                    .setView(v_create)
                    .setNegativeButton("save", new DialogInterface.OnClickListener() {
                        @Override
                        // on click save
                        public void onClick(DialogInterface dialog, int which) {
                            if (et_latitude.getText().toString().matches("") || et_longitude.getText().toString().matches("")) {
                                Toast.makeText(getApplicationContext(), "Fields can't be blank.", Toast.LENGTH_SHORT).show();
                            } else if (Double.parseDouble(et_latitude.getText().toString()) > 90.0 || Double.parseDouble(et_latitude.getText().toString()) < -90.0) {
                                Toast.makeText(getApplicationContext(), "Latitude should be -90.0 ~ 90.0", Toast.LENGTH_SHORT).show();
                            } else if (Double.parseDouble(et_longitude.getText().toString()) > 180.0 || Double.parseDouble(et_longitude.getText().toString()) < -180.0) {
                                Toast.makeText(getApplicationContext(), "Latitude should be -180.0 ~ 180.0", Toast.LENGTH_SHORT).show();
                            } else {
                                ContentValues cv = new ContentValues();
                                cv.put("title", "(" + et_latitude.getText().toString() + ", " + et_longitude.getText().toString() + ")");
                                cv.put("latitude", et_latitude.getText().toString());
                                cv.put("longitude", et_longitude.getText().toString());
                                db.insert(TABLE_NAME, null, cv);
                                Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
                                getList();
                            }
                        }
                    })
                    .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }

                    )
                                .

                        show();
                    }
        };

    // go to google map activity
	OnItemClickListener iclick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> av, View v, int position, long id) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MapsActivity.class);

            String title = titlelist.get(position);
            Cursor c = db.rawQuery("select * from " + TABLE_NAME + " where title='" + title +"';", null);
            c.moveToFirst();
            // transfer lat and lng by intent
            intent.putExtra("LAT", Double.parseDouble(c.getString(c.getColumnIndex("latitude"))));
            intent.putExtra("LNG", Double.parseDouble(c.getString(c.getColumnIndex("longitude"))));
            startActivity(intent);
        }
	};

    // on list item long click -> list view menu
    private class ListViewMenuListener implements View.OnCreateContextMenuListener {
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, 0, 0, "modify");
            menu.add(0, 1, 0, "delete");
        }
    }

    // implement list view menu
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final String title = titlelist.get(info.position);
        switch(item.getItemId()) {
            // on click modify
            case 0:
                // initialize the view used by alertdialog
                View v_create = layoutInflater.inflate(R.layout.alertdialog_create, null);
                et_latitude = (EditText) (v_create.findViewById(R.id.et_latitude));
                et_longitude = (EditText) (v_create.findViewById(R.id.et_longitude));

                // set initial value in textview
                Cursor c = db.rawQuery("select * from " + TABLE_NAME + " where title='" + title +"';", null);
                c.moveToFirst();
                et_latitude.setText(c.getString(c.getColumnIndex("latitude")));
                et_longitude.setText(c.getString(c.getColumnIndex("longitude")));

                // set alertdialog
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("MODIFY")
                        .setView(v_create)
                        .setNegativeButton("save", new DialogInterface.OnClickListener() {

                            // on button save click
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (et_latitude.getText().toString().matches("") || et_longitude.getText().toString().matches("")) {
                                    Toast.makeText(getApplicationContext(), "Fields can't be blank.", Toast.LENGTH_SHORT).show();
                                } else if (Double.parseDouble(et_latitude.getText().toString()) > 90.0 || Double.parseDouble(et_latitude.getText().toString()) < -90.0) {
                                    Toast.makeText(getApplicationContext(), "Latitude should be -90.0 ~ 90.0", Toast.LENGTH_SHORT).show();
                                } else if (Double.parseDouble(et_longitude.getText().toString()) > 180.0 || Double.parseDouble(et_longitude.getText().toString()) < -180.0) {
                                    Toast.makeText(getApplicationContext(), "Latitude should be -180.0 ~ 180.0", Toast.LENGTH_SHORT).show();
                                } else {
                                    db.delete(TABLE_NAME, "title=" + "'" + title + "'", null);
                                    ContentValues cv = new ContentValues();
                                    cv.put("title", "(" + et_latitude.getText().toString() + ", " + et_longitude.getText().toString() + ")");
                                    cv.put("latitude", et_latitude.getText().toString());
                                    cv.put("longitude", et_longitude.getText().toString());
                                    db.insert(TABLE_NAME, null, cv);
                                    Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
                                    getList();
                                }
                            }
                        })
                        .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                break;
            // on click delete
            case 1:
                db.delete(TABLE_NAME, "title=" + "'" + title + "'", null);
                Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                getList();
                break;
        }
        return super.onContextItemSelected(item);
    }


    // reset list view
    public void getList() {

        titlelist = new ArrayList<String>();
        Cursor c = db.rawQuery("select * from " + TABLE_NAME, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            int titleIndex = c.getColumnIndex("title");
            String title = c.getString(titleIndex);
            titlelist.add(title);
            c.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, titlelist);
        lv_notes.setAdapter(adapter);
    }

}
