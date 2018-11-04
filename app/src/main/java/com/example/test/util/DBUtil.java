package com.example.test.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBUtil extends SQLiteOpenHelper {

    private final static String TEACHER_SQL="create table teacher(id integer primary key autoincrement,teacher_id varchar(10),name varchar(20))";
    /*
    * course_section:1 1-2节,2 3-4节,3 5-6节，4 7-8节，5 9-10节
    * week_time:星期几---1,2,3,4,5,6,7
    * week:第几周----1,2,3,4,、、、,20
    * class_room：上课教室
    * class_name:课程名
    * */
    private final static String COURSE_SQL="create table course(id integer primary key autoincrement,teacher_id varchar(10),course_section integer,course_name varchar(30),class_room varchar(30),class_name varchar(30),number integer,week_time integer,week integer)";

    public DBUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TEACHER_SQL);
        db.execSQL(COURSE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
