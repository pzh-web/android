package com.example.test.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter {
    private LayoutInflater infalter;
    private int resource;
    private int textViewResourceId;
    private List<String> target;

    public SpinnerAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        target = objects;
        infalter = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.target.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = infalter.inflate(resource, null);
        TextView text = (TextView) convertView.findViewById(textViewResourceId);
        text.setText(target.get(position));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = infalter.inflate(resource, null);
        TextView text = (TextView) convertView.findViewById(textViewResourceId);
        text.setText(target.get(position));
        System.out.println(target.get(getCount() - 1)+"----------------------------");
        if (position < Integer.valueOf(target.get(getCount() - 1))) {
            text.setBackgroundColor(Color.RED);
        }else {
            text.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }
}

