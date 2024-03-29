// https://developer.android.com/training/data-storage/sqlite
// https://developer.android.com/reference/android/database/Cursor#getColumnIndexOrThrow(java.lang.String)

package com.example.listsqre

import android.content.Context
import android.provider.BaseColumns
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

object NotiTableTemplate : BaseColumns { // Schema for Notifications
    const val TABLE_NAME = "listsqreNoti"
    const val COLUMN_NAME_ID = "idnum"
    const val COLUMN_NAME = "notiTitle"
    const val COLUMN_NAME_02 = "notiDescr"
    const val COLUMN_NAME_03 = "notiHour"
    const val COLUMN_NAME_04 = "notiMin"

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_NAME_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_NAME_02 TEXT, " +
                "$COLUMN_NAME_03 INTEGER, " +
                "$COLUMN_NAME_04 INTEGER)"

    const val SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS $TABLE_NAME"
}

data class ListsqreNotiData(val t: String, val d: String, val h: Int, val m: Int)

class ListsqreNotiDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(NotiTableTemplate.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(NotiTableTemplate.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "NotiFeedReader.db"
    }
}

// ----- global CRUD methods for handling database ----- //

fun feedIntoNotiDb(context: Context, id: Int, title: String, descr: String, hr: Int, min: Int) {
    val dbHelper = ListsqreNotiDbHelper(context)
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
        put(NotiTableTemplate.COLUMN_NAME_ID, id)
        put(NotiTableTemplate.COLUMN_NAME, title)
        put(NotiTableTemplate.COLUMN_NAME_02, descr)
        put(NotiTableTemplate.COLUMN_NAME_03, hr)
        put(NotiTableTemplate.COLUMN_NAME_04, min)
    }
    db?.insert(NotiTableTemplate.TABLE_NAME, null, values)
    db.close()
}

fun updateNotiDb(context: Context) {
    val dbHelper = ListsqreNotiDbHelper(context)
    val db = dbHelper.writableDatabase
    if(NotiOfListsqre.getEntireList().isNotEmpty() && !NotiOfListsqre.empty) {
        db.delete(NotiTableTemplate.TABLE_NAME, null, null)
        for(obj in NotiOfListsqre.getEntireList()) {
            feedIntoNotiDb(context, obj.getId(), obj.getT(), obj.getD(), obj.getH(), obj.getM())
        }
    } else if(NotiOfListsqre.getEntireList().isEmpty() && NotiOfListsqre.empty) {
        db.delete(NotiTableTemplate.TABLE_NAME, null, null)
    } else { /* do nothing */ }
    db.close()
}

fun readFromNotiDb(context: Context) { // for displaying cardview
    val dbHelper = ListsqreNotiDbHelper(context)
    val db = dbHelper.readableDatabase
    val cursor = db.query(NotiTableTemplate.TABLE_NAME, null, null, null, null, null, null)
    with(cursor) {
        while(moveToNext()) {
            val t = getString(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME))
            val d = getString(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME_02))
            val h = getInt(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME_03))
            val m = getInt(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME_04))
            NotiOfListsqre.addNode(t, d, h, m)
        }
    }
    cursor.close()
    db.close()
}

fun readNotiFirstEntry(context: Context): ListsqreNotiData { // get first entry in db
    lateinit var data: ListsqreNotiData
    val dbHelper = ListsqreNotiDbHelper(context)
    val db = dbHelper.readableDatabase
    val cursor = db.query(NotiTableTemplate.TABLE_NAME, null, null, null, null, null, null)
    with(cursor) {
        if(moveToFirst()) {
            val t = getString(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME))
            val d = getString(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME_02))
            val h = getInt(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME_03))
            val m = getInt(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME_04))
            data = ListsqreNotiData(t, d, h, m)
        } else {
            // empty Db
            data = ListsqreNotiData("", "", 0, 0)
        }
    }
    cursor.close()
    db.close()
    return data
}

fun upcomingNoti(context: Context): String {
    var rtnStr = "Set daily reminder here\nNext reminder: "
    val dbHelper = ListsqreNotiDbHelper(context)
    val db = dbHelper.readableDatabase
    val cursor = db.query(NotiTableTemplate.TABLE_NAME, null, null, null, null, null, null)
    with(cursor) {
        rtnStr += if(moveToFirst()) {
            val h = getInt(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME_03))
            val m = getInt(getColumnIndexOrThrow(NotiTableTemplate.COLUMN_NAME_04))
            String.format("~ %02d:%02d", h, m)
        } else {
            String.format("None")
        }
    }
    cursor.close()
    db.close()
    return rtnStr
}

fun isNotiDbEmpty(context: Context): Boolean {
    val dbHelper = ListsqreNotiDbHelper(context)
    val db = dbHelper.readableDatabase
    val cursor = db.query(NotiTableTemplate.TABLE_NAME, null, null, null, null, null, null)
    with(cursor) {
        if(!moveToFirst()) {
            return true // empty Db
        } else { /* do nothing */ }
    }
    cursor.close()
    db.close()
    return false // not empty Db
}

/* --- obsolete ---
fun clearNotiDb(context: Context) {
    val dbHelper = ListsqreNotiDbHelper(context)
    val db = dbHelper.writableDatabase
    db.delete(NotiTableTemplate.TABLE_NAME, null, null)
    db.close()
}
*/