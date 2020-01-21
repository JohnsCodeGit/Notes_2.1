package com.whiskey.notes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class GroupsDB(context: Context?,
               factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = (
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_INDEX + " INT, "
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_TITLE + " TEXT,"
                        + COLUMN_DATE + " TEXT,"
                        + COLUMN_FAV + " INT"
                        + ")"
                )
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun addGroup(note: String, title: String, date: String, fav: Int, index: Int) {
        val values = ContentValues()
        values.put(COLUMN_NAME, note)
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_FAV, fav)

        val db = this.writableDatabase
        values.put(COLUMN_INDEX, index)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getAllGroups(): ArrayList<NoteModel> {
        val notes = ArrayList<NoteModel>()

        val cursor = this.readableDatabase.rawQuery(SELECT_NOTE, null)
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                if (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) != null) {
                    val note: String = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                    val title: String = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    val date: String = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                    val noteItem = NoteModel(note, title, date)

                    notes.add(noteItem)

                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        return notes
    }
    fun deleteAll(){
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME")
    }
    fun updateGroup(note: String, title: String, date: String, fav: Int, position: Int){
        val newValues = ContentValues()
        newValues.put(COLUMN_NAME, note)
        newValues.put(COLUMN_TITLE, title)
        newValues.put(COLUMN_DATE, date)
        newValues.put(COLUMN_FAV, fav)
        val db = this.writableDatabase
        db.update(TABLE_NAME, newValues, "$COLUMN_INDEX=$position", null)
        db.close()
    }

    fun updateGroup(noteItem: NoteModel, fav: Int, position: Int) {
        val newValues = ContentValues()
        val note = noteItem.note
        val title = noteItem.title
        val date = noteItem.date

        newValues.put(COLUMN_NAME, note)
        newValues.put(COLUMN_TITLE, title)
        newValues.put(COLUMN_DATE, date)
        newValues.put(COLUMN_FAV, fav)
        val db = this.writableDatabase
        db.update(TABLE_NAME, newValues, "$COLUMN_INDEX=$position", null)
        db.close()
    }

    fun deleteItem(item: Int){
        val db = this.writableDatabase
        val items = item.toString()
        db.delete(TABLE_NAME, "item = $items", null)

        db.execSQL(
            "UPDATE $TABLE_NAME  SET $COLUMN_INDEX  = $COLUMN_INDEX -1   WHERE  + $COLUMN_INDEX >  $items "

        )
        db.close()
        Log.d("itemDeletedDataBase", item.toString())
    }
    fun getFav(position: Int): Int{
        val db = this.writableDatabase
        val c: Cursor = db.rawQuery("SELECT $COLUMN_FAV FROM $TABLE_NAME WHERE $COLUMN_INDEX == $position", null)
        val bool:Int
        bool = if (c.moveToFirst()) {
            c.getInt(c.getColumnIndex(COLUMN_FAV))
        } else 0


        c.close()
        db.close()
        Log.d("boolDB", bool.toString())
        return bool


    }

    fun getAllFav(): ArrayList<NoteModel>{
        val db = this.writableDatabase
        val favs = ArrayList<NoteModel>()
        val one = 1
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_FAV == $one", null)
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                if (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) != null) {
                    val note: String = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                    val title: String = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    val date: String = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                    val noteItem = NoteModel(note, title, date)

                    favs.add(noteItem)

                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        return favs


    }
    fun getGroupSize(): Long{
        val db = this.readableDatabase
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME)
    }
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "GroupDatabase.db"
        const val TABLE_NAME = "GroupsTable"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "noteCol"
        const val COLUMN_TITLE = "titleCol"
        const val COLUMN_DATE = "dateCol"
        const val COLUMN_FAV = "favCol"
        const val SELECT_NOTE = "SELECT * FROM $TABLE_NAME"
        const val COLUMN_INDEX = "item"

    }
}