package com.liu.weather.util;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;

import com.liu.weather.db.WeatherDB;
import com.liu.weather.model.City;
import com.liu.weather.model.County;
import com.liu.weather.model.Province;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
}
