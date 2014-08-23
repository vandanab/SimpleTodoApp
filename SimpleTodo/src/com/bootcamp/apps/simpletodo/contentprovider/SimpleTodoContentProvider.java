package com.bootcamp.apps.simpletodo.contentprovider;

import com.bootcamp.apps.simpletodo.database.SimpleTodoDatabaseHelper;
import com.bootcamp.apps.simpletodo.database.SimpleTodoTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class SimpleTodoContentProvider extends ContentProvider {

	// application database.
	private SimpleTodoDatabaseHelper database;

	// codes returned by UriMatcher on successful match.
	private static final int ITEMS_CODE = 10;
	private static final int ITEM_ID_CODE = 20;
	
	private static final String AUTHORITY = "com.bootcamp.apps.simpletodo.contentprovider";
	private static final String BASE_PATH = "simpletodo";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/simpletodo";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, ITEMS_CODE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ITEM_ID_CODE);
	}

	@Override
	public boolean onCreate() {
		database = new SimpleTodoDatabaseHelper(getContext());
		return false;
	}

	/**
	 * No use case for query() function. But overriding for sake of completeness.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Set the table
		queryBuilder.setTables(SimpleTodoTable.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
			case ITEMS_CODE:
				break;
		    case ITEM_ID_CODE:
		    	// adding the ID to the original query
		    	queryBuilder.appendWhere(SimpleTodoTable.COLUMN_ID + "="
		    			+ uri.getLastPathSegment());
		    	break;
		    default:
		    	throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    long id = 0;
	    switch (uriType) {
	    	case ITEMS_CODE:
	    		// Insert one item at a time.
	    		id = sqlDB.insert(SimpleTodoTable.TABLE_NAME, null, values);
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    	case ITEMS_CODE:
	    		// Useful for clear list functionality.
	    		// Will use it if I implement clear list functionality.
	    		rowsDeleted = sqlDB.delete(SimpleTodoTable.TABLE_NAME, selection,
	    				selectionArgs);
	    		break;
	    	case ITEM_ID_CODE:
	    		String id = uri.getLastPathSegment();
	    		if (TextUtils.isEmpty(selection)) {
	    			rowsDeleted = sqlDB.delete(SimpleTodoTable.TABLE_NAME,
	    					SimpleTodoTable.COLUMN_ID + "=" + id, null);
	    		} else {
	    			rowsDeleted = sqlDB.delete(SimpleTodoTable.TABLE_NAME,
	    					SimpleTodoTable.COLUMN_ID + "=" + id + " and " + selection,
	    					selectionArgs);
	    		}
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
			case ITEMS_CODE:
				// Just for completeness.
				rowsUpdated = sqlDB.update(SimpleTodoTable.TABLE_NAME, values, 
						selection, selectionArgs);
				break;
			case ITEM_ID_CODE:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(SimpleTodoTable.TABLE_NAME, 
							values,
							SimpleTodoTable.COLUMN_ID + "=" + id,
							null);
				} else {
					rowsUpdated = sqlDB.update(SimpleTodoTable.TABLE_NAME,
							values,
							SimpleTodoTable.COLUMN_ID + "=" + id + " and " + selection,
							selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}
}
