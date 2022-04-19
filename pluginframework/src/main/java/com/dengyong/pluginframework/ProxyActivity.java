package com.dengyong.pluginframework;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

/**
  * @Description
  * @Author DengYong
  * @Time 2022/4/14 17:33
  */
public class ProxyActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ProxyActivity","这个是占坑的Activity");
    }

    class a{
        private Activity get(){
            return ProxyActivity.this;
        }
    }
}
