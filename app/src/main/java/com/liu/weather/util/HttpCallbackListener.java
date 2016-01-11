package com.liu.weather.util;

/**
 * Created by liu on 2016/1/11 0011.
 */
public interface HttpCallbackListener {
    /**
     * 请求成功时调用
     * @param response
     */
    public void onFinish(String response);

    /**
     * 请求出错时调用
     * @param e
     */
    public void onError(Exception e);
}
