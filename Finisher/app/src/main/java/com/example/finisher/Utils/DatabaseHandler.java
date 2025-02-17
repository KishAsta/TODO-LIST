package com.example.finisher.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.finisher.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER)";
    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create the table again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }//database opening

    //Inserting Task
    public void insertTask(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }

    //Getting all Tasks
    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if (cursor != null) {
                int idIndex = cursor.getColumnIndex(ID);
                int taskIndex = cursor.getColumnIndex(TASK);
                int statusIndex = cursor.getColumnIndex(STATUS);

                while (cursor.moveToNext()) {
                    if (idIndex != -1 && taskIndex != -1 && statusIndex != -1) {
                        ToDoModel task = new ToDoModel();
                        task.setId(cursor.getInt(idIndex));
                        task.setTask(cursor.getString(taskIndex));
                        task.setStatus(cursor.getInt(statusIndex));
                        taskList.add(task);
                    }
                }
            }
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
        return taskList;
    }


    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + " = ?", new String[]{String.valueOf(id)});
    }

    //update Task
    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + " = ?", new String[]{String.valueOf(id)});
    }
    //delete task
    public void deleteTask(int id) {
        db.delete(TODO_TABLE, ID + " = ?", new String[]{String.valueOf(id)});
    }
}
