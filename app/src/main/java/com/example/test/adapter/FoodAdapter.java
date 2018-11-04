package com.example.test.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.entity.Food;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {
    private int viewResourceId;
    public FoodAdapter(Context context, int resource, List<Food> objects) {
        super(context, resource, objects);
        this.viewResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前food实例
        final Food food = (Food)getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(viewResourceId, parent,false);//实例化一个对象
        TextView name = view.findViewById(R.id.name);
        TextView price = view.findViewById(R.id.price);
        name.setText(food.getName());
        price.setText(food.getPrice());

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("name",food.getName());
            }
        });
        return view;
    }
}
