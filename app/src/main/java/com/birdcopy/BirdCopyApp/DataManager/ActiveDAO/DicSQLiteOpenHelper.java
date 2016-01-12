package com.birdcopy.BirdCopyApp.DataManager.ActiveDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birdcopy.BirdCopyApp.DataManager.FlyingItemData;
import com.birdcopy.BirdCopyApp.Download.FlyingFileManager;

public class DicSQLiteOpenHelper extends SQLiteOpenHelper {

	private static DicSQLiteOpenHelper sInstance;

	public static synchronized DicSQLiteOpenHelper getInstance(Context context) {
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new DicSQLiteOpenHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	// Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "mydic.db";

    // table name
    private static final String TABLENAME = "BE_DIC_PUB";

    // Table Columns names
    //private static final String KEY_ID = "id";
    private static final String KEY_WORD  = "BEWORD";
    private static final String KEY_INDEX = "BEINDEX";
    private static final String KEY_ENTRY = "BEENTRY";
	private static final String KEY_TAG   = "BETAG";

	DicSQLiteOpenHelper(Context context) {

	    super(new DicDatabaseContext(context), DATABASE_NAME, null, DATABASE_VERSION,null);
    }

	// Called when the database connection is being configured.
	// Configure database settings for things like foreign key support, write-ahead logging, etc.
	@Override
	public void onConfigure(SQLiteDatabase db) {
		super.onConfigure(db);
		db.setForeignKeyConstraintsEnabled(true);
	}

	// Called when the database is created for the FIRST time.
	// If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {

	    String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLENAME + "("
			    + KEY_WORD  + " TEXT,"
			    + KEY_INDEX + " TEXT,"
			    + KEY_ENTRY + " TEXT"
			    + KEY_TAG   + " TEXT"
			    + ")";
	    db.execSQL(CREATE_ITEMS_TABLE);
    }

	// Called when the database needs to be upgraded.
	// This method will only be called if a database already exists on disk with the same DATABASE_NAME,
	// but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	    if (oldVersion != newVersion) {
		    // Simplest implementation is to drop all old tables and recreate them
		    db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);

		    // Create tables again
		    onCreate(db);
	    }
    }
	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new item
	public void add_DicItem(FlyingItemData itemData) {

		// Create and/or open the database for writing
		SQLiteDatabase db = getWritableDatabase();

		// It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
		// consistency of the database.
		db.beginTransaction();
		try {
			// The user might already exist in the database (i.e. the same user created multiple posts).

			ContentValues values = new ContentValues();
			values.put(KEY_WORD, itemData.getBEWORD());
			values.put(KEY_INDEX, itemData.getBEINDEX());
			values.put(KEY_ENTRY, itemData.getBEENTRY());
			values.put(KEY_INDEX, itemData.getBEINDEX());

			// Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
			db.insertOrThrow(TABLENAME, null, values);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("Add_DicItem", "Error while trying to add post to database");
		} finally {
			db.endTransaction();
		}

		db.close(); // Closing database connection
	}

	// Getting single contact
	public List<FlyingItemData> get_items(String word, String index) {

		List<FlyingItemData> itemDatas = new ArrayList<>();

		SQLiteDatabase db = getReadableDatabase();

		String SELECT_QUERY =
				String.format("SELECT * FROM %s  WHERE BEWORD=? AND BEINDEX=?",TABLENAME);

		Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{word,index});

		try {
			if (cursor.moveToFirst()) {

				do {
					FlyingItemData itemData = new FlyingItemData();

					itemData.setBEWORD(cursor.getString(cursor.getColumnIndex(KEY_WORD)));
					itemData.setBEINDEX(cursor.getString(cursor.getColumnIndex(KEY_INDEX)));
					itemData.setBEENTRY(cursor.getString(cursor.getColumnIndex(KEY_ENTRY)));
					itemData.setBETAG(cursor.getString(cursor.getColumnIndex(KEY_TAG)));

					itemDatas.add(itemData);
				} while(cursor.moveToNext());
			}
		}
		catch (Exception e) {
			Log.d("Get_items", "Error while trying to get posts from database");
		}
		finally
		{
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		db.close();

		return itemDatas;
	}

	// Getting single contact
	public List<FlyingItemData> get_items(String word) {

		List<FlyingItemData> itemDatas = new ArrayList<>();

		SQLiteDatabase db = getReadableDatabase();

		String SELECT_QUERY =
				String.format("SELECT * FROM %s  WHERE BEWORD=?", TABLENAME);


		Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{word});

		try {
			if (cursor.moveToFirst()) {

				do {
					FlyingItemData itemData = new FlyingItemData();

					itemData.setBEWORD(cursor.getString(cursor.getColumnIndex(KEY_WORD)));
					itemData.setBEINDEX(cursor.getString(cursor.getColumnIndex(KEY_INDEX)));
					itemData.setBEENTRY(cursor.getString(cursor.getColumnIndex(KEY_ENTRY)));
					itemData.setBETAG(cursor.getString(cursor.getColumnIndex(KEY_TAG)));

					itemDatas.add(itemData);
				} while(cursor.moveToNext());
			}
		}
		catch (Exception e) {
			Log.d("Get_items", "Error while trying to get posts from database");
		}
		finally
		{
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		db.close();

		return itemDatas;
	}
}

class DicDatabaseContext extends ContextWrapper {

	private static final String DEBUG_CONTEXT = "DicDatabaseContext";

	public DicDatabaseContext(Context base) {
		super(base);
	}

	@Override
	public File getDatabasePath(String name)
	{
		final String dicDBpath = FlyingFileManager.getMyDicDBFilePath();

		File result = FlyingFileManager.getFile(dicDBpath);

		return result;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory)
	{

		return openOrCreateDatabase(name,mode,factory,null);
	}

	@Override
	public  SQLiteDatabase openOrCreateDatabase(String name,
	                                                    int mode, SQLiteDatabase.CursorFactory factory,
	                                                    @Nullable DatabaseErrorHandler errorHandler)
	{
		final String dicDBpath = FlyingFileManager.getMyDicDBFilePath();

		File file = FlyingFileManager.getFile(dicDBpath);

		SQLiteDatabase db =SQLiteDatabase.openOrCreateDatabase(file, null);
		db.setVersion(DicSQLiteOpenHelper.DATABASE_VERSION);

		// SQLiteDatabase result = super.openOrCreateDatabase(name, mode, factory);
		if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN))
		{
			Log.w(DEBUG_CONTEXT,
					"openOrCreateDatabase(" + name + ",,) = " + db.getPath());
		}
		return db;
	}
}
