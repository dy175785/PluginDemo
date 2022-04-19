package com.dengyong.pluginframework.base;

import android.app.Activity;
import android.content.res.Resources;

import com.dengyong.pluginframework.AppPluginHelper;

/**
 * @Description
 * @Author DengYong
 * @Time 2022/4/18 17:06
 */
public abstract class BaseActivity extends Activity {

    public abstract void loadResource(String apkPath);

    @Override
    public Resources getResources() {
        if (this.getApplicationContext() != null) {
            Resources resources = BaseResourcesHelper.getInstance(this.getApplicationContext()).getResources();
            if (resources != null){
                return resources;
            }
            return super.getResources();
        }
        return super.getResources();
    }
}
