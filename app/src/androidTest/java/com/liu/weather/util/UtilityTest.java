package com.liu.weather.util;

import android.test.AndroidTestCase;

/**
 * Created by liu on 2016/1/11 0011.
 */
public class UtilityTest extends AndroidTestCase{

    public void testInitDB(){
        Utility.initDB(getContext());
    }
}
