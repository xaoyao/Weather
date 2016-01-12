package com.liu.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.liu.weather.util.Utility;

/**
 * Created by liu on 2016/1/11 0011.
 */
public class WeatherOpenHelper extends SQLiteOpenHelper{
    private Context context;

    /**
     * 省份建表语句
     */
    private static final String CREATE_PROVINCE="create table Province(" +
            "_id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";
    /**
     * 市级城市建表语句
     */
    private static final String CREATE_CITY="create table City(" +
            "_id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_code text)";
    /**
     * 县级城市建表语句
     */
    private static final String CREATE_COUNTY="create table County(" +
            "_id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_code text)";

    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
