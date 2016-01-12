package com.liu.weather.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liu.weather.R;
import com.liu.weather.service.AutoUpdateService;
import com.liu.weather.util.HttpCallbackListener;
import com.liu.weather.util.HttpUtil;
import com.liu.weather.util.Utility;

public class WeatherActivity extends AppCompatActivity {
    private LinearLayout weatherInfoLayout;
    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    /**
     * 用于天气日期
     */
    private TextView dateText;
    /**
     * 用于当前温度
     */
    private TextView wenduText;
    /**
     * 用于显示天气状况
     */
    private TextView typeText;
    /**
     * 用于显示风向
     */
    private TextView fengxiangText;
    /**
     * 用于显示风力
     */
    private TextView fengliText;
    /**
     * 用于显示最低气温
     */
    private TextView lowText;
    /**
     * 用于显示最高气温
     */
    private TextView highText;
    /**
     * 用于显示是否容易感冒
     */
    private TextView ganmaoText;
    /**
     * 切换城市按钮
     */
    private Button switchCity;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化控件
        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText= (TextView) findViewById(R.id.city_name);
        dateText= (TextView) findViewById(R.id.date);
        wenduText= (TextView) findViewById(R.id.wendu);
        typeText= (TextView) findViewById(R.id.type);
        fengxiangText= (TextView) findViewById(R.id.fengxiang);
        fengliText= (TextView) findViewById(R.id.fengli);
        lowText= (TextView) findViewById(R.id.low);
        highText= (TextView) findViewById(R.id.high);
        ganmaoText= (TextView) findViewById(R.id.ganmao);


        String countyCode=getIntent().getStringExtra("countyCode");
        //countyCode是否为空
        if(!TextUtils.isEmpty(countyCode)){
            dateText.setText("同步中...");
            //不可见
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(countyCode);
            SharedPreferences.Editor editor=getSharedPreferences("weatherInfo",MODE_PRIVATE).edit();
            //保存countyCode方便进行天气更新
            editor.putString("countyCode",countyCode);
            editor.commit();
        }else {
            showWeather();
        }
    }

    /**
     * 按钮点击事件
     * @param view
     */
    public void click(View view){
        switch (view.getId()){
            case R.id.switch_city:
                //选择城市
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
            break;
            case R.id.refresh_weather:
                //手动刷新天气信息
                dateText.setText("同步中...");
                SharedPreferences prefs=getSharedPreferences("weatherInfo",MODE_PRIVATE);
                String code=prefs.getString("countyCode","");
                if(!TextUtils.isEmpty(code)){
                    queryWeatherInfo(code);
                }
                break;

        }
    }

    /**
     * 根据code查询天气信息
     * @param countyCode
     */
    private void queryWeatherInfo(String countyCode){
        String address="http://wthrcdn.etouch.cn/weather_mini?citykey="+countyCode;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

//                Log.d("weather",response);
                //解析服务器返回的JSON数据
                Utility.handleWeatherResponse(WeatherActivity.this,response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dateText.setText("同步失败");
                    }
                });

            }
        });
    }

    private void showWeather(){
        SharedPreferences prefs=getSharedPreferences("weatherInfo",MODE_PRIVATE);
        cityNameText.setText(prefs.getString("cityName",""));
        dateText.setText(prefs.getString("date",""));
        wenduText.setText(prefs.getString("wendu","")+"℃");
        typeText.setText(prefs.getString("type",""));
        fengxiangText.setText(prefs.getString("fengxiang",""));
        fengliText.setText(prefs.getString("fengli",""));
        lowText.setText(prefs.getString("low",""));
        highText.setText(prefs.getString("high",""));
        ganmaoText.setText(prefs.getString("ganmao", ""));
        cityNameText.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        //启动自动更新服务
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
