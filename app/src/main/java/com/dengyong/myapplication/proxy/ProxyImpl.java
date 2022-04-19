package com.dengyong.myapplication.proxy;

import android.util.Log;

/**
  * @Description
  * @Author DengYong
  * @Time 2022/4/14 10:49
  */
public class ProxyImpl implements ProxyInterface{
    @Override
    public void getLog(String message) {
        Log.e("ProxyImpl",message);
    }
}
