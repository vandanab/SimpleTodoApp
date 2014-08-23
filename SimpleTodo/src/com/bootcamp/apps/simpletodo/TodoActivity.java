package com.bootcamp.apps.simpletodo;

import com.bootcamp.apps.simpletodo.contentprovider.SimpleTodoContentProvider;
import com.bootcamp.apps.simpletodo.database.SimpleTodoTable;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ListView;


public class TodoActivity extends Activity implements 
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final int REQUEST_CODE = 3;
	private SimpleCursorAdapter itemsCursorAdapter;
	private ListView lvItems;
	private EditText etNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        
        lvItems = (ListView) findViewById(R.id.lvItems);
        etNewItem = (EditText) findViewById(R.id.etNewItem);
        
        getLoaderManager().initLoader(0, null, this);
        initListAdapter();
        
        setUpListViewListeners();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo, menu);
        return true;
    }

	/**
	 * Handle events for menu items on the action bar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.avClear:
	        	// Clear the todo list on clicking the delete icon on the action bar.
	            deleteAll();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * @return Loader for cursor for the result of the query for items in the todo list.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { SimpleTodoTable.COLUMN_ID, SimpleTodoTable.COLUMN_ITEM };
	    CursorLoader cursorLoader = new CursorLoader(this,
	        SimpleTodoContentProvider.CONTENT_URI, projection, null, null, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		itemsCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		itemsCursorAdapter.swapCursor(null);
	}

	/**
	 * After return from EditItemActivity.
	 * @param requestCode the requestCode received from the intent on returning.
	 * 		Used to distinguish between the different intents for several activity requests.
	 * 		In this case, we have just one activity request (EditItemActivity).
	 * @param resultCode the result code returned by the activity we are returning from.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			Toast.makeText(TodoActivity.this, "Todo item edited", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * On click listener to add an item to the todo list when the "Add" button is clicked.
	 * @param v view object passed to the listener.
	 */
	public void addItem(View v) {
    	String itemText = etNewItem.getText().toString();
    	ContentValues values = new ContentValues();
    	values.put(SimpleTodoTable.COLUMN_ITEM, itemText);
    	getContentResolver().insert(SimpleTodoContentProvider.CONTENT_URI, values);
    	etNewItem.setText("");
    }

	// Setup a cursor adapter for the list view.
	// Cursor for results from querying the items from the db.
	private void initListAdapter() {
    	// Fields from the database (projection).
        // Must include the _id column for the adapter to work.
        String[] colNames = new String[] { SimpleTodoTable.COLUMN_ITEM };
        int[] to = new int[] {android.R.id.text1};
    	itemsCursorAdapter = new SimpleCursorAdapter(this,
    			android.R.layout.simple_list_item_1, null,
    			colNames, to, /** FLAG_AUTO_REQUERY */ 0);
    	lvItems.setAdapter(itemsCursorAdapter);
    }

	// Setup listeners for valid actions on list view.
    private void setUpListViewListeners() {
    	// Delete items on long click on an item in the list.
		lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View item,
					int position, long id) {
				Uri uri = Uri.parse(SimpleTodoContentProvider.CONTENT_URI + "/" + id);
				getContentResolver().delete(uri, null, null);
				return false;
			}
		});
		
		// Edit item on click of an item in the list.
		lvItems.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View item,
					int position, long id) {
				// Load the edit item activity with the text of the item.
				Intent i = new Intent(TodoActivity.this, EditItemActivity.class);
				i.putExtra("itemText", ((TextView)item).getText());
				i.putExtra("itemId", (int)id);
				startActivityForResult(i, REQUEST_CODE);
			}
		});
	}

    // Delete all items in the todo list.
    private void deleteAll() {
		getContentResolver().delete(SimpleTodoContentProvider.CONTENT_URI, null, null);
	}
}
