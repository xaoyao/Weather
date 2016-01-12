package com.liu.weather;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.liu.weather.db.WeatherOpenHelper;

/**
 * Created by liu on 2016/1/11 0011.
 */
public class Test extends AndroidTestCase{

    public void testWeatherOpenHelper(){
        WeatherOpenHelper dbHelper=new WeatherOpenHelper(getContext(),"weather.db",null,1);
        SQLiteDatabase sb=dbHelper.getWritableDatabase();
    }
}
