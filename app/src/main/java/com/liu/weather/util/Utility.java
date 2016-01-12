package com.liu.weather.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

import com.liu.weather.db.WeatherDB;
import com.liu.weather.model.City;
import com.liu.weather.model.County;
import com.liu.weather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liu on 2016/1/11 0011.
 */
public class Utility {

    /**
     * 解析本地xml(assets/china.xml)文件，初始化数据库
     */
    public static void initDB(Context context){
        Province province=null;
        City city=null;
        County county=null;
        try {
            InputStream is=context.getResources().getAssets().open("china.xml");
            XmlPullParser xp= Xml.newPullParser();
            //设置输入流
            xp.setInput(is,"UTF-8");
            int type=xp.getEventType();
            while (type!=XmlPullParser.END_DOCUMENT){
                switch (type){
                    case XmlPullParser.START_TAG:
                        //province节点
                        if("province".equals(xp.getName())){
                            province=new Province();
                            int count=xp.getAttributeCount();
                            for(int i=0;i<count;i++){
                                //设置provinceCode
                                if("id".equals(xp.getAttributeName(i))){
                                    province.setProvinceCode(xp.getAttributeValue(i));
                                }
                                //设置provinceName
                                else if("name".equals(xp.getAttributeName(i))){
                                    province.setProvinceName(xp.getAttributeValue(i));
                                }
                            }
                        }
                        //city节点
                        else if("city".equals(xp.getName())){
                            city=new City();
                            city.setProvinceCode(province.getProvinceCode());
                            int count =xp.getAttributeCount();
                            for(int i=0;i<count;i++){
                                if("id".equals(xp.getAttributeName(i))){
                                    city.setCityCode(xp.getAttributeValue(i));
                                }else if("name".equals(xp.getAttributeName(i))){
                                    city.setCityName(xp.getAttributeValue(i));
                                }
                            }
                        }else if("county".equals(xp.getName())){
                            county=new County();
                            county.setCityCode(city.getCityCode());
                            int count=xp.getAttributeCount();
                            for(int i=0;i<count;i++){
                                if("name".equals(xp.getAttributeName(i))){
                                    county.setCountyName(xp.getAttributeValue(i));
                                }else if("weatherCode".equals(xp.getAttributeName(i))){
                                    county.setCountyCode(xp.getAttributeValue(i));
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        //存储数据
                        if("county".equals(xp.getName())){
                            WeatherDB.getInstance(context).saveCounty(county);
                        }else if("city".equals(xp.getName())){
                            WeatherDB.getInstance(context).saveCity(city);
                        }else if("province".equals(xp.getName())){
                            WeatherDB.getInstance(context).saveProvince(province);
                        }
                        break;
                }
                type=xp.next();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析服务器返回的JSON数据，并将解析的数据存储到本地
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject object=new JSONObject(response);
            JSONObject weatherInfo=object.getJSONObject("data");
            String wendu=weatherInfo.getString("wendu");
            String ganmao=weatherInfo.getString("ganmao");
            JSONArray array=weatherInfo.getJSONArray("forecast");
            JSONObject info= (JSONObject) array.get(0);
            String fengxiang=info.getString("fengxiang");
            String fengli=info.getString("fengli");
            String high=info.getString("high");
            String type=info.getString("type");
            String low=info.getString("low");
            String date=info.getString("date");
            String cityName=weatherInfo.getString("city");
            saveWeatherInfo(context,wendu,ganmao,fengxiang,fengli,high,type,low,date,cityName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的信息存储到SharedPrefences文件中
     */
    public static void saveWeatherInfo(Context context , String wendu,String ganmao,String fengxiang,
                                       String fengli,String high,String type,String low,
                                       String date,String cityName){
        SharedPreferences.Editor editor= context.getSharedPreferences("weatherInfo",Context.MODE_PRIVATE).edit();
        //存储是否选中城市，选中城市，下次直接显示天气信息
        editor.putBoolean("city_selected",true);

        editor.putString("wendu", wendu);
        editor.putString("ganmao", ganmao);
        editor.putString("fengxiang", fengxiang);
        editor.putString("fengli", fengli);
        editor.putString("high", high);
        editor.putString("type", type);
        editor.putString("low",low);
        editor.putString("date",date);
        editor.putString("cityName",cityName);
        editor.commit();

    }
}
