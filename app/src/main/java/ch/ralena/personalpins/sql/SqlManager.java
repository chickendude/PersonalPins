package ch.ralena.personalpins.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;

public class SqlManager {
	SqlHelper sqlHelper;
	SQLiteDatabase database;

	public SqlManager(Context context) {
		sqlHelper = new SqlHelper(context);
	}

	public void open() {
		database = sqlHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	private long getLong(Cursor cursor, String columnName) {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getLong(index);
	}

	private String getString(Cursor cursor, String columnName) {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getString(index);
	}

	public List<Pin> getPins() {
//		database.delete(SqlHelper.TABLE_PIN, BaseColumns._ID + ">-1", null);
//		database.delete(SqlHelper.TABLE_PINTAG, BaseColumns._ID + ">-1", null);
//		database.delete(SqlHelper.TABLE_TAG, BaseColumns._ID + ">-1", null);

		List<Pin> pins = new ArrayList<>();
		Cursor cursor = database.rawQuery(
				"SELECT * FROM " + SqlHelper.TABLE_PIN,
				null);
		while (cursor.moveToNext()) {
			long id = getLong(cursor, BaseColumns._ID);
			String title = getString(cursor, SqlHelper.COL_PIN_TITLE);
			String type = getString(cursor, SqlHelper.COL_PIN_TYPE);
			String note = getString(cursor, SqlHelper.COL_PIN_NOTE);
			String filepath = getString(cursor, SqlHelper.COL_PIN_FILEPATH);
			List<Tag> tags = getTagsForId(id);
			pins.add(new Pin(id, title, type, note, filepath, tags));
		}
		cursor.close();
		return pins;
	}

	public List<Tag> getTagsForId(long pinId) {
		List<Tag> tags = new ArrayList<>();

		Cursor cursor = database.rawQuery(
				"SELECT " + SqlHelper.COL_PINTAG_FOREIGNKEY_TAG + " FROM " + SqlHelper.TABLE_PINTAG +
						" WHERE " + SqlHelper.COL_PINTAG_FOREIGNKEY_PIN + "=" + pinId,
				null);
		while (cursor.moveToNext()) {
			long tagId = cursor.getLong(0);
			tags.add(getTag(tagId));
		}
		cursor.close();
		return tags;
	}

	private Tag getTag(long tagId) {
		Cursor cursor = database.rawQuery(
				"SELECT " + SqlHelper.COL_TAG_TITLE + " FROM " + SqlHelper.TABLE_TAG +
						" WHERE " + BaseColumns._ID + "=" + tagId,
				null);
		cursor.moveToFirst();
		String title = cursor.getString(0);
		return new Tag(title);
	}

	public long insertPin(Pin pin) {
		// insert pins
		ContentValues values = new ContentValues();
		values.put(SqlHelper.COL_PIN_TITLE, pin.getTitle());
		values.put(SqlHelper.COL_PIN_TYPE, pin.getType());
		values.put(SqlHelper.COL_PIN_NOTE, pin.getNote());
		values.put(SqlHelper.COL_PIN_FILEPATH, pin.getFilepath());
		long pinId = database.insert(SqlHelper.TABLE_PIN, null, values);

		// insert tags
		for (Tag tag : pin.getTags()) {
			ContentValues tagValues = new ContentValues();
			tagValues.put(SqlHelper.COL_TAG_TITLE, tag.getTitle());
			long tagId = database.insert(SqlHelper.TABLE_TAG, null, tagValues);

			ContentValues pintagValues = new ContentValues();
			pintagValues.put(SqlHelper.COL_PINTAG_FOREIGNKEY_PIN, pinId);
			pintagValues.put(SqlHelper.COL_PINTAG_FOREIGNKEY_TAG, tagId);
			database.insert(SqlHelper.TABLE_PINTAG, null, pintagValues);
		}

		return pinId;
	}
}
