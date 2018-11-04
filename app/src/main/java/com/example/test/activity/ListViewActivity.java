package com.example.test.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.adapter.FoodAdapter;
import com.example.test.entity.Food;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends Activity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        List<String> data = new ArrayList<String>();
        data.add("1");
        data.add("2");
        data.add("3");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        ListView lv = findViewById(R.id.lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

        // 自定义adapter
        List<Food> foods = new ArrayList<Food>();
        for (int i=0;i<4;i++){
            Food food = new Food();
            food.setName("name_"+i);
            food.setPrice(i+"");
            foods.add(food);
        }
        FoodAdapter foodAdapter = new FoodAdapter(this,R.layout.food_view,foods);
        ListView foodLv = findViewById(R.id.foodLv);
        foodLv.setAdapter(foodAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        Toast.makeText(this, item, Toast.LENGTH_LONG).show();
    }
}
