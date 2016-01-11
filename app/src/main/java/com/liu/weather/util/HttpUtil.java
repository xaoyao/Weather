package com.liu.weather.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by liu on 2016/1/11 0011.
 */
public class HttpUtil {

    /**
     * 发送HTTP请求，GET方式
     * @param address   http地址
     * @param listener  回调
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=new URL(address);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    //获取输入流
                    InputStream is=connection.getInputStream();
                    ByteArrayOutputStream bos=new ByteArrayOutputStream();
                    byte[] b=new byte[1024];
                    int len=0;
                    if((len=is.read(b))!=-1){
                        bos.write(b,0,len);
                    }
                    //获取服务器响应的信息
                    String response=new String(bos.toByteArray());
                    bos.close();

                    if(listener!=null){
                        //回调onFinish()方法
                        listener.onFinish(response);
                    }

                } catch (Exception e) {
                    if(listener!=null){
                        //回调onError()方法
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }
}
