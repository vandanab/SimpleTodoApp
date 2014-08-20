package com.bootcamp.apps.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends Activity {

	private EditText etEditItem;
	private int itemIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_item);
		etEditItem = (EditText) findViewById(R.id.etEditItem);
		etEditItem.setText(getIntent().getStringExtra("itemText"));
		etEditItem.setSelection(etEditItem.getText().length()); // set cursor at the end of the text.
		itemIndex = getIntent().getIntExtra("itemIndex", -1); // for sending back to the TodoActivity.
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

	public void saveEdit(View v) {
		Intent result = new Intent();
		result.putExtra("itemIndex", itemIndex);
		result.putExtra("editedText", etEditItem.getText().toString());
		setResult(RESULT_OK, result);
		finish();
	}
}
