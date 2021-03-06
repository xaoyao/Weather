package com.liu.weather.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.liu.weather.R;
import com.liu.weather.db.WeatherDB;
import com.liu.weather.model.City;
import com.liu.weather.model.County;
import com.liu.weather.model.Province;
import com.liu.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList=new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        //判断是否为第一次启动，第一次启动初始化数据库
        SharedPreferences pref=getSharedPreferences("data", MODE_PRIVATE);
        boolean isFirst=pref.getBoolean("isFirst", true);
        if(isFirst){
            //初始化数据库
            Utility.initDB(this);
            SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putBoolean("isFirst",false);
            editor.commit();
        }
        //是否是从天气信息界面跳转过来的
        boolean isFromWeatherActivity =getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences preference=getSharedPreferences("weatherInfo",MODE_PRIVATE);
        //判断是否已经选中过城市，若选中，且不是从WeatherActivity跳转过来的则直接显示天气信息
        if(preference.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        listView= (ListView) findViewById(R.id.list_view);
        titleText= (TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        weatherDB=WeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    //启动WeatherActivity,显示天气信息
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("countyCode",countyCode);
                    startActivity(intent);
                }
            }
        });

        queryProvinces();   //加载省份数据
    }

    /**
     * 查询全国所有的省
     */
    private void queryProvinces(){
        provinceList=weatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }
    }

    /**
     * 查询选中的省中所有的市
     */
    private void queryCities(){
        cityList=weatherDB.loadCities(selectedProvince.getProvinceCode());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }

    }

    /**
     * 查询选中市中所有的县
     */
    private void queryCounties(){
        countyList=weatherDB.loadCounties(selectedCity.getCityCode());
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }

    }


    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }
}
