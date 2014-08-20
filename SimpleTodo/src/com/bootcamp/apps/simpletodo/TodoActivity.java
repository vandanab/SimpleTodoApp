package com.bootcamp.apps.simpletodo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class TodoActivity extends Activity {

	private static final String ITEMS_FILENAME = "items.txt";
	private static final int REQUEST_CODE = 3;
	private ArrayList<String> items;
	private ArrayAdapter<String> itemsAdapter;
	private ListView lvItems;
	private EditText etNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        items = new ArrayList<String>();
        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        etNewItem = (EditText) findViewById(R.id.etNewItem);
        setUpListViewListener();
    }

    private void setUpListViewListener() {
		lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View item,
					int position, long id) {
				items.remove(position);
				itemsAdapter.notifyDataSetChanged();
				writeItems();
				return false;
			}
		});
		
		lvItems.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View item,
					int position, long id) {
				// Load the edit item activity with the text of the item
				Intent i = new Intent(TodoActivity.this, EditItemActivity.class);
				i.putExtra("itemText", items.get(position));
				i.putExtra("itemIndex", position);
				startActivityForResult(i, REQUEST_CODE);
			}
		});
	}
  
    private void readItems() {
    	File filesDir = getFilesDir();
    	File itemsFile = new File(filesDir, ITEMS_FILENAME);
    	try {
    		items = new ArrayList<String>(FileUtils.readLines(itemsFile));
    	} catch (IOException e) {
    		items = new ArrayList<String>();
    	}
    }

    private void writeItems() {
    	File filesDir = getFilesDir();
    	File itemsFile = new File(filesDir, ITEMS_FILENAME);
    	try {
    		FileUtils.writeLines(itemsFile,  items);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo, menu);
        return true;
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			int itemIndex = data.getIntExtra("itemIndex", -1);
			if (itemIndex >= 0) {
				items.set(itemIndex, data.getStringExtra("editedText"));
				itemsAdapter.notifyDataSetChanged();
				writeItems();
			}
		}
	}

    public void addItem(View v) {
    	String itemText = etNewItem.getText().toString();
    	itemsAdapter.add(itemText);
    	etNewItem.setText("");
    	writeItems();
    }
}
