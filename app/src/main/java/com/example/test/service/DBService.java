package com.example.test.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.test.entity.CourseInfo;
import com.example.test.entity.Food;
import com.example.test.util.DBHelper;
import com.example.test.util.DBUtil;

import java.util.ArrayList;
import java.util.List;

public class DBService {
    /*操作SQLite*/
    private DBUtil dbUtil;
    private SQLiteDatabase db;

    public DBService(Context context) {
        dbUtil = new DBUtil(context, "course_info", null, 1);
        db = dbUtil.getWritableDatabase();
    }

    public void addTeacher(String teacherId, String name) {
        String sql = "insert into teacher (teacher_id,name) values(?,?)";
        db.execSQL(sql, new Object[]{teacherId, name});
    }

    public void addCourse(CourseInfo courseInfo) {
        String sql = "insert into course(teacher_id,course_section,course_name,class_room,class_name,number,week_time,week) values(?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{courseInfo.getTeacherId(), courseInfo.getCourseSection(), courseInfo.getCourseName(), courseInfo.getClassRoom(),
                courseInfo.getClassName(), courseInfo.getNumber(), courseInfo.getWeekTime(), courseInfo.getWeek()});
    }

    public List<CourseInfo> queryCourseInfoByTeacherIdAndWeek(String teacher_id, String week) {
        CourseInfo courseInfo = null;
        List<CourseInfo> list = new ArrayList<>();
        String sql = "select * from course where teacher_id=? and week=? order by week_time,course_section";
        Cursor c = db.rawQuery(sql, new String[]{teacher_id, week});
        while (c.moveToNext()) {
            courseInfo = new CourseInfo();
            String courseSection = c.getString(c.getColumnIndex("course_section"));
            String courseName = c.getString(c.getColumnIndex("course_name"));
            String classRoom = c.getString(c.getColumnIndex("class_room"));
            String className = c.getString(c.getColumnIndex("class_name"));
            String number = c.getString(c.getColumnIndex("number"));
            String weekTime = c.getString(c.getColumnIndex("week_time"));
            courseInfo.setClassName(className);
            courseInfo.setCourseName(courseName);
            courseInfo.setWeekTime(Integer.valueOf(weekTime));
            courseInfo.setNumber(Integer.valueOf(number));
            courseInfo.setClassRoom(classRoom);
            courseInfo.setCourseSection(Integer.valueOf(courseSection));
            list.add(courseInfo);
        }
        return list;
    }

    public String queryTeacherIdByName(String name) {
        String sql = "select * from teacher where name=?";
        Cursor c = db.rawQuery(sql, new String[]{name});
        String teacherId = null;
        while (c.moveToNext()) {
            teacherId = c.getString(c.getColumnIndex("teacher_id"));
        }
        return teacherId;
    }

    public List<String> queryTeacher() {
        List<String> teachers = getSearchTeacher();
        int len = teachers.size();
        String sql = "select name from teacher where teacher_id not in(select teacher_id from course)";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            teachers.add(c.getString(c.getColumnIndex("name")));
        }
        teachers.add(len + "");
        return teachers;
    }

    public List<String> getSearchTeacher() {
        String sql = "select distinct name from teacher t,course c where t.teacher_id=c.teacher_id";
        Cursor c = db.rawQuery(sql, null);
        List<String> teachers = new ArrayList<>();
        while (c.moveToNext()) {
            teachers.add(c.getString(c.getColumnIndex("name")));
        }
        return teachers;
    }
}
