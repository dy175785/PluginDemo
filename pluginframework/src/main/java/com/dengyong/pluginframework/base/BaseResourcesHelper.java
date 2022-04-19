package com.dengyong.pluginframework.base;

import android.content.Context;
import android.content.res.Resources;

import com.dengyong.pluginframework.AppPluginHelper;

import java.io.File;

/**
  * @Description
  * @Author DengYong
  * @Time 2022/4/19 11:11
  */
public class BaseResourcesHelper {
    private Context context;

    private static BaseResourcesHelper HELPER = null;

    private Resources resources = null;

    private BaseResourcesHelper(Context context) {
        this.context = context;
    }

    public static BaseResourcesHelper getInstance(Context context){
        if (HELPER == null){
            synchronized (BaseResourcesHelper.class){
                if (HELPER == null){
                    HELPER = new BaseResourcesHelper(context);
                }
            }
        }
        return HELPER;
    }

    public void setResource(String apkPath){
        if (new File(apkPath).exists()){
            if (resources == null){
                resources = AppPluginHelper.getHelper(context).loadResources(apkPath);
            }
        }
    }

    public Resources getResources(){
        return resources;
    }


}
