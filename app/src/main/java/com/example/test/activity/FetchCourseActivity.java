package com.example.test.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.test.R;
import com.example.test.adapter.CourseInfoAdapter;
import com.example.test.adapter.SpinnerAdapter;
import com.example.test.entity.CourseInfo;
import com.example.test.service.DBService;
import com.example.test.util.Fetcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FetchCourseActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private Spinner teacherSpinner;
    private Spinner weekSpinner;
    private static List<String> teacherDatas;
    private ArrayAdapter<String> teacherAdapter;
    private ArrayAdapter<String> weekAdapter;
    private EditText yzm;
    private Fetcher fetcher;
    private DBService dbService;
    private List<CourseInfo> courseInfos = new ArrayList<>();
    private ListView lv;
    private CourseInfoAdapter courseInfoAdapter;
    private Bitmap bit;
    private ImageView yzmImage;
    private Handler handler;
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                yzmImage.setImageBitmap(bit);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_course);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        weekSpinner = findViewById(R.id.weekSpinner);
        yzm = findViewById(R.id.yzm);
        lv = findViewById(R.id.courseInfo);
        yzmImage = findViewById(R.id.yzmImage);
        dbService = new DBService(this);
        // 1、开启权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //2、 获取教师的信息
        getTeacherInfo();
        teacherAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, android.R.id.text1, teacherDatas);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);
        teacherSpinner.setOnItemSelectedListener(this);
        //3、获取周次
        List<String> weeks = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));
        weekAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weeks);
        weekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpinner.setAdapter(weekAdapter);
        // 4、显示课程信息
        courseInfoAdapter = new CourseInfoAdapter(this, R.layout.course_info, courseInfos);
        lv.setAdapter(courseInfoAdapter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    courseInfoAdapter.clearData();
                    for (int i = 0; i < courseInfos.size(); i++) {
                        courseInfoAdapter.notifyNewData(courseInfos.get(i));
                    }
                    if (courseInfos.size() == 0)
                        courseInfoAdapter.notifyDataSetChanged();
                }
            }
        };
        // 5、显示验证码
        setImageView();
    }


    public void getTeacherInfo() {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    fetcher = new Fetcher(dbService);
                    fetcher.fetch();
                    teacherDatas = fetcher.fetchTeacherInfo();
                }
            });
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setImageView() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    bit = BitmapFactory.decodeFile("/sdcard/picture/yzm.png");
                    handler2.sendEmptyMessage(2);
                }
            }
        }.start();
    }

    /**
     * 检索数据
     */
    public void checkData(View view) {
        courseInfos.clear();
        // 获取教师名，周次
        final String teacherName = (String) teacherSpinner.getSelectedItem();
        final String week = (String) weekSpinner.getSelectedItem();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                courseInfos = fetcher.fetchTeacherCourse(teacherName, yzm.getText() + "", week);
                handler.sendEmptyMessage(1);
            }
        });
        t.start();
    }

    /**
     * spinner切换事件
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                fetcher.fetch();
            }
        });
        t.start();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
