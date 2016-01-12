package com.liu.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liu.weather.model.City;
import com.liu.weather.model.County;
import com.liu.weather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装数据库操作
 * 通过WeatherDB.getInstance()方法获取实例
 * Created by liu on 2016/1/11 0011.
 */
public class WeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME="weather.db";
    /**
     * 数据库版本
     */
    public static final int VERSION=1;

    private SQLiteDatabase db;

    private static Context context;


    /**
     * 私有化构造方法，单例
     * @param context
     */
    private WeatherDB(Context context){
        WeatherOpenHelper dbHelper=new WeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }

    /**
     * 静态内部类实现单例
     */
    private static class WeatherDBInstance{
        private static final WeatherDB WEATHER_DB=new WeatherDB(context);
    }

    /**
     * 获取WeatherDB的实例
     * @param c
     * @return
     */
    public static WeatherDB getInstance(Context c){
        context =c;
        return WeatherDBInstance.WEATHER_DB;
    }

    /**
     * 将Province实例存储至数据库
     * @param province
     */
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 从数据库读出所有的省份信息
     * @return
     */
    public List<Province> loadProvinces(){
        List<Province> list=new ArrayList<>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            Province province=new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(province);
        }
        return list;
    }

    /**
     * 将City实例存储至数据库
     * @param city
     */
    public void saveCity(City city){
        if(city!=null){
            ContentValues values=new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_code",city.getProvinceCode());
            db.insert("City",null,values);
        }
    }

    /**
     * 从数据库读出某一省份的所有城市
     * @param provinceCode
     * @return
     */
    public List<City> loadCities(String provinceCode){
        List<City> list=new ArrayList<>();
        Cursor cursor=db.query("City",null,"province_code=?",new String[]{provinceCode},null,null,null);
        while(cursor.moveToNext()){
            City city=new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(city);
        }
        return list;
    }

    /**
     * 将County实例存储至数据库
     * @param county
     */
    public void saveCounty(County county){
        if(county!=null){
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_code",county.getCityCode());
            db.insert("County",null,values);
        }
    }

    /**
     * 从数据库读出某一城市下所有的县信息
     * @param cityCode
     * @return
     */
    public List<County> loadCounties(String cityCode){
        List<County> list=new ArrayList<>();
        Cursor cursor=db.query("County",null,"city_code=?",new String[]{cityCode},null,null,null);
        while (cursor.moveToNext()){
            County county=new County();
            county.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            county.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            list.add(county);
        }
        return list;
    }


}
