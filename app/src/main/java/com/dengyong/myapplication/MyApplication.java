package com.dengyong.myapplication;

import android.app.Application;

import com.dengyong.pluginframework.AppPluginHelper;
import com.dengyong.pluginframework.ProxyActivity;

/**
 * @Description
 * @Author DengYong
 * @Time 2022/4/14 14:15
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppPluginHelper.getHelper(this).hookStartActivity(ProxyActivity.class).hookStartServices(MyService.class).hookLaunchActivity();
    }
}
