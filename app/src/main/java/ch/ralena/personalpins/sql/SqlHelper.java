package ch.ralena.personalpins.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class SqlHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "personalpins.db";
	private static final int DB_VERSION = 1;

	// TABLES
	public static final String TABLE_PIN = "PIN";
	public static final String TABLE_TAG = "TAG";
	public static final String TABLE_PINTAG = "PINTAG";
	public static final String TABLE_BOARD = "BOARD";
	public static final String TABLE_BOARDPIN = "BOARDPIN";


	public SqlHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	// pin fields
	public static final String COL_PIN_TITLE = "title";
	public static final String COL_PIN_TYPE = "type";
	public static final String COL_PIN_NOTE = "note";
	public static final String COL_PIN_FILEPATH = "filepath";
	// tag fields
	public static final String COL_TAG_TITLE = "title";
	// pintag fields
	public static final String COL_PINTAG_FOREIGNKEY_PIN = "pin_id";
	public static final String COL_PINTAG_FOREIGNKEY_TAG = "tag_id";
	// board fields
	public static final String COL_BOARD_TITLE = "title";
	public static final String COL_BOARD_COVER_FILEPATH = "cover_filepath";
	// boardpin fields
	public static final String COL_BOARDPIN_FOREIGNKEY_BOARD = "board_id";
	public static final String COL_BOARDPIN_FOREIGNKEY_PIN = "pin_id";

	// SQL
	private static final String CREATE_PIN =
			"CREATE TABLE " + TABLE_PIN +
					"( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COL_PIN_TITLE + " TEXT, " +
					COL_PIN_TYPE + " TEXT, " +
					COL_PIN_NOTE + " TEXT, " +
					COL_PIN_FILEPATH + " TEXT " +
					" )";
	private static final String CREATE_TAG =
			"CREATE TABLE " + TABLE_TAG +
					"( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COL_TAG_TITLE + " TEXT " +
					" )";
	private static final String CREATE_PINTAG =
			"CREATE TABLE " + TABLE_PINTAG +
					"( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COL_PINTAG_FOREIGNKEY_PIN + " INT, " +
					COL_PINTAG_FOREIGNKEY_TAG + " INT, " +
					"FOREIGN KEY(" + COL_PINTAG_FOREIGNKEY_PIN + ") REFERENCES " + TABLE_PIN + "(_ID), " +
					"FOREIGN KEY(" + COL_PINTAG_FOREIGNKEY_TAG + ") REFERENCES " + TABLE_TAG + "(_ID) " +
					" )";
	private static final String CREATE_BOARD =
			"CREATE TABLE " + TABLE_BOARD +
					"( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COL_BOARD_TITLE + " TEXT, " +
					COL_BOARD_COVER_FILEPATH + " TEXT " +
					" )";
	private static final String CREATE_BOARDPIN =
			"CREATE TABLE " + TABLE_BOARDPIN +
					"( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COL_BOARDPIN_FOREIGNKEY_BOARD + " INT, " +
					COL_BOARDPIN_FOREIGNKEY_PIN + " INT, " +
					"FOREIGN KEY(" + COL_BOARDPIN_FOREIGNKEY_BOARD + ") REFERENCES " + TABLE_PIN + "(_ID), " +
					"FOREIGN KEY(" + COL_BOARDPIN_FOREIGNKEY_PIN + ") REFERENCES " + TABLE_TAG + "(_ID) " +
					" )";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PIN);
		db.execSQL(CREATE_TAG);
		db.execSQL(CREATE_PINTAG);
		db.execSQL(CREATE_BOARD);
		db.execSQL(CREATE_BOARDPIN);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
