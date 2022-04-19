package com.dengyong.pluginapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
  * @Description
  * @Author DengYong
  * @Time 2022/4/12 10:25
  */
public class ToastUtils {

    public void toast(Context context){
        Toast.makeText(context, "加载插件中的类文件", Toast.LENGTH_SHORT).show();
    }

}
