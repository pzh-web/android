package com.example.test.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.entity.CourseInfo;

import java.util.List;

public class CourseInfoAdapter extends ArrayAdapter<CourseInfo> {
    private Context context;
    private int resource;
    private List<CourseInfo> courseInfos;
    private static final String[] WEEKTIME = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static final String[] COURSESECTION = {"上午 1-2节", "上午 3-4节", "下午 5-6节", "下午 7-8节", "晚上 9-10节"};

    public CourseInfoAdapter(Context context, int resource, List<CourseInfo> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.context = context;
        this.courseInfos = objects;
    }

    @Override
    public int getCount() {
        return this.courseInfos.size();
    }

    @Nullable
    @Override
    public CourseInfo getItem(int position) {
        return this.courseInfos.get(position);
    }

    public void clearData(){
        courseInfos.clear();
    }


    public void notifyNewData(CourseInfo courseInfo) {
        courseInfos.add(courseInfo);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CourseInfo courseInfo = (CourseInfo) getItem(position);
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder.weekTime = convertView.findViewById(R.id.weekTime);
            holder.className = convertView.findViewById(R.id.className);
            holder.classRoom = convertView.findViewById(R.id.classRoom);
            holder.courseName = convertView.findViewById(R.id.courseName);
            holder.courseSection = convertView.findViewById(R.id.courseSection);
            holder.number = convertView.findViewById(R.id.number);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.weekTime.setText(WEEKTIME[courseInfo.getWeekTime() - 1]);
        holder.courseName.setText(courseInfo.getCourseName());
        holder.classRoom.setText(courseInfo.getClassRoom());
        holder.className.setText(courseInfo.getClassName());
        holder.courseSection.setText(COURSESECTION[courseInfo.getCourseSection() - 1]);
        holder.number.setText("" + courseInfo.getNumber());

        return convertView;
    }

    public static class Holder {
        TextView weekTime;
        TextView className;
        TextView classRoom;
        TextView number;
        TextView courseName;
        TextView courseSection;
    }
}