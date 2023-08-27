// https://developer.android.com/training/data-storage/sqlite
// https://developer.android.com/reference/android/database/Cursor#getColumnIndexOrThrow(java.lang.String)

package com.example.listsqre

import android.content.Context
import android.provider.BaseColumns
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

object TableTemplate : BaseColumns { // for listsqre table
    const val TABLE_NAME = "listsqre"
    const val COLUMN_NAME_ID = "idnum"
    const val COLUMN_NAME_TITLE = "listname"

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_NAME_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME_TITLE TEXT)"

    const val SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS $TABLE_NAME"
}

class ListsqreDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TableTemplate.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(TableTemplate.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
    }
}

// ----- global CRUD methods for handling database ----- //

fun feedIntoDb(context: Context, Id: Int, listName: String) {
    val dbHelper = ListsqreDbHelper(context)
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
        put(TableTemplate.COLUMN_NAME_ID, Id)
        put(TableTemplate.COLUMN_NAME_TITLE, listName)
    }
    db?.insert(TableTemplate.TABLE_NAME, null, values)
    db.close()
}

fun readFromDb(context: Context) {
    val dbHelper = ListsqreDbHelper(context)
    val db = dbHelper.readableDatabase
    val projection = arrayOf(TableTemplate.COLUMN_NAME_TITLE)
    val cursor = db.query(TableTemplate.TABLE_NAME, projection, null, null, null, null, null)
    with(cursor) {
        while (moveToNext()) {
            val listname = getString(getColumnIndexOrThrow(TableTemplate.COLUMN_NAME_TITLE))
            Listsqre.addNode(listname)
        }
    }
    cursor.close()
    db.close()
}

fun updateDb(context: Context, delTable: Boolean) {
    val dbHelper = ListsqreDbHelper(context)
    val db = dbHelper.writableDatabase
    if(delTable) {
        db.delete(TableTemplate.TABLE_NAME, null, null)
    } else {
        db.delete(TableTemplate.TABLE_NAME, null, null)
        for(obj in Listsqre.getEntireList()) {
            feedIntoDb(context, obj.getId(), obj.getListname())
        }
    }
    db.close()
}