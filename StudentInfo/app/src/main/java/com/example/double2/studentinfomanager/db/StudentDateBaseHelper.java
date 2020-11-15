package com.example.double2.studentinfomanager.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDateBaseHelper extends SQLiteOpenHelper {

    public static final String CreateStudentInfo = "create table student ("
            + "number integer primary key, "
            + "gender text , "
            + "name text,"
            + "date text,"
            + "YN text,"
            + "specialty text,"
            + "temperature text)";

    public StudentDateBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateStudentInfo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }


}