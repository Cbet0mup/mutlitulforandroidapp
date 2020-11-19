package com.pavel.multitool.noteFileSupplement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
                                                //разметка таблицы
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notesDb";
    public static final String TABLE_NOTES_TEXT = "notesText";
    public static final String TABLE_TRASH_TEXT = "notesTrash";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "textBody";


    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override       //метод первого запуска
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NOTES_TEXT + "(" + KEY_ID
                + " integer primary key autoincrement, " + KEY_TITLE + " text, " + KEY_TEXT + " text" + ")");

        db.execSQL("create table " + TABLE_TRASH_TEXT + "(" + KEY_ID
                + " integer primary key autoincrement, " + KEY_TITLE + " text, " + KEY_TEXT + " text" + ")");

    }

    @Override   //это для обновления ДБ если потребуется
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES_TEXT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRASH_TEXT);
                onCreate(db);
    }

    //достать все записки
    public List<TextTableModel> getAllNotes(String tableName){
        List<TextTableModel> allNote = new ArrayList<>();

        // 1. создаём строку запроса
        String query = "SELECT  * FROM " + tableName;

        // 2. получаем ссылку на экземпляр DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. проходим по строкам бд, создаём экземпляр записки и кладём в коллекцию
        TextTableModel tableNotes;
        if (cursor.moveToFirst()) {
            try {
                do {
                    tableNotes = new TextTableModel();
                    tableNotes.setId(Integer.parseInt(cursor.getString(0)));
                    tableNotes.setTitle(cursor.getString(1));
                    tableNotes.setBody(cursor.getString(2));

                    // добавляем записку в коллекцию
                    allNote.add(tableNotes);
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }

        }


        return allNote;
    }

    public void addNote(TextTableModel book, String tableName){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, book.getTitle()); // get title
        values.put(KEY_TEXT, book.getBody()); // get body

        // 3. insert
        db.insert(tableName, // table
                null, //nullColumnHack
                values);

        // 4. close
        db.close();
    }

    // удаление записки
    public void deleteNote(TextTableModel note, String tableName) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(tableName,
                KEY_TITLE+" = ?",
                new String[] { String.valueOf(note.getTitle()) });

        // 3. close
        db.close();


    }

    public int updateNote(TextTableModel tableName) {
        // Updating


            // 1. get reference to writable DB
            SQLiteDatabase db = this.getWritableDatabase();

            // 2. create ContentValues
            ContentValues values = new ContentValues();
            values.put("title", tableName.getTitle()); // get title
            values.put("textBody", tableName.getBody()); // get body

            // 3. updating row
            db.update(TABLE_NOTES_TEXT, //table
                    values, // column/value
                    KEY_ID+" = ?", // selections
                    new String[] { String.valueOf(tableName.getId()) }); //selection args

            // 4. close
            db.close();

            return 1;

    }
}
