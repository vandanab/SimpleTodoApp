package com.bootcamp.apps.simpletodo;

import com.bootcamp.apps.simpletodo.contentprovider.SimpleTodoContentProvider;
import com.bootcamp.apps.simpletodo.database.SimpleTodoTable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends Activity {

	private EditText etEditItem;
	private int itemId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_item);
		etEditItem = (EditText) findViewById(R.id.etEditItem);
		etEditItem.setText(getIntent().getStringExtra("itemText"));
		// set cursor at the end of the text.
		etEditItem.setSelection(etEditItem.getText().length());
		itemId = getIntent().getIntExtra("itemId", -1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * On click listener to save the change for the item in the todo list,
	 * 		when the "Save" button is clicked.
	 * @param v view object passed to the listener.
	 */
	public void saveEdit(View v) {
		Uri uri = Uri.parse(SimpleTodoContentProvider.CONTENT_URI + "/" + itemId);
		ContentValues values = new ContentValues();
		values.put(SimpleTodoTable.COLUMN_ITEM, etEditItem.getText().toString());
		getContentResolver().update(uri, values, null, null);

		Intent result = new Intent();
		setResult(RESULT_OK, result);
		finish();
	}
}
