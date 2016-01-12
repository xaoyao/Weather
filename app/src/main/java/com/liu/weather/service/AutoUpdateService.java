package com.liu.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.liu.weather.receiver.AutoUpdateReceiver;
import com.liu.weather.util.HttpCallbackListener;
import com.liu.weather.util.HttpUtil;
import com.liu.weather.util.Utility;

/**
 * Created by liu on 2016/1/12 0012.
 */
public class AutoUpdateService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int time=1*60*60*1000;  //1小时更新一次
        long triggerAtTime= SystemClock.elapsedRealtime()+time;
        Intent i= new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        SharedPreferences prefs=getSharedPreferences("weatherInfo",MODE_PRIVATE);
        String code=prefs.getString("countyCode", "");
        String address="http://wthrcdn.etouch.cn/weather_mini?citykey="+code;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }
}
