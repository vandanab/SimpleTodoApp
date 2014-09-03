package com.bootcamp.apps.simpletodo.adapter;

import com.bootcamp.apps.simpletodo.EditItemActivity;
import com.bootcamp.apps.simpletodo.R;
import com.bootcamp.apps.simpletodo.contentprovider.SimpleTodoContentProvider;
import com.bootcamp.apps.simpletodo.database.SimpleTodoTable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class SimpleTodoCursorAdapter extends ResourceCursorAdapter {

	private LayoutInflater inflater;

	public static final int REQUEST_CODE = 3;

	 public SimpleTodoCursorAdapter(Context context, Cursor c, int flags) {
		 super(context, R.layout.simple_todo_item, c, flags);
		 inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	 
	 @Override
	 public void bindView(View view, final Context context, final Cursor cursor) {
		 CheckBox todoItemCheckbox = (CheckBox) view.findViewById(R.id.cbTodoItemDoneStatus);
		 TextView todoItem = (TextView) view.findViewById(R.id.tvTodoItem);
		 todoItem.setText(cursor.getString(cursor.getColumnIndex(SimpleTodoTable.COLUMN_ITEM)));
		 boolean isCompleted = cursor.getInt(
				 cursor.getColumnIndex(SimpleTodoTable.COLUMN_STATUS)) == 1 ? true : false;
		 if (isCompleted) {
			 todoItem.setTextColor(Color.GRAY);
		 } else {
			 todoItem.setTextColor(Color.BLACK);
		 }
		 todoItemCheckbox.setChecked(isCompleted);
		 // information to be stored in the view.
		 todoItemCheckbox.setTag(todoItem);
		 todoItem.setTag(cursor.getInt(cursor.getColumnIndex(SimpleTodoTable.COLUMN_ID)));

		 todoItemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				CheckBox c = (CheckBox) buttonView;
				TextView tv = (TextView) c.getTag();

				if (isChecked) {
					tv.setTextColor(Color.GRAY);
				} else {
					tv.setTextColor(Color.BLACK);
				}
				// update the data in the database.
				Uri uri = Uri.parse(SimpleTodoContentProvider.CONTENT_URI + "/" + tv.getTag());
				ContentValues values = new ContentValues();
				values.put(SimpleTodoTable.COLUMN_STATUS, isChecked ? 1 : 0);
				context.getContentResolver().update(uri, values, null, null);
			}
		 });

		 todoItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Load the edit item activity with the text of the item.
				Intent i = new Intent(context, EditItemActivity.class);
				i.putExtra("itemText", ((TextView) v).getText());
				i.putExtra("itemId", (Integer) v.getTag());
				((Activity) context).startActivityForResult(i, REQUEST_CODE);
			}
		 });

		 view.setLongClickable(true);
		 view.setClickable(true);
	 }
	 
	 @Override
	 public View newView(Context context, Cursor cursor, ViewGroup parent) {
	  return inflater.inflate(R.layout.simple_todo_item, parent, false);
	 }
}
