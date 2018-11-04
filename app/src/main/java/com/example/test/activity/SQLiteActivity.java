package com.example.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.test.R;
import com.example.test.adapter.FoodAdapter;
import com.example.test.entity.Food;
import com.example.test.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteActivity extends Activity {
    private DBHelper helper;
    private SQLiteDatabase db;
    private  List<Food> foods ;
    private FoodAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sqlite_activity);
        helper = new DBHelper(this,"food",null,1);
        db = helper.getWritableDatabase();

        query();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void addFood(View view) throws Exception{
        EditText name = findViewById(R.id.inputName);
        EditText price = findViewById(R.id.inputPrice);
        String sql="insert into foods (name,price) values(?,?)";
        db.execSQL(sql,new Object[]{name.getText(),price.getText()});

        Thread.sleep(1000);
        Food food = new Food();
        food.setName(name.getText().toString());
        food.setPrice(price.getText().toString());
        foods.add(food);
        adapter.notifyDataSetChanged();
    }

    public void query(){
        String sql = "select * from foods";
        foods = new ArrayList<Food>();
        Food food = null;
        Cursor c = db.rawQuery(sql,null);
        while (c.moveToNext()){
            food = new Food();
            String name=c.getString(c.getColumnIndex("name"));
            String price=c.getString(c.getColumnIndex("price"));
            food.setName(name);
            food.setPrice(price);
            foods.add(food);
        }
        ListView foodLv = findViewById(R.id.foods);
        adapter = new FoodAdapter(this,R.layout.food_view,foods);
        foodLv.setAdapter(adapter);
    }
}
